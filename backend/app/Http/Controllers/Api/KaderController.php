<?php

namespace App\Http\Controllers\Api;

use App\Enums\UserRole;
use App\Http\Controllers\Controller;
use App\Http\Resources\UserResource;
use App\Models\KaderInvitation;
use App\Models\User;
use App\Services\AuditLogService;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Str;
use Illuminate\Validation\Rule;

class KaderController extends Controller
{
    // ==========================================
    // DAFTAR KADER (hanya kader_admin)
    // ==========================================

    public function index(): JsonResponse
    {
        $kaders = User::whereIn('role', [UserRole::KADER, UserRole::KADER_ADMIN])
            ->orderBy('name')
            ->get();

        return response()->json(UserResource::collection($kaders));
    }

    // ==========================================
    // BUAT KODE UNDANGAN KADER BARU
    // ==========================================

    public function createInvitation(Request $request): JsonResponse
    {
        $request->validate([
            'role' => ['required', Rule::in([UserRole::KADER->value, UserRole::KADER_ADMIN->value])],
            'notes' => ['nullable', 'string', 'max:255'],
        ]);

        $code = strtoupper(Str::random(8));

        $invitation = KaderInvitation::create([
            'code'       => $code,
            'created_by' => $request->user()->id,
            'role'       => UserRole::from($request->role),
            'expires_at' => now()->addHours(48),
            'notes'      => $request->notes,
        ]);

        AuditLogService::log('create', $invitation, notes: "Kode undangan kader dibuat: {$code}");

        return response()->json([
            'message'    => 'Kode undangan berhasil dibuat. Berlaku 48 jam.',
            'code'       => $code,
            'expires_at' => $invitation->expires_at,
        ], 201);
    }

    // ==========================================
    // DAFTAR UNDANGAN AKTIF
    // ==========================================

    public function listInvitations(): JsonResponse
    {
        $invitations = KaderInvitation::whereNull('used_at')
            ->where('expires_at', '>', now())
            ->orderByDesc('created_at')
            ->get(['code', 'role', 'expires_at', 'notes', 'created_at']);

        return response()->json($invitations);
    }

    // ==========================================
    // NONAKTIFKAN KADER
    // ==========================================

    public function deactivate(Request $request, User $kader): JsonResponse
    {
        if (! in_array($kader->role, [UserRole::KADER, UserRole::KADER_ADMIN])) {
            return response()->json(['message' => 'Pengguna bukan kader.'], 422);
        }

        // Jangan nonaktifkan diri sendiri
        if ($kader->id === $request->user()->id) {
            return response()->json(['message' => 'Tidak dapat menonaktifkan akun Anda sendiri.'], 422);
        }

        $kader->update(['is_active' => false]);

        // Cabut semua token kader tersebut
        $kader->tokens()->delete();

        AuditLogService::log('update', $kader, notes: "Kader dinonaktifkan oleh admin");

        return response()->json(['message' => 'Kader berhasil dinonaktifkan.']);
    }

    // ==========================================
    // AKTIFKAN KEMBALI KADER
    // ==========================================

    public function reactivate(User $kader): JsonResponse
    {
        if (! in_array($kader->role, [UserRole::KADER, UserRole::KADER_ADMIN])) {
            return response()->json(['message' => 'Pengguna bukan kader.'], 422);
        }

        $kader->update(['is_active' => true]);

        AuditLogService::log('update', $kader, notes: "Kader diaktifkan kembali");

        return response()->json(['message' => 'Kader berhasil diaktifkan kembali.']);
    }
}

