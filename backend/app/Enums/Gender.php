<?php

namespace App\Enums;

enum Gender: string
{
    case L = 'L'; // Laki-laki
    case P = 'P'; // Perempuan

    public function label(): string
    {
        return match ($this) {
            self::L => 'Laki-laki',
            self::P => 'Perempuan',
        };
    }
}

