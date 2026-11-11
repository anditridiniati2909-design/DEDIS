<?php

namespace App\Enums;

enum MeasurementMethod: string
{
    case STANDING = 'standing';   // Berdiri (Tinggi Badan, anak usia >= 24 bulan)
    case RECUMBENT = 'recumbent'; // Telentang (Panjang Badan, anak usia < 24 bulan)

    public function label(): string
    {
        return match ($this) {
            self::STANDING => 'Berdiri (Tinggi Badan)',
            self::RECUMBENT => 'Telentang (Panjang Badan)',
        };
    }
}

