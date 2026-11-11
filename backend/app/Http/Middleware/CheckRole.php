<?php

namespace App\Http\Middleware;

use App\Enums\UserRole;
use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

/**
 * Periksa peran pengguna dari database (bukan hanya token).
 * Penggunaan di routes: ->middleware('role:kader,kader_admin')
 */
class CheckRole
{
    public function handle(Request $request, Closure $next, string ...$roles): Response
    {
        $user = $request->user();

        if (! $user) {
            return response()->json(['message' => 'Tidak terautentikasi.'], 401);
        }

        // Selalu ambil peran dari DB untuk mencegah eskalasi lewat token lama
        $user->refresh();

        if (! $user->is_active) {
            return response()->json([
                'message' => 'Akun tidak aktif. Hubungi administrator.',
            ], 403);
        }

        $allowedRoles = array_map(fn($r) => UserRole::from($r), $roles);

        if (! in_array($user->role, $allowedRoles)) {
            return response()->json([
                'message' => 'Akses ditolak. Peran tidak memiliki izin untuk tindakan ini.',
            ], 403);
        }

        return $next($request);
    }
}

