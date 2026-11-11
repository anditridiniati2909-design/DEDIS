<?php
namespace App\Http\Resources;

use Illuminate\Http\Resources\Json\JsonResource;

class MessageResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     */
    public function toArray($request): array
    {
        return [
            'id'          => $this->id,
            'sender_id'   => $this->sender_id,
            'sender_name' => $this->sender?->name,
            'receiver_id' => $this->receiver_id,
            'content'     => $this->content,
            'read_at'     => $this->read_at?->toISOString(),
            'created_at'  => $this->created_at->toISOString(),
        ];
    }
}
?>
