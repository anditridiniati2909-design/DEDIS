<?php
namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class UserNotification extends Model
{
    use HasFactory;

    protected $table = 'user_notifications';

    protected $fillable = [
        'user_id',
        'reminder_schedule',
        'new_message',
        'new_measurement',
        'whatsapp_reminder',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}
?>
