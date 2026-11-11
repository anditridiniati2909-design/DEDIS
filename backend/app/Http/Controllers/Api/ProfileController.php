<?php
namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Requests\Profile\UpdateProfileRequest;
use App\Http\Resources\UserResource;
use Illuminate\Http\Request;

class ProfileController extends Controller
{
    /**
     * GET /api/v1/profile
     * Return the current authenticated user's profile.
     */
    public function show(Request $request)
    {
        return new UserResource($request->user());
    }

    /**
     * PUT /api/v1/profile
     * Update the authenticated user's profile (name, phone, dusun).
     */
    public function update(UpdateProfileRequest $request)
    {
        $user = $request->user();
        $user->name         = $request->input('name');
        $user->phone_number = $request->input('phone');
        $user->dusun        = $request->input('dusun');
        $user->save();
        return new UserResource($user);
    }
}
?>