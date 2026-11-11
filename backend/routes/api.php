<?php

use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\ChildController;
use App\Http\Controllers\Api\KaderController;
use App\Http\Controllers\Api\MeasurementController;
use App\Http\Controllers\Api\MessageController;
use App\Http\Controllers\Api\NotificationController;
use App\Http\Controllers\Api\PasswordController;
use App\Http\Controllers\Api\ProfileController;
use App\Http\Controllers\Api\ReportController;
use App\Http\Controllers\Api\ScheduleController;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| DEDIS API Routes — versi 1 (/api/v1/)
|--------------------------------------------------------------------------
|
| Semua route dilindungi CORS + rate limiting (konfigurasi di bootstrap/app.php).
| Peran divalidasi SERVER-SIDE. Klien TIDAK boleh menentukan peran.
|
| Middleware:
|   auth:sanctum          → wajib login (token Sanctum)
|   role:kader,kader_admin → hanya kader atau kader_admin
|   role:kader_admin       → hanya kader_admin (ketua/bidan)
|   prevent.role.escalation → tolak field role/is_active dari request
|
*/

Route::prefix('v1')->group(function () {

    // ==========================================
    // PUBLIK — tidak butuh autentikasi
    // ==========================================
    Route::prefix('auth')->group(function () {
        Route::post('login',          [AuthController::class, 'login']);
        Route::post('register',       [AuthController::class, 'register']);
        Route::post('activate-kader', [AuthController::class, 'activateKader']);
    });

    // ==========================================
    // TERAUTENTIKASI — butuh token Sanctum
    // ==========================================
    Route::middleware(['auth:sanctum', 'prevent.role.escalation'])->group(function () {

        // Profil pengguna sendiri
        Route::get('auth/me',      [AuthController::class, 'me']);
        Route::post('auth/logout', [AuthController::class, 'logout']);

        // ==========================================
        // PROFIL & AKUN
        // ==========================================
        Route::get('profile',  [ProfileController::class, 'show']);
        Route::put('profile',  [ProfileController::class, 'update']);

        // Ubah kata sandi
        Route::post('password', [PasswordController::class, 'change']);

        // Preferensi notifikasi
        Route::get('notifications',  [NotificationController::class, 'index']);
        Route::put('notifications',  [NotificationController::class, 'update']);

        // ==========================================
        // PESAN & KONSULTASI
        // ==========================================
        Route::get('messages',              [MessageController::class, 'index']);   // daftar percakapan
        Route::get('messages/{partnerId}',  [MessageController::class, 'show']);    // pesan dengan partner tertentu + mark-read
        Route::post('messages',             [MessageController::class, 'store']);   // kirim pesan

        // ==========================================
        // JADWAL POSYANDU
        // ==========================================
        Route::get('schedules',             [ScheduleController::class, 'upcoming']);
        Route::post('schedules',            [ScheduleController::class, 'store'])
            ->middleware('role:kader,kader_admin');
        Route::patch('schedules/{schedule}/cancel', [ScheduleController::class, 'cancel'])
            ->middleware('role:kader,kader_admin');

        // Konfirmasi kehadiran orang tua
        Route::post('schedules/{schedule}/confirm',   [ScheduleController::class, 'confirm']);
        Route::delete('schedules/{schedule}/confirm', [ScheduleController::class, 'cancelConfirm']);

        // ==========================================
        // ORANG TUA — daftar & detail anak sendiri
        // ==========================================
        Route::prefix('children')->group(function () {
            Route::get('/',              [ChildController::class, 'index']);
            Route::get('{child}',        [ChildController::class, 'show']);
            Route::post('link',          [ChildController::class, 'linkChild']); // tautkan via link_code

            // QR code anak (string kode, dipakai ZXing di Android)
            Route::get('{child}/qr',     [ChildController::class, 'showQr']);

            // Pengukuran (orang tua hanya bisa lihat, tidak bisa catat)
            Route::get('{child}/measurements',                   [MeasurementController::class, 'index']);
            Route::get('{child}/measurements/{measurement}',     [MeasurementController::class, 'show']);

            // Laporan tren pertumbuhan individu
            Route::get('{child}/growth-trend', [ReportController::class, 'childGrowthTrend']);
        });

        // ==========================================
        // KADER — tambah, edit, catat pengukuran
        // ==========================================
        Route::middleware('role:kader,kader_admin')->group(function () {
            Route::post('children',           [ChildController::class, 'store']);
            Route::put('children/{child}',    [ChildController::class, 'update']);

            // Catat pengukuran baru (append-only)
            Route::post('children/{child}/measurements', [MeasurementController::class, 'store']);

            // Bagikan jadwal ke WhatsApp (buka share intent, tidak menyimpan token WA)
            Route::get('schedules/{schedule}/share-text', [ScheduleController::class, 'shareText']);

            // Laporan bulanan
            Route::get('reports/monthly', [ReportController::class, 'monthlySummary']);
        });

        // ==========================================
        // KADER ADMIN — manajemen kader, hapus data
        // ==========================================
        Route::middleware('role:kader_admin')->group(function () {
            Route::delete('children/{child}',          [ChildController::class, 'destroy']);

            // Manajemen kader
            Route::get('kader',                               [KaderController::class, 'index']);
            Route::post('kader/invitations',                  [KaderController::class, 'createInvitation']);
            Route::get('kader/invitations',                   [KaderController::class, 'listInvitations']);
            Route::patch('kader/{kader}/deactivate',          [KaderController::class, 'deactivate']);
            Route::patch('kader/{kader}/reactivate',          [KaderController::class, 'reactivate']);
        });
    });
});
