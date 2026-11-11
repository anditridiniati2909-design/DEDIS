<?php

namespace App\Http\Requests\Measurement;

use App\Enums\MeasurementMethod;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class StoreMeasurementRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'measured_on'        => ['nullable', 'date', 'before_or_equal:today'],
            'measurement_date'   => ['nullable', 'date', 'before_or_equal:today'],
            'weight_kg'          => ['nullable', 'numeric', 'min:1', 'max:60'],
            'height_cm'          => ['nullable', 'numeric', 'min:30', 'max:130'],
            'height_method'      => ['nullable'],
            'measurement_method' => ['nullable'],
            'head_circ_cm'       => ['nullable', 'numeric', 'min:20', 'max:60'],
            'head_circumference_cm' => ['nullable', 'numeric', 'min:20', 'max:60'],
            'upper_arm_cm'       => ['nullable', 'numeric', 'min:5', 'max:30'],
            'notes'              => ['nullable', 'string', 'max:1000'],
        ];
    }

    /** Minimal berat ATAU tinggi harus diisi */
    public function withValidator($validator): void
    {
        $validator->after(function ($validator) {
            if (! $this->filled('weight_kg') && ! $this->filled('height_cm')) {
                $validator->errors()->add(
                    'weight_kg',
                    'Minimal berat badan atau tinggi/panjang badan harus diisi.'
                );
            }
        });
    }

    public function messages(): array
    {
        return [
            'measured_on.required'        => 'Tanggal pengukuran wajib diisi.',
            'measured_on.before_or_equal' => 'Tanggal pengukuran tidak boleh di masa depan.',
            'weight_kg.min'               => 'Berat badan minimal 1 kg.',
            'weight_kg.max'               => 'Berat badan maksimal 60 kg.',
            'height_cm.min'               => 'Tinggi/panjang badan minimal 30 cm.',
            'height_cm.max'               => 'Tinggi/panjang badan maksimal 130 cm.',
        ];
    }
}

