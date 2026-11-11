<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class MeasurementResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        return [
            'id'                     => $this->id,
            'child_id'               => $this->child_id,
            'recorded_by'            => $this->recorded_by,
            'measurement_date'       => $this->measurement_date ? $this->measurement_date->toDateString() : null,
            'measured_on'            => $this->measurement_date ? $this->measurement_date->toDateString() : null,
            'age_in_months'          => $this->age_in_months,

            // Data terukur
            'weight_kg'              => (float) $this->weight_kg,
            'height_cm'              => (float) $this->height_cm,
            'head_circumference_cm'  => $this->head_circumference_cm ? (float) $this->head_circumference_cm : null,
            'measurement_method'     => $this->measurement_method instanceof \App\Enums\MeasurementMethod 
                                            ? $this->measurement_method->value 
                                            : (string) $this->measurement_method,

            // Z-score dan status
            'z_score_hfa'            => $this->z_score_hfa !== null ? (float) $this->z_score_hfa : null,
            'z_score_wfa'            => $this->z_score_wfa !== null ? (float) $this->z_score_wfa : null,
            'z_score_wfh'            => $this->z_score_wfh !== null ? (float) $this->z_score_wfh : null,
            'status_hfa'             => $this->status_hfa,
            'status_wfa'             => $this->status_wfa,
            'status_wfh'             => $this->status_wfh,
            'needs_attention'        => (bool) $this->needs_attention,
            'attention_reason'       => $this->attention_reason,

            'created_at'             => $this->created_at,
        ];
    }
}

