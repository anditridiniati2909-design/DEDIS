<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class UserResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        return [
            'id'             => $this->id,
            'name'           => $this->name,
            'email'          => $this->email,
            'role'           => $this->role instanceof \App\Enums\UserRole ? $this->role->value : $this->role,
            'phone'          => $this->phone_number,
            'phone_number'   => $this->phone_number,
            'dusun'          => $this->dusun,
            'children_count' => $this->relationLoaded('children') ? $this->children->count() : $this->children()->count(),
            'is_active'      => $this->is_active,
            'created_at'     => $this->created_at,
        ];
    }
}

