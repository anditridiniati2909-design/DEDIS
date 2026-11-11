<?php

namespace App\Http\Controllers\Api;

use App\Enums\UserRole;
use App\Http\Controllers\Controller;
use App\Http\Requests\Auth\ActivateKaderRequest;
use App\Http\Requests\Auth\LoginRequest;
use App\Http\Requests\Auth\RegisterRequest;
use App\Http\Resources\UserResource;
use App\Models\KaderInvitation;
use App\Models\User;
use App\Services\AuditLogService;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\RateLimiter;

class AuthController extends Controller
{
    // ==========================================
    // LOGIN
    // ==========================================

    public function login(LoginRequest $request): JsonResponse
    {
        $key = 'login:' . $request->ip() . ':' . $request->input('email');

        // Rate limit: 5 percobaan per 15 menit
        if (RateLimiter::tooManyAttempts($key, 5)) {
            $seconds = RateLimiter::availableIn($key);
            return response()->json([
                'message' => "Terlalu banyak percobaan login. Coba lagi dalam {$seconds} detik.",
            ], 429);
        }

        $user = User::where('email', $request->email)->first();

        if (! $user || ! Hash::check($request->password, $user->password)) {
            RateLimiter::hit($key, 900); // 15 menit
            return response()->json([
                'message' => 'Email atau kata sandi salah.',
            ], 401);
        }

        // Periksa status akun
        if (! $user->is_active) {
            return response()->json([
                'message' => 'Akun tidak aktif. Hubungi administrator posyandu.',
            ], 403);
        }

        if ($user->isLocked()) {
            return response()->json([
                'message' => 'Akun terkunci sementara karena terlalu banyak percobaan gagal.',
                'locked_until' => $user->locked_until,
            ], 403);
        }

        RateLimiter::clear($key);
        $user->resetLoginAttempts();

        // Buat token Sanctum
        $tokenName = 'dedis-' . $user->role->value . '-' . now()->timestamp;
        $token = $user->createToken($tokenName);

        AuditLogService::log('login', $user, notes: 'Login berhasil');

        return response()->json([
            'message' => 'Login berhasil.',
            'token'   => $token->plainTextToken,
            'user'    => new UserResource($user),
        ]);
    }

    // ==========================================
    // REGISTER (selalu orang_tua)
    // ==========================================

    public function register(RegisterRequest $request): JsonResponse
    {
        $user = User::create([
            'name'     => $request->name,
            'email'    => $request->email,
            'password' => Hash::make($request->password),
            'role'     => UserRole::ORANG_TUA, // HARDCODED — tidak bisa diubah dari request
        ]);

        $token = $user->createToken('dedis-orang_tua-' . now()->timestamp);

        AuditLogService::log('create', $user, notes: 'Registrasi akun orang tua baru');

        return response()->json([
            'message' => 'Akun berhasil dibuat.',
            'token'   => $token->plainTextToken,
            'user'    => new UserResource($user),
        ], 201);
    }

    // ==========================================
    // AKTIVASI KADER (via kode undangan)
    // ==========================================

    public function activateKader(ActivateKaderRequest $request): JsonResponse
    {
        $invitation = KaderInvitation::where('code', $request->invitation_code)
            ->whereNull('used_at')
            ->where('expires_at', '>', now())
            ->first();

        if (! $invitation) {
            return response()->json([
                'message' => 'Kode undangan tidak valid atau sudah kedaluwarsa.',
            ], 422);
        }

        $user = User::create([
            'name'     => $request->name,
            'email'    => $request->email,
            'password' => Hash::make($request->password),
            'role'     => $invitation->role, // peran dari undangan (kader/kader_admin)
        ]);

        $invitation->markAsUsed($user->id);

        $token = $user->createToken('dedis-' . $user->role->value . '-' . now()->timestamp);

        AuditLogService::log('create', $user, notes: "Aktivasi kader via kode {$invitation->code}");

        return response()->json([
            'message' => 'Akun kader berhasil diaktifkan.',
            'token'   => $token->plainTextToken,
            'user'    => new UserResource($user),
        ], 201);
    }

    // ==========================================
    // LOGOUT
    // ==========================================

    public function logout(Request $request): JsonResponse
    {
        AuditLogService::log('logout', $request->user());
        $request->user()->currentAccessToken()->delete();

        return response()->json(['message' => 'Berhasil keluar.']);
    }

    // ==========================================
    // ME (data pengguna saat ini)
    // ==========================================

    public function me(Request $request): JsonResponse
    {
        return response()->json(new UserResource($request->user()->fresh()));
    }
}

