<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class ChildResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        $ageInMonths = $this->calculateAgeInMonths();

        return [
            'id'                  => $this->id,
            'card_code'           => $this->card_code,
            'name'                => $this->name,
            'gender'              => $this->gender instanceof \App\Enums\Gender ? $this->gender->value : (string) $this->gender,
            'birth_date'          => $this->birth_date ? $this->birth_date->toDateString() : null,
            'age_in_months'       => $ageInMonths,
            'age_label'           => "{$ageInMonths} bln",
            'mother_name'         => $this->mother_name,
            'hamlet'              => $this->hamlet,
            'dusun'               => $this->hamlet,
            'is_mbg_recipient'    => (bool) $this->is_mbg_recipient,
            'created_by'          => $this->created_by,
            'created_at'          => $this->created_at,

            // Pengukuran terakhir
            'latest_measurement' => $this->relationLoaded('measurements')
                ? ($this->measurements->sortByDesc('measurement_date')->first() 
                    ? new MeasurementResource($this->measurements->sortByDesc('measurement_date')->first()) 
                    : null)
                : ($this->latestMeasurement ? new MeasurementResource($this->latestMeasurement) : null),

            // Daftar semua pengukuran (jika dimuat)
            'measurements' => $this->whenLoaded('measurements',
                fn() => MeasurementResource::collection(
                    $this->measurements->sortByDesc('measurement_date')
                )
            ),
        ];
    }
}

