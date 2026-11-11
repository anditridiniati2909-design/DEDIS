<?php
namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Requests\Auth\ChangePasswordRequest;
use Illuminate\Support\Facades\Hash;
use Symfony\Component\HttpFoundation\Response;

class PasswordController extends Controller
{
    /**
     * Change the authenticated user's password.
     */
    public function change(ChangePasswordRequest $request)
    {
        $user = $request->user();
        // Verify old password
        if (!Hash::check($request->input('old_password'), $user->password)) {
            return response()->json([
                'message' => 'Kata sandi lama tidak cocok.'
            ], Response::HTTP_UNAUTHORIZED);
        }

        $user->password = Hash::make($request->input('new_password'));
        $user->save();

        return response()->json([
            'message' => 'Kata sandi berhasil diubah.'
        ], Response::HTTP_OK);
    }
}
?>
