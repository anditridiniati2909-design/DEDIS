<?php
namespace App\Http\Requests\Profile;

use Illuminate\Foundation\Http\FormRequest;

class UpdateProfileRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     */
    public function authorize(): bool
    {
        // Only authenticated users can update their own profile
        return $this->user() !== null;
    }

    /**
     * Get the validation rules that apply to the request.
     */
    public function rules(): array
    {
        return [
            'name' => ['required', 'string', 'max:255'],
            // Accept numbers only, length >=10, optional leading +62 or 0
            'phone' => ['required', 'regex:/^0?\d{9,15}$/'],
            'dusun' => ['nullable', 'string', 'max:255'],
        ];
    }

    public function messages(): array
    {
        return [
            'phone.regex' => 'Nomor HP harus berupa angka Indonesia (10-15 digit, dapat diawali 0).',
        ];
    }
}
?>
