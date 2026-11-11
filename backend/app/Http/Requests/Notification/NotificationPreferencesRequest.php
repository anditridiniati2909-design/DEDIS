<?php
namespace App\Http\Requests\Notification;

use Illuminate\Foundation\Http\FormRequest;

class NotificationPreferencesRequest extends FormRequest
{
    public function authorize(): bool { return $this->user() !== null; }

    public function rules(): array
    {
        return [
            'reminder_schedule'  => ['boolean'],
            'new_message'        => ['boolean'],
            'new_measurement'    => ['boolean'],
            'whatsapp_reminder'  => ['boolean'],
        ];
    }
}
?>
