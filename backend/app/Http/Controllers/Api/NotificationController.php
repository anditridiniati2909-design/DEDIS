<?php
namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Requests\Notification\NotificationPreferencesRequest;
use App\Models\UserNotification;
use Illuminate\Http\Response;

class NotificationController extends Controller
{
    /**
     * Return the notification preferences for the authenticated user.
     */
    public function index()
    {
        $user = request()->user();
        $prefs = $user->notificationPreferences ?? new UserNotification(['user_id' => $user->id]);
        return response()->json($prefs);
    }

    /**
     * Update the notification preferences for the authenticated user.
     */
    public function update(NotificationPreferencesRequest $request)
    {
        $user = $request->user();
        $prefs = $user->notificationPreferences ?? new UserNotification(['user_id' => $user->id]);
        $prefs->fill($request->only([
            'reminder_schedule',
            'new_message',
            'new_measurement',
            'whatsapp_reminder',
        ]));
        $prefs->save();
        return response()->json(['message' => 'Preferensi notifikasi berhasil disimpan.']);
    }
}
?>
