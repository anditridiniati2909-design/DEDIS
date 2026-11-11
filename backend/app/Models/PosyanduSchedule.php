<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class PosyanduSchedule extends Model
{
    use SoftDeletes;

    protected $fillable = [
        'created_by',
        'title',
        'scheduled_date',
        'start_time',
        'end_time',
        'location',
        'description',
        'is_cancelled',
        'cancelled_reason',
    ];

    protected $casts = [
        'scheduled_date' => 'date',
        'is_cancelled'   => 'boolean',
    ];

    public function createdBy(): BelongsTo
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    /** Jadwal mendatang yang belum dibatalkan, urut dari yang terdekat */
    public function scopeUpcoming($query)
    {
        return $query->where('scheduled_date', '>=', now()->toDateString())
                     ->where('is_cancelled', false)
                     ->orderBy('scheduled_date');
    }
}

