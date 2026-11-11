<?php

namespace App\Enums;

enum UserRole: string
{
    case ORANG_TUA = 'orang_tua';
    case KADER = 'kader';
    case KADER_ADMIN = 'kader_admin';

    public function label(): string
    {
        return match ($this) {
            self::ORANG_TUA => 'Orang Tua / Wali',
            self::KADER => 'Kader Posyandu',
            self::KADER_ADMIN => 'Kader Admin / Bidan',
        };
    }

    public function isKaderLevel(): bool
    {
        return in_array($this, [self::KADER, self::KADER_ADMIN], true);
    }
}

