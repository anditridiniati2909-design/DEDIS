<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;

/**
 * ============================================================
 * WHO GROWTH STANDARD SEEDER
 * ============================================================
 *
 * ⚠️  BLOCKER — TIDAK BOLEH DIJALANKAN TANPA DATA RESMI WHO ⚠️
 *
 * File ini KOSONG secara sengaja. Data L, M, S standar WHO
 * WAJIB diambil dari file CSV resmi WHO:
 *
 *   https://www.who.int/tools/child-growth-standards/standards
 *
 * File yang dibutuhkan (unduh dari situs WHO di atas):
 *   - wfl_girls_0_2_zscores.csv  (PB/U, perempuan, 0-24 bln)
 *   - wfl_boys_0_2_zscores.csv   (PB/U, laki-laki, 0-24 bln)
 *   - wfh_girls_2_5_zscores.csv  (TB/U, perempuan, 24-60 bln)
 *   - wfh_boys_2_5_zscores.csv   (TB/U, laki-laki, 24-60 bln)
 *   - lhfa_girls_0_2_zscores.csv (PB/U, perempuan)
 *   - lhfa_boys_0_2_zscores.csv  (PB/U, laki-laki)
 *   - hfa_girls_2_5_zscores.csv  (TB/U, perempuan)
 *   - hfa_boys_2_5_zscores.csv   (TB/U, laki-laki)
 *   - wfa_girls_0_5_zscores.csv  (BB/U, perempuan)
 *   - wfa_boys_0_5_zscores.csv   (BB/U, laki-laki)
 *
 * Letakkan file CSV di: database/seeders/who_data/
 *
 * Setelah file ada, implementasi seeder akan membaca kolom:
 *   Month/Length/Height, L, M, S
 * dan memasukkan ke tabel who_growth_standards.
 *
 * JANGAN PERNAH menginvent atau mengaproksimasi nilai L, M, S.
 * Data ini dipakai untuk menentukan status stunting anak nyata.
 * ============================================================
 */
class WhoGrowthStandardSeeder extends Seeder
{
    public function run(): void
    {
        // TODO: Implementasi setelah file CSV WHO diunggah
        // Contoh alur:
        //
        // $whoDataPath = database_path('seeders/who_data');
        // foreach ($this->datasets() as [$file, $indicator, $gender, $xAxis]) {
        //     $path = "{$whoDataPath}/{$file}";
        //     if (! file_exists($path)) {
        //         $this->command->error("File tidak ditemukan: {$path}");
        //         continue;
        //     }
        //     // Baca CSV, insert ke who_growth_standards
        // }

        $this->command->warn('WhoGrowthStandardSeeder: BELUM DIJALANKAN.');
        $this->command->warn('Upload file CSV WHO resmi ke database/seeders/who_data/ terlebih dahulu.');
        $this->command->warn('Lihat komentar di file ini untuk daftar file yang dibutuhkan.');
    }

    private function datasets(): array
    {
        return [
            // [filename, indicator, gender, x_axis]
            ['wfa_boys_0_5_zscores.csv',   'wfa',  'L', 'age_months'],
            ['wfa_girls_0_5_zscores.csv',  'wfa',  'P', 'age_months'],
            ['lhfa_boys_0_2_zscores.csv',  'lhfa', 'L', 'age_months'],
            ['lhfa_girls_0_2_zscores.csv', 'lhfa', 'P', 'age_months'],
            ['hfa_boys_2_5_zscores.csv',   'hfa',  'L', 'age_months'],
            ['hfa_girls_2_5_zscores.csv',  'hfa',  'P', 'age_months'],
            ['wfl_boys_0_2_zscores.csv',   'wfl',  'L', 'height_cm'],
            ['wfl_girls_0_2_zscores.csv',  'wfl',  'P', 'height_cm'],
            ['wfh_boys_2_5_zscores.csv',   'wfh',  'L', 'height_cm'],
            ['wfh_girls_2_5_zscores.csv',  'wfh',  'P', 'height_cm'],
        ];
    }
}

