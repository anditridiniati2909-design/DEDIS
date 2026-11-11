<?php

namespace App\Models;

use App\Enums\Gender;
use Carbon\Carbon;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\Relations\HasOne;
use Illuminate\Database\Eloquent\SoftDeletes;

class Child extends Model
{
    use HasFactory, SoftDeletes;

    protected $fillable = [
        'card_code',
        'name',
        'birth_date',
        'gender',
        'mother_name',
        'hamlet',
        'is_mbg_recipient',
        'is_archived',
        'created_by',
    ];

    protected $casts = [
        'birth_date' => 'date',
        'gender' => Gender::class,
        'is_mbg_recipient' => 'boolean',
        'is_archived' => 'boolean',
    ];

    // Kode kartu acak untuk QR (Anonim/Pseudonim, bukan NIK atau tanggal lahir)
    public static function generateCardCode(): string
    {
        do {
            $code = 'DEDIS-' . strtoupper(substr(bin2hex(random_bytes(4)), 0, 6));
        } while (self::where('card_code', $code)->exists());

        return $code;
    }

    // Hitung umur dalam bulan berdasarkan tanggal ukur atau hari ini
    public function calculateAgeInMonths(?Carbon $targetDate = null): int
    {
        $target = $targetDate ?? Carbon::today();
        return (int) floor($this->birth_date->diffInMonths($target));
    }

    public function getAgeInMonthsAttribute(): int
    {
        return $this->calculateAgeInMonths();
    }

    // Relasi
    public function creator(): BelongsTo
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    public function guardians(): BelongsToMany
    {
        return $this->belongsToMany(User::class, 'child_guardian_links', 'child_id', 'user_id')
            ->wherePivot('is_linked', true)
            ->withPivot('linked_at')
            ->withTimestamps();
    }

    public function guardianLinks(): HasMany
    {
        return $this->hasMany(ChildGuardianLink::class, 'child_id');
    }

    public function measurements(): HasMany
    {
        return $this->hasMany(Measurement::class, 'child_id')->orderBy('measurement_date', 'desc');
    }

    public function latestMeasurement(): HasOne
    {
        return $this->hasOne(Measurement::class, 'child_id')->latestOfMany('measurement_date');
    }

    public function immunizations(): HasMany
    {
        return $this->hasMany(Immunization::class, 'child_id')->orderBy('scheduled_date', 'asc');
    }

    public function followUps(): HasMany
    {
        return $this->hasMany(GrowthFollowUp::class, 'child_id')->orderBy('follow_up_date', 'desc');
    }

    // Scope: Sasaran aktif Posyandu (< 60 bulan / 5 tahun)
    public function scopeActive(Builder $query): Builder
    {
        return $query->where('is_archived', false);
    }
}

