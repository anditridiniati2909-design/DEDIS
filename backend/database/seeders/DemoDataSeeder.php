<?php

namespace Database\Seeders;

use App\Enums\Gender;
use App\Enums\MeasurementMethod;
use App\Enums\UserRole;
use App\Models\Child;
use App\Models\ChildGuardianLink;
use App\Models\Measurement;
use App\Models\User;
use Carbon\Carbon;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class DemoDataSeeder extends Seeder
{
    public function run(): void
    {
        // 1. Akun Kader
        $kader = User::updateOrCreate(
            ['email' => 'sari@posyandu.id'],
            [
                'name'                  => 'Kader Sari',
                'password'              => Hash::make('123456'),
                'role'                  => UserRole::KADER,
                'phone_number'          => '081234567890',
                'dusun'                 => 'Borongloe',
                'privacy_consent_at'    => Carbon::now(),
                'is_active'             => true,
                'failed_login_attempts' => 0,
            ]
        );

        // 2. Akun Orang Tua
        $orangTua = User::updateOrCreate(
            ['email' => 'nuraini@posyandu.id'],
            [
                'name'                  => 'Nur Aini',
                'password'              => Hash::make('123456'),
                'role'                  => UserRole::ORANG_TUA,
                'phone_number'          => '081298765432',
                'dusun'                 => 'Borongloe',
                'privacy_consent_at'    => Carbon::now(),
                'is_active'             => true,
                'failed_login_attempts' => 0,
            ]
        );

        // 3. Anak 1: Aisyah Putri (24 bulan)
        $aisyah = Child::updateOrCreate(
            ['card_code' => 'DEDIS-2024-0012'],
            [
                'name'             => 'Aisyah Putri',
                'birth_date'       => Carbon::now()->subMonths(24)->toDateString(),
                'gender'           => Gender::PEREMPUAN,
                'mother_name'      => 'Nur Aini',
                'hamlet'           => 'Borongloe',
                'is_mbg_recipient' => false,
                'is_archived'      => false,
                'created_by'       => $kader->id,
            ]
        );

        // 4. Anak 2: Nabil Pratama (8 bulan)
        $nabil = Child::updateOrCreate(
            ['card_code' => 'DEDIS-2026-0034'],
            [
                'name'             => 'Nabil Pratama',
                'birth_date'       => Carbon::now()->subMonths(8)->toDateString(),
                'gender'           => Gender::LAKI_LAKI,
                'mother_name'      => 'Nur Aini',
                'hamlet'           => 'Borongloe',
                'is_mbg_recipient' => true,
                'is_archived'      => false,
                'created_by'       => $kader->id,
            ]
        );

        // 5. Tautan Orang Tua <-> Anak (ChildGuardianLink)
        ChildGuardianLink::updateOrCreate(
            ['child_id' => $aisyah->id, 'user_id' => $orangTua->id],
            ['is_linked' => true, 'linked_at' => Carbon::now()]
        );

        ChildGuardianLink::updateOrCreate(
            ['child_id' => $nabil->id, 'user_id' => $orangTua->id],
            ['is_linked' => true, 'linked_at' => Carbon::now()]
        );

        // 6. Tiga Pengukuran untuk Aisyah Putri
        // Bulan 22: 78.0 cm, 9.4 kg
        Measurement::updateOrCreate(
            [
                'child_id'         => $aisyah->id,
                'measurement_date' => Carbon::now()->subMonths(2)->toDateString(),
            ],
            [
                'recorded_by'           => $kader->id,
                'age_in_months'         => 22,
                'weight_kg'             => 9.40,
                'height_cm'             => 78.00,
                'head_circumference_cm' => 45.80,
                'measurement_method'    => MeasurementMethod::RECUMBENT,
                'z_score_hfa'           => -2.50,
                'z_score_wfa'           => -0.80,
                'z_score_wfh'           => -0.60,
                'status_hfa'            => 'pendek',
                'status_wfa'            => 'gizi_baik',
                'status_wfh'            => 'gizi_baik',
                'needs_attention'       => true,
                'attention_reason'      => 'Tinggi badan di bawah batas normal WHO (-2 SD)',
            ]
        );

        // Bulan 23: 78.6 cm, 9.8 kg
        Measurement::updateOrCreate(
            [
                'child_id'         => $aisyah->id,
                'measurement_date' => Carbon::now()->subMonths(1)->toDateString(),
            ],
            [
                'recorded_by'           => $kader->id,
                'age_in_months'         => 23,
                'weight_kg'             => 9.80,
                'height_cm'             => 78.60,
                'head_circumference_cm' => 46.00,
                'measurement_method'    => MeasurementMethod::RECUMBENT,
                'z_score_hfa'           => -2.30,
                'z_score_wfa'           => -0.60,
                'z_score_wfh'           => -0.50,
                'status_hfa'            => 'pendek',
                'status_wfa'            => 'gizi_baik',
                'status_wfh'            => 'gizi_baik',
                'needs_attention'       => true,
                'attention_reason'      => 'Pertumbuhan mulai membaik namun tetap perlu pengawasan',
            ]
        );

        // Bulan 24 (Terbaru): 79.2 cm, 10.2 kg
        Measurement::updateOrCreate(
            [
                'child_id'         => $aisyah->id,
                'measurement_date' => Carbon::now()->toDateString(),
            ],
            [
                'recorded_by'           => $kader->id,
                'age_in_months'         => 24,
                'weight_kg'             => 10.20,
                'height_cm'             => 79.20,
                'head_circumference_cm' => 46.20,
                'measurement_method'    => MeasurementMethod::STANDING,
                'z_score_hfa'           => -2.10,
                'z_score_wfa'           => -0.40,
                'z_score_wfh'           => -0.60,
                'status_hfa'            => 'pendek',
                'status_wfa'            => 'gizi_baik',
                'status_wfh'            => 'gizi_baik',
                'needs_attention'       => true,
                'attention_reason'      => 'Perlu pemantauan lanjutan bersama bidan',
            ]
        );

        // 7. Tiga Pengukuran untuk Nabil Pratama
        // Bulan 6: 66.0 cm, 7.3 kg
        Measurement::updateOrCreate(
            [
                'child_id'         => $nabil->id,
                'measurement_date' => Carbon::now()->subMonths(2)->toDateString(),
            ],
            [
                'recorded_by'           => $kader->id,
                'age_in_months'         => 6,
                'weight_kg'             => 7.30,
                'height_cm'             => 66.00,
                'head_circumference_cm' => 42.50,
                'measurement_method'    => MeasurementMethod::RECUMBENT,
                'z_score_hfa'           => -0.40,
                'z_score_wfa'           => -0.20,
                'z_score_wfh'           => 0.10,
                'status_hfa'            => 'normal',
                'status_wfa'            => 'gizi_baik',
                'status_wfh'            => 'gizi_baik',
                'needs_attention'       => false,
            ]
        );

        // Bulan 7: 68.5 cm, 7.9 kg
        Measurement::updateOrCreate(
            [
                'child_id'         => $nabil->id,
                'measurement_date' => Carbon::now()->subMonths(1)->toDateString(),
            ],
            [
                'recorded_by'           => $kader->id,
                'age_in_months'         => 7,
                'weight_kg'             => 7.90,
                'height_cm'             => 68.50,
                'head_circumference_cm' => 43.10,
                'measurement_method'    => MeasurementMethod::RECUMBENT,
                'z_score_hfa'           => -0.20,
                'z_score_wfa'           => -0.10,
                'z_score_wfh'           => 0.20,
                'status_hfa'            => 'normal',
                'status_wfa'            => 'gizi_baik',
                'status_wfh'            => 'gizi_baik',
                'needs_attention'       => false,
            ]
        );

        // Bulan 8 (Terbaru): 71.0 cm, 8.4 kg
        Measurement::updateOrCreate(
            [
                'child_id'         => $nabil->id,
                'measurement_date' => Carbon::now()->toDateString(),
            ],
            [
                'recorded_by'           => $kader->id,
                'age_in_months'         => 8,
                'weight_kg'             => 8.40,
                'height_cm'             => 71.00,
                'head_circumference_cm' => 43.80,
                'measurement_method'    => MeasurementMethod::RECUMBENT,
                'z_score_hfa'           => 0.10,
                'z_score_wfa'           => 0.00,
                'z_score_wfh'           => 0.30,
                'status_hfa'            => 'normal',
                'status_wfa'            => 'gizi_baik',
                'status_wfh'            => 'gizi_baik',
                'needs_attention'       => false,
            ]
        );
    }
}

