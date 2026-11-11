<?php

namespace App\Services;

use App\Models\Child;
use App\Models\GrowthFollowUp;
use App\Models\Measurement;

class EarlyWarningService
{
    /**
     * Periksa apakah pengukuran baru memicu peringatan dini stunting.
     * Mengembalikan array peringatan (bisa kosong).
     *
     * Trigger (sesuai spesifikasi DEDIS):
     * 1. Berat badan turun dari bulan lalu
     * 2. BB tidak naik selama 2 bulan berturut-turut
     * 3. Z-score TB/U turun ≥ 0.5 SD dari pengukuran sebelumnya
     * 4. Z-score TB/U mendekati atau di bawah -1.5 SD (menuju -2)
     */
    public function check(Measurement $current, Child $child): array
    {
        $warnings = [];

        // Ambil 2 pengukuran terakhir sebelum yang ini (untuk trend BB)
        $previous = Measurement::where('child_id', $child->id)
            ->where('id', '!=', $current->id)
            ->whereNotNull('weight_kg')
            ->orderByDesc('measured_on')
            ->limit(2)
            ->get();

        $prev1 = $previous->first();   // paling baru sebelum current
        $prev2 = $previous->get(1);    // satu sebelumnya lagi

        // 1. Berat turun dari bulan lalu
        if ($prev1 && $current->weight_kg !== null) {
            if ($current->weight_kg < $prev1->weight_kg) {
                $warnings[] = [
                    'type'   => 'berat_turun',
                    'detail' => [
                        'current_weight' => $current->weight_kg,
                        'prev_weight'    => $prev1->weight_kg,
                        'diff_kg'        => round($current->weight_kg - $prev1->weight_kg, 2),
                        'prev_date'      => $prev1->measured_on->toDateString(),
                    ],
                ];
            }
        }

        // 2. BB tidak naik 2 bulan berturut-turut
        if ($prev1 && $prev2 && $current->weight_kg !== null) {
            $noGain1 = $current->weight_kg <= $prev1->weight_kg;
            $noGain2 = $prev1->weight_kg <= ($prev2->weight_kg ?? $prev1->weight_kg);
            if ($noGain1 && $noGain2) {
                $warnings[] = [
                    'type'   => 'tidak_naik_2bln',
                    'detail' => [
                        'weights' => [
                            $prev2->measured_on->toDateString() => $prev2->weight_kg,
                            $prev1->measured_on->toDateString() => $prev1->weight_kg,
                            $current->measured_on->toDateString() => $current->weight_kg,
                        ],
                    ],
                ];
            }
        }

        // 3 & 4. Trigger berbasis Z-score TB/U
        if ($current->hfa_zscore !== null) {

            // Trigger 4: mendekati -2 SD (threshold peringatan dini: -1.5)
            if ($current->hfa_zscore <= -1.5) {
                $warnings[] = [
                    'type'   => 'mendekati_minus2',
                    'detail' => [
                        'hfa_zscore' => $current->hfa_zscore,
                        'threshold'  => -1.5,
                    ],
                ];
            }

            // Trigger 3: drop Z-score ≥ 0.5 SD
            $prevWithHfa = Measurement::where('child_id', $child->id)
                ->where('id', '!=', $current->id)
                ->whereNotNull('hfa_zscore')
                ->orderByDesc('measured_on')
                ->first();

            if ($prevWithHfa) {
                $drop = $prevWithHfa->hfa_zscore - $current->hfa_zscore;
                if ($drop >= 0.5) {
                    $warnings[] = [
                        'type'   => 'zscore_drop',
                        'detail' => [
                            'prev_zscore'    => $prevWithHfa->hfa_zscore,
                            'current_zscore' => $current->hfa_zscore,
                            'drop'           => round($drop, 3),
                            'prev_date'      => $prevWithHfa->measured_on->toDateString(),
                        ],
                    ];
                }
            }
        }

        return $warnings;
    }

    /**
     * Simpan peringatan sebagai GrowthFollowUp records.
     */
    public function saveWarnings(array $warnings, Measurement $measurement, int $createdBy): void
    {
        foreach ($warnings as $warning) {
            GrowthFollowUp::create([
                'measurement_id' => $measurement->id,
                'child_id'       => $measurement->child_id,
                'created_by'     => $createdBy,
                'type'           => $warning['type'],
                'detail'         => $warning['detail'],
                'action_date'    => now()->addDays(7)->toDateString(),
            ]);
        }
    }
}

