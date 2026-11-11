<?php

namespace App\Enums;

enum AssistanceType: string
{
    case COUNSELING = 'counseling';                       // Konseling gizi
    case MIDWIFE_CONSULTATION = 'midwife_consultation';   // Konsultasi bidan desa
    case REFERRAL = 'referral';                           // Rujukan faskes (Puskesmas)

    public function label(): string
    {
        return match ($this) {
            self::COUNSELING => 'Konseling Gizi',
            self::MIDWIFE_CONSULTATION => 'Konsultasi Bidan',
            self::REFERRAL => 'Rujukan ke Faskes / Puskesmas',
        };
    }
}

