<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Requests\Child\StoreChildRequest;
use App\Http\Resources\ChildResource;
use App\Models\Child;
use App\Models\ChildGuardianLink;
use App\Services\AuditLogService;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class ChildController extends Controller
{
    // ==========================================
    // DAFTAR BALITA
    // Orang tua: hanya anak yang ditautkan ke akun mereka
    // Kader: semua anak di posyandu
    // ==========================================

    public function index(Request $request): JsonResponse
    {
        $user = $request->user();

        if ($user->isOrangTua()) {
            // Ambil hanya anak yang dikaitkan via ChildGuardianLink
            $childIds = ChildGuardianLink::where('user_id', $user->id)
                ->pluck('child_id');
            $children = Child::whereIn('id', $childIds)
                ->with('measurements')
                ->orderBy('name')
                ->get();
        } else {
            // Kader melihat semua anak
            $children = Child::with('measurements')->orderBy('name')->get();
        }

        return response()->json(ChildResource::collection($children));
    }

    // ==========================================
    // DETAIL BALITA
    // ==========================================

    public function show(Request $request, Child $child): JsonResponse
    {
        $this->authorizeChildAccess($request, $child);

        return response()->json(new ChildResource($child->load('measurements')));
    }

    // ==========================================
    // TAMBAH BALITA (hanya kader)
    // ==========================================

    public function store(StoreChildRequest $request): JsonResponse
    {
        $child = Child::create($request->validated() + [
            'registered_by' => $request->user()->id,
        ]);

        AuditLogService::log('create', $child, newValues: $child->toArray());

        return response()->json(new ChildResource($child), 201);
    }

    // ==========================================
    // UPDATE BALITA (hanya kader)
    // ==========================================

    public function update(StoreChildRequest $request, Child $child): JsonResponse
    {
        $old = $child->toArray();
        $child->update($request->validated());

        AuditLogService::log('update', $child,
            oldValues: AuditLogService::diff($old, $child->fresh()->toArray()),
            newValues: $child->fresh()->toArray()
        );

        return response()->json(new ChildResource($child->fresh()));
    }

    // ==========================================
    // HAPUS BALITA (soft delete, hanya kader_admin)
    // ==========================================

    public function destroy(Request $request, Child $child): JsonResponse
    {
        AuditLogService::log('delete', $child, oldValues: $child->toArray());
        $child->delete();

        return response()->json(['message' => 'Data anak berhasil dihapus.']);
    }

    // ==========================================
    // TAUTKAN ANAK KE ORANG TUA (via link_code)
    // ==========================================

    public function linkChild(Request $request): JsonResponse
    {
        $request->validate([
            'link_code' => ['required', 'string', 'size:8'],
        ]);

        $child = Child::where('link_code', $request->link_code)->first();

        if (! $child) {
            return response()->json(['message' => 'Kode tautan tidak ditemukan.'], 404);
        }

        $user = $request->user();

        // Periksa apakah sudah ditautkan
        $existing = ChildGuardianLink::where('child_id', $child->id)
            ->where('user_id', $user->id)
            ->first();

        if ($existing) {
            return response()->json(['message' => 'Anak sudah ditautkan ke akun Anda.'], 422);
        }

        // Maksimal 2 wali per anak
        $guardianCount = ChildGuardianLink::where('child_id', $child->id)->count();
        if ($guardianCount >= 2) {
            return response()->json(['message' => 'Anak sudah memiliki 2 wali terdaftar.'], 422);
        }

        ChildGuardianLink::create([
            'child_id' => $child->id,
            'user_id'  => $user->id,
        ]);

        AuditLogService::log('create', null, notes: "Orang tua {$user->id} menautkan anak {$child->id}");

        return response()->json([
            'message' => 'Anak berhasil ditautkan ke akun Anda.',
            'child'   => new ChildResource($child),
        ]);
    }

    // ==========================================
    // QR CODE ANAK
    // Mengembalikan kode string yang sama dengan yang di-scan KaderPindaiQrScreen.
    // Format: "DEDIS-{child_id}-{registration_number}"
    // ==========================================

    public function showQr(Request $request, Child $child): JsonResponse
    {
        $this->authorizeChildAccess($request, $child);

        // Generate kode yang sama dengan yang di-scan KaderPindaiQrScreen
        $code = $child->card_code ?: ('DEDIS-' . $child->id . '-' . str_pad($child->id, 4, '0', STR_PAD_LEFT));

        return response()->json([
            'child_id'          => $child->id,
            'child_name'        => $child->name,
            'qr_code'           => $code,  // string ini diencode oleh ZXing di Android
        ]);
    }

    // ==========================================
    // Helper: validasi akses IDOR
    // ==========================================

    private function authorizeChildAccess(Request $request, Child $child): void
    {
        $user = $request->user();
        if ($user->isOrangTua()) {
            $linked = ChildGuardianLink::where('child_id', $child->id)
                ->where('user_id', $user->id)
                ->exists();
            if (! $linked) {
                abort(403, 'Akses ditolak. Anak tidak tautkan ke akun Anda.');
            }
        }
        // Kader bisa akses semua anak
    }
}

