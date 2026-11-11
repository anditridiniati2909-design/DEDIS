<?php

namespace Database\Seeders;

use App\Enums\MeasurementMethod;
use App\Models\Child;
use App\Models\Measurement;
use App\Models\User;
use App\Services\GrowthCalculationService;
use Carbon\Carbon;
use Illuminate\Database\Seeder;

class TestMeasurementSeeder extends Seeder
{
    public function run(): void
    {
        $child = Child::where('name', 'Aisyah Putri')->first();
        $kader = User::where('email', 'sari@posyandu.id')->first();

        if (! $child || ! $kader) {
            $this->command?->error('Jalankan DemoDataSeeder terlebih dahulu.');
            return;
        }

        $service = app(GrowthCalculationService::class);
        $growth = $service->calculate(
            gender: $child->gender,
            ageInMonths: $child->ageInMonths(),
            weightKg: 10.4,
            heightCm: 80.0,
            heightMethod: MeasurementMethod::STANDING
        );

        // Gunakan hasil perhitungan backend (dengan nilai referensi realistis jika tabel WHO belum terisi)
        $hfaZ = $growth['hfa_zscore'] ?? -2.00;
        $hfaStatus = $growth['hfa_status'] !== 'belum_bisa_dinilai' ? $growth['hfa_status'] : 'pendek';
        $wfaZ = $growth['wfa_zscore'] ?? -0.30;
        $wfaStatus = $growth['wfa_status'] !== 'belum_bisa_dinilai' ? $growth['wfa_status'] : 'gizi_baik';
        $wfhZ = $growth['wfh_zscore'] ?? -0.50;
        $wfhStatus = $growth['wfh_status'] !== 'belum_bisa_dinilai' ? $growth['wfh_status'] : 'gizi_baik';

        $measurement = Measurement::create([
            'child_id'            => $child->id,
            'recorded_by'         => $kader->id,
            'measurement_date'    => Carbon::now()->toDateString(),
            'age_in_months'       => $child->ageInMonths(),
            'weight_kg'           => 10.40,
            'height_cm'           => 80.00,
            'head_circumference_cm' => 46.50,
            'measurement_method'  => MeasurementMethod::STANDING,
            'z_score_hfa'         => $hfaZ,
            'status_hfa'          => $hfaStatus,
            'z_score_wfa'         => $wfaZ,
            'status_wfa'          => $wfaStatus,
            'z_score_wfh'         => $wfhZ,
            'status_wfh'          => $wfhStatus,
            'needs_attention'     => true,
            'attention_reason'    => 'Tinggi badan di bawah batas normal WHO (-2 SD)',
        ]);

        if ($this->command) {
            $this->command->info("Pengukuran Aisyah Putri tersimpan!");
            $this->command->info("- ID: {$measurement->id}");
            $this->command->info("- BB: {$measurement->weight_kg} kg, TB: {$measurement->height_cm} cm (Berdiri)");
            $this->command->info("- Z-Score TB/U: {$measurement->z_score_hfa} ({$measurement->status_hfa})");
            $this->command->info("- Z-Score BB/U: {$measurement->z_score_wfa} ({$measurement->status_wfa})");
            $this->command->info("- Z-Score BB/TB: {$measurement->z_score_wfh} ({$measurement->status_wfh})");
        }
    }
}

