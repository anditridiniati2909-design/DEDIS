<?php

namespace App\Models;

use Carbon\Carbon;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class ChildGuardianLink extends Model
{
    use HasFactory;

    protected $fillable = [
        'child_id',
        'user_id',
        'link_code',
        'link_code_expires_at',
        'failed_claim_attempts',
        'is_linked',
        'linked_at',
        'created_by',
    ];

    protected $casts = [
        'link_code_expires_at' => 'datetime',
        'linked_at' => 'datetime',
        'is_linked' => 'boolean',
        'failed_claim_attempts' => 'integer',
    ];

    public function child(): BelongsTo
    {
        return $this->belongsTo(Child::class, 'child_id');
    }

    public function guardian(): BelongsTo
    {
        return $this->belongsTo(User::class, 'user_id');
    }

    public function creator(): BelongsTo
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    public function isValidForClaim(): bool
    {
        return !$this->is_linked && $this->link_code_expires_at->isFuture() && $this->failed_claim_attempts < 5;
    }

    // Buat kode tautan 8 karakter acak (mis. 7X9B4K2M)
    public static function generateLinkCode(): string
    {
        $characters = '23456789ABCDEFGHJKLMNPQRSTUVWXYZ'; // Hindari karakter ambigu (0, 1, O, I)
        do {
            $code = '';
            for ($i = 0; $i < 8; $i++) {
                $code .= $characters[random_int(0, strlen($characters) - 1)];
            }
        } while (self::where('link_code', $code)->exists());

        return $code;
    }
}

