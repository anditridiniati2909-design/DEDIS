<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use App\Models\Child;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Validation\ValidationException;

class AuthController extends Controller
{
    /**
     * POST /api/v1/register
     * Mendaftarkan pengguna baru (Kader / Orang Tua) dan mencatat balita jika ada
     */
    public function register(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'phone' => 'required|string|max:25',
            'password' => 'required|string|min:6',
            'email' => 'nullable|string|email|max:255',
            'role' => 'nullable|in:kader,orang_tua',
            'child_name' => 'nullable|string|max:255',
            'dusun' => 'nullable|string|max:255',
            'birth_date' => 'nullable',
            'is_mbg_recipient' => 'nullable|boolean',
        ]);

        $cleanPhone = preg_replace('/[^0-9]/', '', $validated['phone']);
        $email = $validated['email'] ?? ($cleanPhone . '@dedis.posyandu');
        $role = $validated['role'] ?? 'orang_tua';

        // Cek jika nomor HP sudah terdaftar
        $existingUser = User::where('phone', $cleanPhone)
            ->orWhere('phone', $validated['phone'])
            ->orWhere('email', $email)
            ->first();

        if ($existingUser) {
            // Update password dan profil jika user mendaftar ulang
            $existingUser->update([
                'name' => $validated['name'],
                'password' => Hash::make($validated['password']),
                'role' => $role,
                'phone' => $cleanPhone,
            ]);
            $user = $existingUser;
        } else {
            $user = User::create([
                'name' => $validated['name'],
                'email' => $email,
                'password' => Hash::make($validated['password']),
                'role' => $role,
                'phone' => $cleanPhone,
            ]);
        }

        // Jika ada nama balita, daftarkan balita ke buku register posyandu
        $child = null;
        if (!empty($validated['child_name'])) {
            $childName = trim($validated['child_name']);
            $motherName = trim($validated['name']);

            $child = Child::where('mother_name', $motherName)
                ->where('name', $childName)
                ->first();

            if (!$child) {
                // Generate kode kartu unik yang aman dari bentrokan ID
                $maxId = (int) (Child::max('id') ?? 0);
                do {
                    $maxId++;
                    $cardCode = 'DEDIS-' . str_pad($maxId, 3, '0', STR_PAD_LEFT);
                } while (Child::where('card_code', $cardCode)->exists());

                $birthDate = Carbon::now()->subMonths(12)->toDateString();
                if (!empty($request->input('birth_date'))) {
                    try {
                        $birthDate = Carbon::parse($request->input('birth_date'))->toDateString();
                    } catch (\Exception $e) {}
                }
                $isMbg = $request->boolean('is_mbg_recipient', true);

                $child = Child::create([
                    'card_code' => $cardCode,
                    'name' => $childName,
                    'gender' => $request->input('gender', 'L'),
                    'birth_date' => $birthDate,
                    'mother_name' => $motherName,
                    'hamlet' => $validated['dusun'] ?? 'Dusun Bontocinde',
                    'dusun' => $validated['dusun'] ?? 'Dusun Bontocinde',
                    'is_mbg_recipient' => $isMbg,
                ]);
            }
        }

        $token = $user->createToken('auth_token')->plainTextToken;

        return response()->json([
            'message' => 'Registrasi berhasil',
            'user' => $user,
            'child' => $child,
            'token' => $token,
            'token_type' => 'Bearer'
        ], 201);
    }

    /**
     * POST /api/v1/login
     * Masuk ke aplikasi menggunakan Nomor HP / Email dan Kata Sandi
     */
    public function login(Request $request)
    {
        $identifier = $request->input('phone') ?? $request->input('email');
        $password = $request->input('password');

        if (empty($identifier) || empty($password)) {
            return response()->json([
                'message' => 'Nomor WhatsApp / HP dan kata sandi wajib diisi.'
            ], 422);
        }

        $cleanPhone = preg_replace('/[^0-9]/', '', $identifier);

        $user = User::where('phone', $cleanPhone)
            ->orWhere('phone', $identifier)
            ->orWhere('email', $identifier)
            ->first();

        if (!$user || !Hash::check($password, $user->password)) {
            return response()->json([
                'message' => 'Nomor WhatsApp atau kata sandi tidak cocok.'
            ], 401);
        }

        // Cari balita milik orang tua ini berdasarkan nama ibu secara presisi (bukan substring parsial)
        $cleanName = trim($user->name);
        $cleanMother = trim(str_ireplace(['ibu ', 'bapak ', 'ayah ', 'mama ', 'papa '], '', $cleanName));

        $children = Child::where(function($q) use ($cleanName, $cleanMother) {
            $q->where('mother_name', $cleanName)
              ->orWhereRaw('LOWER(TRIM(mother_name)) = ?', [strtolower($cleanName)]);
            if (!empty($cleanMother)) {
                $q->orWhereRaw('LOWER(TRIM(REPLACE(REPLACE(REPLACE(mother_name, "Ibu ", ""), "Bapak ", ""), "Ayah ", ""))) = ?', [strtolower($cleanMother)]);
            }
        })
        ->with(['latestMeasurement', 'measurements'])
        ->get();

        $child = $children->first();

        // Buat access token baru
        $token = $user->createToken('auth_token')->plainTextToken;

        return response()->json([
            'message' => 'Login berhasil',
            'user' => [
                'id' => $user->id,
                'name' => $user->name,
                'phone' => $user->phone,
                'email' => $user->email,
                'role' => $user->role ?? 'orang_tua',
            ],
            'child' => $child,
            'children' => $children,
            'token' => $token,
            'token_type' => 'Bearer'
        ], 200);
    }

    /**
     * POST /api/v1/reset-password
     * Atur ulang kata sandi dengan verifikasi Nomor WhatsApp & Nama Balita
     */
    public function resetPassword(Request $request)
    {
        $validated = $request->validate([
            'phone' => 'required|string',
            'child_name' => 'required|string',
            'new_password' => 'required|string|min:6',
        ]);

        $cleanPhone = preg_replace('/[^0-9]/', '', $validated['phone']);

        $user = User::where('phone', $cleanPhone)
            ->orWhere('phone', $validated['phone'])
            ->first();

        if (!$user) {
            return response()->json([
                'message' => 'Nomor WhatsApp tidak terdaftar di sistem.'
            ], 404);
        }

        // Verifikasi nama balita (case insensitive)
        $childName = trim($validated['child_name']);
        $child = Child::whereRaw('LOWER(name) LIKE ?', ['%' . strtolower($childName) . '%'])->first();

        if (!$child) {
            return response()->json([
                'message' => 'Nama balita tidak sesuai dengan data posyandu.'
            ], 422);
        }

        // Perbarui kata sandi user
        $user->update([
            'password' => Hash::make($validated['new_password'])
        ]);

        return response()->json([
            'message' => 'Kata sandi berhasil diperbarui. Silakan masuk kembali.'
        ], 200);
    }

    /**
     * POST /api/v1/change-password
     * Ubah kata sandi dari profil akun (dengan verifikasi kata sandi lama)
     */
    public function changePassword(Request $request)
    {
        $validated = $request->validate([
            'phone' => 'nullable|string',
            'old_password' => 'required|string',
            'new_password' => 'required|string|min:6',
        ]);

        $user = $request->user();
        if (!$user && !empty($validated['phone'])) {
            $cleanPhone = preg_replace('/[^0-9]/', '', $validated['phone']);
            $user = User::where('phone', $cleanPhone)
                ->orWhere('phone', $validated['phone'])
                ->first();
        }

        if (!$user) {
            return response()->json([
                'message' => 'Pengguna tidak ditemukan.'
            ], 404);
        }

        if (!Hash::check($validated['old_password'], $user->password)) {
            return response()->json([
                'message' => 'Kata sandi lama tidak cocok.'
            ], 401);
        }

        $user->update([
            'password' => Hash::make($validated['new_password'])
        ]);

        return response()->json([
            'message' => 'Kata sandi berhasil diperbarui.'
        ], 200);
    }

    /**
     * POST /api/v1/logout
     * Menghapus sesi token user
     */
    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'message' => 'Logout berhasil, sesi telah dihapus.'
        ], 200);
    }

    /**
     * GET /api/v1/me
     * Mengambil info profil user yang sedang login
     */
    public function me(Request $request)
    {
        return response()->json($request->user(), 200);
    }
}
