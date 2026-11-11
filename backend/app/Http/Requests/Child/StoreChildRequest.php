<?php

namespace App\Http\Requests\Child;

use App\Enums\Gender;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class StoreChildRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true; // Otorisasi peran ditangani middleware CheckRole
    }

    public function rules(): array
    {
        return [
            'name'            => ['required', 'string', 'max:255'],
            'gender'          => ['required', Rule::enum(Gender::class)],
            'birth_date'      => [
                'required', 'date',
                'before_or_equal:today',
                'after:' . now()->subYears(6)->toDateString(),
            ],
            'birth_weight_kg' => ['nullable', 'numeric', 'min:0.5', 'max:8'],
            'birth_height_cm' => ['nullable', 'numeric', 'min:30', 'max:70'],
            'mother_name'     => ['nullable', 'string', 'max:255'],
            'father_name'     => ['nullable', 'string', 'max:255'],
            'guardian_name'   => ['nullable', 'string', 'max:255'],
            'guardian_phone'  => ['nullable', 'string', 'max:20'],
            'address'         => ['nullable', 'string', 'max:500'],
            'receives_mbg'    => ['boolean'],
            'notes'           => ['nullable', 'string', 'max:1000'],
        ];
    }

    public function messages(): array
    {
        return [
            'name.required'              => 'Nama anak wajib diisi.',
            'gender.required'            => 'Jenis kelamin wajib dipilih.',
            'birth_date.required'        => 'Tanggal lahir wajib diisi.',
            'birth_date.before_or_equal' => 'Tanggal lahir tidak boleh di masa depan.',
            'birth_date.after'           => 'Usia anak melebihi batas layanan (5 tahun 11 bulan).',
        ];
    }
}

