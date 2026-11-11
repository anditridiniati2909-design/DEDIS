<?php
namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Requests\Message\SendMessageRequest;
use App\Http\Resources\MessageResource;
use App\Models\Message;
use Illuminate\Http\Request;

class MessageController extends Controller
{
    /**
     * GET /api/v1/messages
     * List all conversations for the authenticated user.
     * Returns one entry per unique partner (latest message, unread count).
     */
    public function index(Request $request)
    {
        $userId = $request->user()->id;

        // Collect all unique partner IDs
        $sent     = Message::where('sender_id', $userId)->select('receiver_id as partner_id');
        $received = Message::where('receiver_id', $userId)->select('sender_id as partner_id');
        $partnerIds = $sent->union($received)->get()->pluck('partner_id')->unique();

        $conversations = $partnerIds->map(function ($partnerId) use ($userId) {
            $latest = Message::where(function ($q) use ($userId, $partnerId) {
                $q->where('sender_id', $userId)->where('receiver_id', $partnerId);
            })->orWhere(function ($q) use ($userId, $partnerId) {
                $q->where('sender_id', $partnerId)->where('receiver_id', $userId);
            })->latest()->first();

            $unread = Message::where('sender_id', $partnerId)
                ->where('receiver_id', $userId)
                ->whereNull('read_at')
                ->count();

            return [
                'partner_id'   => $partnerId,
                'partner_name' => $latest?->sender_id === $partnerId
                    ? $latest->sender->name
                    : $latest?->receiver->name,
                'last_message' => $latest?->content,
                'last_at'      => $latest?->created_at,
                'unread_count' => $unread,
            ];
        })->sortByDesc('last_at')->values();

        return response()->json($conversations);
    }

    /**
     * GET /api/v1/messages/{partnerId}
     * List all messages between the authenticated user and a specific partner.
     * Also marks unread messages (sent by partner) as read.
     */
    public function show(Request $request, int $partnerId)
    {
        $userId = $request->user()->id;

        // Mark messages from partner as read
        Message::where('sender_id', $partnerId)
            ->where('receiver_id', $userId)
            ->whereNull('read_at')
            ->update(['read_at' => now()]);

        $messages = Message::where(function ($q) use ($userId, $partnerId) {
            $q->where('sender_id', $userId)->where('receiver_id', $partnerId);
        })->orWhere(function ($q) use ($userId, $partnerId) {
            $q->where('sender_id', $partnerId)->where('receiver_id', $userId);
        })->orderBy('created_at')->get();

        return MessageResource::collection($messages);
    }

    /**
     * POST /api/v1/messages
     * Send a new message.
     */
    public function store(SendMessageRequest $request)
    {
        $message = Message::create([
            'sender_id'   => $request->user()->id,
            'receiver_id' => $request->input('receiver_id'),
            'content'     => $request->input('content'),
        ]);

        // Trigger push notification (placeholder – implement with FCM later)
        $this->sendPushNotification($request->input('receiver_id'), $request->user()->name, $request->input('content'));

        return new MessageResource($message->load('sender', 'receiver'));
    }

    /**
     * Placeholder for push notification.
     * TODO: Implement using Firebase Cloud Messaging (FCM) server key.
     * To enable: inject FCM notification via laravel/notification-channels package
     * and dispatch using $user->notify(new NewMessageNotification($message)).
     *
     * @param int    $toUserId  Recipient user ID
     * @param string $senderName Display name of the sender
     * @param string $body       Message content
     */
    protected function sendPushNotification(int $toUserId, string $senderName, string $body): void
    {
        // TODO: Implement FCM push notification here when server key is available.
        // $token = UserFcmToken::where('user_id', $toUserId)->value('token');
        // if ($token) { /* dispatch FCM via HTTP v1 API */ }
    }
}
?>
