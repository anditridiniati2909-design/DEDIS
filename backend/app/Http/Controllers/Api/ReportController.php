<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Child;
use App\Models\Measurement;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class ReportController extends Controller
{
    // ==========================================
    // RINGKASAN BULANAN (untuk laporan kader)
    // ==========================================

    public function monthlySummary(Request $request): JsonResponse
    {
        $request->validate([
            'year'  => ['required', 'integer', 'min:2020', 'max:' . now()->year],
            'month' => ['required', 'integer', 'min:1', 'max:12'],
        ]);

        $year  = (int) $request->year;
        $month = (int) $request->month;

        // Total anak terdaftar
        $totalChildren = Child::whereNull('deleted_at')->count();

        // Anak yang diukur bulan ini
        $measuredThisMonth = Measurement::whereYear('measured_on', $year)
            ->whereMonth('measured_on', $month)
            ->distinct('child_id')
            ->count('child_id');

        // Distribusi status TB/U bulan ini (pengukuran terakhir per anak)
        $latestPerChild = Measurement::whereYear('measured_on', $year)
            ->whereMonth('measured_on', $month)
            ->whereNotNull('hfa_zscore')
            ->select('child_id', DB::raw('MAX(measured_on) as last_date'))
            ->groupBy('child_id');

        $hfaDistribution = Measurement::joinSub($latestPerChild, 'latest', function ($join) {
            $join->on('measurements.child_id', '=', 'latest.child_id')
                 ->on('measurements.measured_on', '=', 'latest.last_date');
        })
        ->select('hfa_status', DB::raw('COUNT(*) as total'))
        ->groupBy('hfa_status')
        ->pluck('total', 'hfa_status');

        // Peringatan dini aktif (belum diselesaikan)
        $activeWarnings = DB::table('growth_follow_ups')
            ->whereNull('resolved_at')
            ->whereNull('deleted_at')
            ->count();

        return response()->json([
            'period' => sprintf('%04d-%02d', $year, $month),
            'summary' => [
                'total_children'      => $totalChildren,
                'measured_this_month' => $measuredThisMonth,
                'not_measured'        => $totalChildren - $measuredThisMonth,
                'coverage_pct'        => $totalChildren > 0
                    ? round($measuredThisMonth / $totalChildren * 100, 1)
                    : 0,
            ],
            'hfa_distribution' => [
                'sangat_pendek'   => $hfaDistribution['sangat_pendek'] ?? 0,
                'pendek'          => $hfaDistribution['pendek'] ?? 0,
                'normal'          => $hfaDistribution['normal'] ?? 0,
                'tinggi'          => $hfaDistribution['tinggi'] ?? 0,
                'belum_dinilai'   => $measuredThisMonth - $hfaDistribution->sum(),
            ],
            'active_warnings' => $activeWarnings,
        ]);
    }

    // ==========================================
    // TREN PERTUMBUHAN INDIVIDU
    // ==========================================

    public function childGrowthTrend(Request $request, Child $child): JsonResponse
    {
        $measurements = Measurement::where('child_id', $child->id)
            ->orderBy('measured_on')
            ->select([
                'measured_on', 'age_in_months',
                'weight_kg', 'height_corrected_cm',
                'hfa_zscore', 'hfa_status',
                'wfa_zscore', 'wfa_status',
                'wfh_zscore', 'wfh_status',
            ])
            ->get();

        return response()->json([
            'child'        => [
                'id'         => $child->id,
                'name'       => $child->name,
                'birth_date' => $child->birth_date,
                'gender'     => $child->gender,
            ],
            'measurements' => $measurements,
        ]);
    }
}

