<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

/**
 * Tolak request yang mengandung field sensitif (pencegahan eskalasi peran).
 * Aktif untuk semua route API yang memerlukan perlindungan.
 */
class PreventRoleEscalation
{
    private const FORBIDDEN_FIELDS = [
        'role',
        'is_active',
        'login_attempts',
        'locked_until',
    ];

    public function handle(Request $request, Closure $next): Response
    {
        foreach (self::FORBIDDEN_FIELDS as $field) {
            if ($request->has($field)) {
                return response()->json([
                    'message' => 'Request mengandung field yang tidak diizinkan.',
                    'field'   => $field,
                ], 422);
            }
        }

        return $next($request);
    }
}

