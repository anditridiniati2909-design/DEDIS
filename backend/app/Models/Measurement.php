<?php

namespace App\Models;

use App\Enums\MeasurementMethod;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\SoftDeletes;

class Measurement extends Model
{
    use HasFactory, SoftDeletes;

    protected $fillable = [
        'child_id',
        'recorded_by',
        'measurement_date',
        'age_in_months',
        'weight_kg',
        'height_cm',
        'head_circumference_cm',
        'measurement_method',
        'correction_applied',
        'z_score_hfa',
        'z_score_wfa',
        'z_score_wfh',
        'z_score_bfa',
        'status_hfa',
        'status_wfa',
        'status_wfh',
        'status_bfa',
        'needs_attention',
        'attention_reason',
        'is_verified_by_midwife',
        'offline_sync_id',
    ];

    protected $casts = [
        'measurement_date' => 'date',
        'measurement_method' => MeasurementMethod::class,
        'correction_applied' => 'boolean',
        'needs_attention' => 'boolean',
        'is_verified_by_midwife' => 'boolean',
        'weight_kg' => 'decimal:2',
        'height_cm' => 'decimal:2',
        'head_circumference_cm' => 'decimal:2',
        'z_score_hfa' => 'decimal:2',
        'z_score_wfa' => 'decimal:2',
        'z_score_wfh' => 'decimal:2',
        'z_score_bfa' => 'decimal:2',
    ];

    public function child(): BelongsTo
    {
        return $this->belongsTo(Child::class, 'child_id');
    }

    public function recorder(): BelongsTo
    {
        return $this->belongsTo(User::class, 'recorded_by');
    }

    public function followUps(): HasMany
    {
        return $this->hasMany(GrowthFollowUp::class, 'measurement_id');
    }
}

