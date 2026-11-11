<?php

namespace App\Http\Controllers\Api;

use App\Enums\MeasurementMethod;
use App\Http\Controllers\Controller;
use App\Http\Requests\Measurement\StoreMeasurementRequest;
use App\Http\Resources\MeasurementResource;
use App\Models\Child;
use App\Models\ChildGuardianLink;
use App\Models\Measurement;
use App\Services\AuditLogService;
use App\Services\EarlyWarningService;
use App\Services\GrowthCalculationService;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class MeasurementController extends Controller
{
    public function __construct(
        private readonly GrowthCalculationService $growthService,
        private readonly EarlyWarningService $warningService,
    ) {}

    // ==========================================
    // DAFTAR PENGUKURAN PER ANAK
    // ==========================================

    public function index(Request $request, Child $child): JsonResponse
    {
        $this->authorizeChildAccess($request, $child);

        $measurements = Measurement::where('child_id', $child->id)
            ->orderByDesc('measured_on')
            ->get();

        return response()->json(MeasurementResource::collection($measurements));
    }

    // ==========================================
    // CATAT PENGUKURAN BARU (append-only)
    // ==========================================

    public function store(StoreMeasurementRequest $request, Child $child): JsonResponse
    {
        $this->authorizeChildAccess($request, $child);

        $data = $request->validated();
        
        $methodRaw = $data['measurement_method'] ?? $data['height_method'] ?? null;
        $method = $methodRaw ? (MeasurementMethod::tryFrom($methodRaw) ?? MeasurementMethod::RECUMBENT) : MeasurementMethod::RECUMBENT;

        // Hitung Z-score dan status gizi
        $growthResult = $this->growthService->calculate(
            gender:       $child->gender,
            ageInMonths:  $child->ageInMonths(),
            weightKg:     isset($data['weight_kg']) ? (float) $data['weight_kg'] : null,
            heightCm:     isset($data['height_cm']) ? (float) $data['height_cm'] : null,
            heightMethod: $method,
        );

        $measurementDate = $data['measurement_date'] ?? $data['measured_on'] ?? now()->toDateString();

        $measurement = Measurement::create([
            'child_id'               => $child->id,
            'recorded_by'            => $request->user()->id,
            'measurement_date'       => $measurementDate,
            'age_in_months'          => $child->ageInMonths(),
            'weight_kg'              => $data['weight_kg'] ?? null,
            'height_cm'              => $data['height_cm'] ?? null,
            'head_circumference_cm'  => $data['head_circumference_cm'] ?? $data['head_circ_cm'] ?? null,
            'measurement_method'     => $method,
            'correction_applied'     => ($growthResult['height_corrected_cm'] ?? null) !== ($data['height_cm'] ?? null),
            'z_score_hfa'            => $growthResult['hfa_zscore'],
            'status_hfa'             => $growthResult['hfa_status'],
            'z_score_wfa'            => $growthResult['wfa_zscore'],
            'status_wfa'             => $growthResult['wfa_status'],
            'z_score_wfh'            => $growthResult['wfh_zscore'],
            'status_wfh'             => $growthResult['wfh_status'],
            'needs_attention'        => ($growthResult['hfa_zscore'] !== null && $growthResult['hfa_zscore'] < -2),
        ]);

        AuditLogService::log('create', $measurement, newValues: $measurement->toArray());

        // Periksa peringatan dini
        $warnings = $this->warningService->check($measurement, $child);
        if (! empty($warnings)) {
            $this->warningService->saveWarnings($warnings, $measurement, $request->user()->id);
            $measurement->update([
                'needs_attention' => true,
                'attention_reason' => implode('; ', array_column($warnings, 'message')),
            ]);
        }

        return response()->json([
            'message'      => 'Pengukuran berhasil dicatat.',
            'measurement'  => new MeasurementResource($measurement),
            'warnings'     => $warnings,
        ], 201);
    }

    // ==========================================
    // DETAIL PENGUKURAN
    // ==========================================

    public function show(Request $request, Child $child, Measurement $measurement): JsonResponse
    {
        $this->authorizeChildAccess($request, $child);

        if ($measurement->child_id !== $child->id) {
            abort(403);
        }

        return response()->json(new MeasurementResource($measurement));
    }

    // ==========================================
    // Helper: validasi akses IDOR
    // ==========================================

    private function authorizeChildAccess(Request $request, Child $child): void
    {
        $user = $request->user();
        if ($user->isOrangTua()) {
            $linked = ChildGuardianLink::where('child_id', $child->id)
                ->where('user_id', $user->id)
                ->exists();
            if (! $linked) {
                abort(403, 'Akses ditolak.');
            }
        }
    }
}

