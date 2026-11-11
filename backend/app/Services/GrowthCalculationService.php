<?php

namespace App\Services;

use App\Enums\Gender;
use App\Enums\MeasurementMethod;
use App\Models\WhoGrowthStandard;
use Illuminate\Support\Facades\Log;

/**
 * Hitung status gizi dengan metode WHO LMS.
 *
 * Rumus: Z = ((X/M)^L - 1) / (L * S)
 * Jika L ≈ 0: Z = ln(X/M) / S
 *
 * X  = nilai terukur
 * L, M, S = nilai dari tabel WHO sesuai usia dan jenis kelamin
 *
 * ⚠️  PENTING: Data L/M/S WAJIB berasal dari tabel WHO resmi via WhoGrowthStandardSeeder.
 *     Jangan pernah menginvent atau mendekati nilai ini secara manual.
 *
 * Referensi: WHO MGRS, Permenkes No. 2 Tahun 2020
 */
class GrowthCalculationService
{
    /**
     * Koreksi metode pengukuran (standar WHO):
     * - Anak < 24 bulan diukur berdiri → tambah 0.7 cm
     * - Anak ≥ 24 bulan diukur berbaring → kurang 0.7 cm
     */
    public function applyMeasurementCorrection(
        float $heightCm,
        int $ageInMonths,
        MeasurementMethod $method
    ): float {
        if ($ageInMonths < 24 && $method === MeasurementMethod::STANDING) {
            return $heightCm + 0.7;
        }
        if ($ageInMonths >= 24 && $method === MeasurementMethod::RECUMBENT) {
            return $heightCm - 0.7;
        }
        return $heightCm;
    }

    /**
     * Hitung Z-score dengan metode LMS.
     * Mengembalikan null jika data tidak valid.
     */
    public function calculateZScore(float $measured, float $l, float $m, float $s): ?float
    {
        if ($m <= 0 || $s <= 0) {
            Log::warning('GrowthCalculation: M atau S tidak valid', compact('l', 'm', 's'));
            return null;
        }

        if (abs($l) < 0.0001) {
            // Jika L mendekati 0, gunakan aproksimasi ln
            return log($measured / $m) / $s;
        }

        return (pow($measured / $m, $l) - 1) / ($l * $s);
    }

    /**
     * Hitung semua indikator gizi untuk satu pengukuran.
     *
     * @return array{
     *   height_corrected_cm: float|null,
     *   hfa_zscore: float|null,
     *   hfa_status: string,
     *   wfa_zscore: float|null,
     *   wfa_status: string,
     *   wfh_zscore: float|null,
     *   wfh_status: string,
     * }
     */
    public function calculate(
        Gender $gender,
        int $ageInMonths,
        ?float $weightKg,
        ?float $heightCm,
        MeasurementMethod $heightMethod = MeasurementMethod::RECUMBENT
    ): array {
        $result = [
            'height_corrected_cm' => null,
            'hfa_zscore'          => null,
            'hfa_status'          => 'belum_bisa_dinilai',
            'wfa_zscore'          => null,
            'wfa_status'          => 'belum_bisa_dinilai',
            'wfh_zscore'          => null,
            'wfh_status'          => 'belum_bisa_dinilai',
        ];

        // Pastikan usia dalam batas layanan (0-59 bulan)
        $ageInMonths = min($ageInMonths, 59);

        // --- BB/U (Weight-for-Age) ---
        if ($weightKg !== null) {
            $std = $this->getStandard('wfa', $gender, $ageInMonths);
            if ($std) {
                $z = $this->calculateZScore($weightKg, $std->l, $std->m, $std->s);
                $result['wfa_zscore'] = $z !== null ? round($z, 3) : null;
                $result['wfa_status'] = $this->classifyWfa($result['wfa_zscore']);
            }
        }

        // --- TB/U atau PB/U (Height/Length-for-Age) ---
        if ($heightCm !== null) {
            $corrected = $this->applyMeasurementCorrection($heightCm, $ageInMonths, $heightMethod);
            $result['height_corrected_cm'] = round($corrected, 1);

            // <24 bulan pakai PB/U (lhfa), ≥24 bulan pakai TB/U (hfa)
            $indicator = $ageInMonths < 24 ? 'lhfa' : 'hfa';
            $std = $this->getStandard($indicator, $gender, $ageInMonths);
            if ($std) {
                $z = $this->calculateZScore($corrected, $std->l, $std->m, $std->s);
                $result['hfa_zscore'] = $z !== null ? round($z, 3) : null;
                $result['hfa_status'] = $this->classifyHfa($result['hfa_zscore']);
            }

            // --- BB/TB (Weight-for-Height) ---
            if ($weightKg !== null) {
                $wfhStd = $this->getWfhStandard($gender, $corrected, $ageInMonths);
                if ($wfhStd) {
                    $z = $this->calculateZScore($weightKg, $wfhStd->l, $wfhStd->m, $wfhStd->s);
                    $result['wfh_zscore'] = $z !== null ? round($z, 3) : null;
                    $result['wfh_status'] = $this->classifyWfh($result['wfh_zscore']);
                }
            }
        }

        return $result;
    }

    // ==========================================
    // Klasifikasi Status (Permenkes No. 2/2020)
    // ==========================================

    private function classifyHfa(?float $z): string
    {
        if ($z === null) return 'belum_bisa_dinilai';
        if ($z < -3)    return 'sangat_pendek';  // severely stunted
        if ($z < -2)    return 'pendek';          // stunted
        if ($z <= 3)    return 'normal';
        return 'tinggi';
    }

    private function classifyWfa(?float $z): string
    {
        if ($z === null) return 'belum_bisa_dinilai';
        if ($z < -3)    return 'sangat_kurang';
        if ($z < -2)    return 'kurang';
        if ($z <= 1)    return 'normal';
        return 'risiko_berat_lebih';
    }

    private function classifyWfh(?float $z): string
    {
        if ($z === null) return 'belum_bisa_dinilai';
        if ($z < -3)    return 'gizi_buruk';
        if ($z < -2)    return 'gizi_kurang';
        if ($z <= 1)    return 'gizi_baik';
        if ($z <= 2)    return 'berisiko_lebih';
        if ($z <= 3)    return 'gizi_lebih';
        return 'obesitas';
    }

    /** Apakah anak terklasifikasi stunting? */
    public function isStunting(?float $hfaZscore): bool
    {
        return $hfaZscore !== null && $hfaZscore < -2;
    }

    // ==========================================
    // Helper: ambil data WHO dari database
    // ==========================================

    private function getStandard(string $indicator, Gender $gender, int $ageInMonths): ?WhoGrowthStandard
    {
        return WhoGrowthStandard::where('indicator', $indicator)
            ->where('gender', $gender->value)
            ->where('age_months', $ageInMonths)
            ->first();
    }

    /**
     * WFH menggunakan tinggi badan sebagai x-axis (bukan usia).
     * Bulatkan ke 0.5 cm terdekat untuk lookup tabel.
     */
    private function getWfhStandard(Gender $gender, float $heightCm, int $ageInMonths): ?WhoGrowthStandard
    {
        $indicator = $ageInMonths < 24 ? 'wfl' : 'wfh';
        $heightKey = round($heightCm * 2) / 2; // bulatkan ke 0.5 cm terdekat

        return WhoGrowthStandard::where('indicator', $indicator)
            ->where('gender', $gender->value)
            ->where('height_cm', $heightKey)
            ->first();
    }
}

