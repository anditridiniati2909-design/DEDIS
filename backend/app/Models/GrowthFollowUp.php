<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class GrowthFollowUp extends Model
{
    use SoftDeletes;

    protected $fillable = [
        'measurement_id',
        'child_id',
        'created_by',
        'type',          // 'berat_turun','tidak_naik_2bln','zscore_drop','mendekati_minus2'
        'detail',        // JSON: data trigger
        'action_plan',   // rencana tindak lanjut (teks)
        'action_date',
        'resolved_at',
        'resolved_by',
        'notes',
    ];

    protected $casts = [
        'detail'      => 'array',
        'action_date' => 'date',
        'resolved_at' => 'datetime',
    ];

    public function measurement(): BelongsTo
    {
        return $this->belongsTo(Measurement::class);
    }

    public function child(): BelongsTo
    {
        return $this->belongsTo(Child::class);
    }

    public function createdBy(): BelongsTo
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    public function resolvedBy(): BelongsTo
    {
        return $this->belongsTo(User::class, 'resolved_by');
    }
}

