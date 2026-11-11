<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Immunization extends Model
{
    use SoftDeletes;

    protected $fillable = [
        'child_id',
        'recorded_by',
        'vaccine_name',
        'vaccine_code',       // kode resmi mis. BCG, DPT-HB-Hib1
        'given_at',           // tanggal pemberian
        'age_in_months',      // umur saat imunisasi
        'batch_number',
        'notes',
        'source',             // 'kader' | 'buku_kia_input'
    ];

    protected $casts = [
        'given_at' => 'date',
    ];

    public function child(): BelongsTo
    {
        return $this->belongsTo(Child::class);
    }

    public function recordedBy(): BelongsTo
    {
        return $this->belongsTo(User::class, 'recorded_by');
    }
}

