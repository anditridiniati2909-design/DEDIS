<?php

namespace App\Models;

use App\Enums\Gender;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class WhoGrowthStandard extends Model
{
    use HasFactory;

    protected $fillable = [
        'indicator',
        'gender',
        'age_or_length',
        'l_value',
        'm_value',
        's_value',
        'sd3neg',
        'sd2neg',
        'sd1neg',
        'sd0',
        'sd1pos',
        'sd2pos',
        'sd3pos',
    ];

    protected $casts = [
        'gender' => Gender::class,
        'age_or_length' => 'decimal:2',
        'l_value' => 'decimal:4',
        'm_value' => 'decimal:4',
        's_value' => 'decimal:4',
        'sd3neg' => 'decimal:4',
        'sd2neg' => 'decimal:4',
        'sd1neg' => 'decimal:4',
        'sd0' => 'decimal:4',
        'sd1pos' => 'decimal:4',
        'sd2pos' => 'decimal:4',
        'sd3pos' => 'decimal:4',
    ];
}

