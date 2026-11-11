<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    public function run(): void
    {
        $this->call([
            // ⚠️  WhoGrowthStandardSeeder: WAJIB diisi dari file CSV WHO resmi.
            //     Jangan jalankan tanpa file CSV yang sudah diimpor!
            // WhoGrowthStandardSeeder::class,

            ImmunizationScheduleSeeder::class,
            EducationContentSeeder::class,
            DemoDataSeeder::class,
        ]);
    }
}

