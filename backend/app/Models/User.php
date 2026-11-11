<?php

namespace App\Models;

use App\Enums\UserRole;
use Carbon\Carbon;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\Relations\HasOne;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

class User extends Authenticatable
{
    use HasApiTokens, HasFactory, Notifiable, SoftDeletes;

    protected $fillable = [
        'name',
        'email',
        'password',
        'role',
        'phone_number',
        'dusun',
        'privacy_consent_at',
        'is_active',
        'failed_login_attempts',
        'locked_until',
    ];

    protected $hidden = [
        'password',
        'remember_token',
    ];

    protected $casts = [
        'role' => UserRole::class,
        'privacy_consent_at' => 'datetime',
        'locked_until' => 'datetime',
        'is_active' => 'boolean',
        'failed_login_attempts' => 'integer',
    ];

    // Otorisasi Peran
    public function isOrangTua(): bool
    {
        return $this->role === UserRole::ORANG_TUA;
    }

    public function isKader(): bool
    {
        return in_array($this->role, [UserRole::KADER, UserRole::KADER_ADMIN], true);
    }

    public function isKaderAdmin(): bool
    {
        return $this->role === UserRole::KADER_ADMIN;
    }

    // Hubungan Relasi
    public function children(): BelongsToMany
    {
        return $this->belongsToMany(Child::class, 'child_guardian_links', 'user_id', 'child_id')
            ->wherePivot('is_linked', true)
            ->withPivot('linked_at')
            ->withTimestamps();
    }

    public function guardianLinks(): HasMany
    {
        return $this->hasMany(ChildGuardianLink::class, 'user_id');
    }

    public function recordedMeasurements(): HasMany
    {
        return $this->hasMany(Measurement::class, 'recorded_by');
    }

    public function createdInvitations(): HasMany
    {
        return $this->hasMany(KaderInvitation::class, 'created_by');
    }

    public function notificationPreferences(): HasOne
    {
        return $this->hasOne(UserNotification::class);
    }

    // Keamanan: Brute Force Protection (Maks 5 kali gagal -> kunci 15 menit)
    public function isLockedOut(): bool
    {
        return $this->locked_until !== null && $this->locked_until->isFuture();
    }

    public function recordFailedLogin(): void
    {
        $this->increment('failed_login_attempts');
        if ($this->failed_login_attempts >= 5) {
            $this->update([
                'locked_until' => Carbon::now()->addMinutes(15),
            ]);
        }
    }

    public function resetFailedLogin(): void
    {
        if ($this->failed_login_attempts > 0 || $this->locked_until !== null) {
            $this->update([
                'failed_login_attempts' => 0,
                'locked_until' => null,
            ]);
        }
    }
}

