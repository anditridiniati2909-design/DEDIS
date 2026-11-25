<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Child;
use App\Models\Measurement;
use Illuminate\Http\Request;
use Carbon\Carbon;

class ChildController extends Controller
{
    /**
     * GET /api/v1/children
     * Mengambil daftar balita. Jika ada parameter ?mother_name=..., hanya mengembalikan anak dari ibu tersebut.
     */
    public function index(Request $request)
    {
        $query = Child::with(['latestMeasurement', 'measurements']);
        if ($request->has('mother_name') && !empty($request->query('mother_name'))) {
            $parent = trim($request->query('mother_name'));
            $clean = trim(str_ireplace(['ibu ', 'bapak ', 'pak ', 'ayah ', 'mama ', 'papa '], '', $parent));
            $query->where(function($q) use ($parent, $clean) {
                $q->where('mother_name', $parent)
                  ->orWhereRaw('LOWER(TRIM(mother_name)) = ?', [strtolower($parent)]);
                if (!empty($clean)) {
                    $q->orWhereRaw('LOWER(TRIM(REPLACE(REPLACE(REPLACE(mother_name, "Ibu ", ""), "Bapak ", ""), "Ayah ", ""))) = ?', [strtolower($clean)]);
                }
            });
        }
        $children = $query->get();
        return response()->json($children, 200);
    }

    /**
     * GET /api/v1/children/{id}
     * Mengambil detail balita lengkap dengan riwayat pengukuran bulanan
     */
    public function show($id)
    {
        $child = Child::with(['latestMeasurement', 'measurements'])->find($id);

        if (!$child) {
            return response()->json(['message' => 'Data balita tidak ditemukan'], 404);
        }

        return response()->json($child, 200);
    }

    /**
     * POST /api/v1/children
     * Mendaftarkan balita baru ke sistem Posyandu
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'card_code' => 'nullable|string',
            'name' => 'required|string|max:255',
            'gender' => 'nullable|string',
            'birth_date' => 'nullable',
            'mother_name' => 'nullable|string|max:255',
            'dusun' => 'nullable|string',
            'is_mbg_recipient' => 'nullable|boolean',
        ]);

        // Parsing tanggal lahir cerdas
        $birthDate = Carbon::now()->subMonths(12)->format('Y-m-d');
        if (!empty($validated['birth_date'])) {
            try {
                $birthDate = Carbon::parse($validated['birth_date'])->format('Y-m-d');
            } catch (\Exception $e) {
                // default jika format bebas
            }
        }

        $cardCode = $validated['card_code'] ?? null;
        if (empty($cardCode)) {
            $maxId = (int) (Child::max('id') ?? 0);
            do {
                $maxId++;
                $cardCode = 'DEDIS-' . str_pad($maxId, 3, '0', STR_PAD_LEFT);
            } while (Child::where('card_code', $cardCode)->exists());
        }

        $child = Child::create([
            'card_code' => $cardCode,
            'name' => $validated['name'],
            'gender' => (!empty($validated['gender']) && strtoupper($validated['gender'])[0] == 'L') ? 'L' : 'P',
            'birth_date' => $birthDate,
            'mother_name' => $validated['mother_name'] ?? 'Bapak/Ibu Balita',
            'dusun' => $validated['dusun'] ?? 'Dusun Bontocinde',
            'is_mbg_recipient' => $validated['is_mbg_recipient'] ?? false,
        ]);

        return response()->json([
            'message' => 'Data balita berhasil ditambahkan',
            'child' => $child
        ], 201);
    }

    /**
     * POST /api/v1/children/{id}/measurements
     * Kader mencatat pengukuran tinggi & berat badan baru
     */
    public function storeMeasurement(Request $request, $id)
    {
        $child = Child::find($id);
        if (!$child) {
            return response()->json(['message' => 'Data balita tidak ditemukan'], 404);
        }

        $validated = $request->validate([
            'weight_kg' => 'required|numeric|min:1|max:40',
            'height_cm' => 'required|numeric|min:30|max:150',
            'measurement_method' => 'required|in:standing,recumbent',
            'measurement_date' => 'required|date',
            'head_circumference_cm' => 'nullable|numeric',
            'kader_name' => 'nullable|string',
        ]);

        // Hitung usia balita saat pengukuran (dalam bulan)
        $measDate = Carbon::parse($validated['measurement_date']);
        $birthDate = Carbon::parse($child->birth_date);
        $ageInMonths = (int) $birthDate->diffInMonths($measDate);

        // Standar Estimasi Antropometri Kemenkes RI untuk deteksi awal stunting
        $expectedHeight = 50 + ($ageInMonths * 1.2); // Standar pendekatan tinggi balita
        $heightDiff = $validated['height_cm'] - $expectedHeight;

        $statusHfa = 'normal';
        $needsAttention = false;
        $attentionReason = null;

        if ($heightDiff < -6) {
            $statusHfa = 'sangat_pendek';
            $needsAttention = true;
            $attentionReason = 'Tinggi badan balita berada jauh di bawah garis standar usia (Risiko Tinggi Stunting).';
        } elseif ($heightDiff < -3) {
            $statusHfa = 'pendek';
            $needsAttention = true;
            $attentionReason = 'Tinggi badan balita di bawah kurva normal. Perlu intervensi gizi protein hewani.';
        }

        $kaderName = !empty($validated['kader_name']) ? $validated['kader_name'] : 'Kader Posyandu (Petugas Meja 2)';
        $recordedNote = "Diinput oleh: {$kaderName}";
        if ($needsAttention && $attentionReason) {
            $recordedNote = "{$attentionReason} • {$recordedNote}";
        }

        $measurement = Measurement::create([
            'child_id' => $child->id,
            'measurement_date' => $validated['measurement_date'],
            'age_in_months' => $ageInMonths,
            'weight_kg' => $validated['weight_kg'],
            'height_cm' => $validated['height_cm'],
            'head_circumference_cm' => $validated['head_circumference_cm'] ?? null,
            'measurement_method' => $validated['measurement_method'],
            'status_hfa' => $statusHfa,
            'status_wfa' => 'normal',
            'status_wfh' => 'gizi_baik',
            'needs_attention' => $needsAttention,
            'attention_reason' => $recordedNote,
        ]);

        return response()->json([
            'message' => 'Pengukuran berhasil dicatat oleh Kader Posyandu',
            'measurement' => $measurement,
            'warnings' => $needsAttention ? [$attentionReason] : null
        ], 201);
    }

    /**
     * PUT /api/v1/children/{id}
     * Mengubah / Update data balita
     */
    public function update(Request $request, $id)
    {
        $child = is_numeric($id) ? Child::find($id) : Child::where('name', $id)->first();
        if (!$child) {
            return response()->json(['message' => 'Data balita tidak ditemukan'], 404);
        }

        $validated = $request->validate([
            'name' => 'sometimes|required|string|max:255',
            'gender' => 'nullable|string',
            'birth_date' => 'nullable',
            'mother_name' => 'nullable|string|max:255',
            'dusun' => 'nullable|string',
            'is_mbg_recipient' => 'nullable|boolean',
        ]);

        $child->update($validated);

        return response()->json([
            'message' => 'Data balita berhasil diperbarui',
            'child' => $child
        ], 200);
    }

    /**
     * DELETE /api/v1/children/{id}
     * Menghapus data balita
     */
    public function destroy($id)
    {
        $child = is_numeric($id) ? Child::find($id) : Child::where('name', $id)->first();
        if (!$child) {
            return response()->json(['message' => 'Data balita tidak ditemukan'], 404);
        }

        $child->delete();
        return response()->json(['message' => 'Data balita berhasil dihapus'], 200);
    }
}
