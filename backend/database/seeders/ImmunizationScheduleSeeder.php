<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

/**
 * Jadwal imunisasi standar nasional (Permenkes / IDAI).
 * Ini data referensi (tidak berubah), bukan data pengguna.
 */
class ImmunizationScheduleSeeder extends Seeder
{
    public function run(): void
    {
        $schedules = [
            // [vaccine_code, vaccine_name, recommended_age_months, notes]
            ['HB0',          'Hepatitis B 0',          0,  'Diberikan dalam 24 jam setelah lahir'],
            ['BCG',          'BCG',                     1,  'Idealnya 1 bulan, bisa sampai <3 bulan'],
            ['Polio0',       'Polio Oral 0',            0,  'Diberikan saat lahir'],
            ['DPT-HB-Hib1',  'DPT-HB-Hib 1',           2,  ''],
            ['Polio1',       'Polio Oral 1',            2,  ''],
            ['IPV1',         'IPV (Polio Suntik) 1',    4,  ''],
            ['DPT-HB-Hib2',  'DPT-HB-Hib 2',           3,  ''],
            ['Polio2',       'Polio Oral 2',            3,  ''],
            ['DPT-HB-Hib3',  'DPT-HB-Hib 3',           4,  ''],
            ['Polio3',       'Polio Oral 3',            4,  ''],
            ['Rotavirus1',   'Rotavirus 1',             2,  'Program Nasional 2024'],
            ['Rotavirus2',   'Rotavirus 2',             3,  'Program Nasional 2024'],
            ['Rotavirus3',   'Rotavirus 3',             4,  'Program Nasional 2024'],
            ['Campak-MR1',   'Campak-MR (MR1)',         9,  ''],
            ['JE',           'Japanese Encephalitis',  10,  'Daerah endemis JE'],
            ['DPT-HB-Hib4',  'DPT-HB-Hib (Booster)',  18,  'Booster'],
            ['Campak-MR2',   'Campak-MR (MR2)',        18,  'Booster'],
        ];

        foreach ($schedules as [$code, $name, $ageMonths, $notes]) {
            DB::table('immunization_schedules')->updateOrInsert(
                ['vaccine_code' => $code],
                [
                    'vaccine_name'            => $name,
                    'recommended_age_months'  => $ageMonths,
                    'notes'                   => $notes,
                    'updated_at'              => now(),
                    'created_at'              => now(),
                ]
            );
        }

        $this->command->info('ImmunizationScheduleSeeder: ' . count($schedules) . ' vaksin diimpor.');
    }
}

