<?php

namespace App\Models;

use Carbon\Carbon;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class KaderInvitation extends Model
{
    use HasFactory;

    protected $fillable = [
        'code',
        'created_by',
        'used_by',
        'expires_at',
        'is_used',
    ];

    protected $casts = [
        'expires_at' => 'datetime',
        'is_used' => 'boolean',
    ];

    public function creator(): BelongsTo
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class, 'used_by');
    }

    public function isValid(): bool
    {
        return !$this->is_used && $this->expires_at->isFuture();
    }

    public static function generateCode(): string
    {
        do {
            $code = 'KDR-' . strtoupper(bin2hex(random_bytes(4)));
        } while (self::where('code', $code)->exists());

        return $code;
    }
}

