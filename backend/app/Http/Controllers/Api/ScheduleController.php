<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\PosyanduSchedule;
use App\Services\AuditLogService;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class ScheduleController extends Controller
{
    // ==========================================
    // JADWAL MENDATANG (semua pengguna)
    // ==========================================

    public function upcoming(): JsonResponse
    {
        $schedules = PosyanduSchedule::upcoming()->limit(5)->get();
        return response()->json($schedules);
    }

    // ==========================================
    // BUAT JADWAL BARU (kader saja)
    // ==========================================

    public function store(Request $request): JsonResponse
    {
        $data = $request->validate([
            'title'          => ['required', 'string', 'max:255'],
            'scheduled_date' => ['required', 'date', 'after_or_equal:today'],
            'start_time'     => ['nullable', 'date_format:H:i'],
            'end_time'       => ['nullable', 'date_format:H:i', 'after:start_time'],
            'location'       => ['nullable', 'string', 'max:255'],
            'description'    => ['nullable', 'string', 'max:1000'],
        ]);

        $schedule = PosyanduSchedule::create($data + [
            'created_by'   => $request->user()->id,
            'is_cancelled' => false,
        ]);

        AuditLogService::log('create', $schedule);

        return response()->json($schedule, 201);
    }

    // ==========================================
    // BATALKAN JADWAL (kader saja)
    // ==========================================

    public function cancel(Request $request, PosyanduSchedule $schedule): JsonResponse
    {
        $request->validate([
            'cancelled_reason' => ['nullable', 'string', 'max:500'],
        ]);

        $schedule->update([
            'is_cancelled'     => true,
            'cancelled_reason' => $request->cancelled_reason,
        ]);

        AuditLogService::log('update', $schedule, notes: 'Jadwal dibatalkan');

        return response()->json(['message' => 'Jadwal berhasil dibatalkan.']);
    }

    // ==========================================
    // KONFIRMASI HADIR (orang tua)
    // Simpan konfirmasi kehadiran orang tua untuk jadwal tertentu.
    // ==========================================

    public function confirm(Request $request, PosyanduSchedule $schedule): JsonResponse
    {
        $userId = $request->user()->id;
        // Simpan konfirmasi (upsert berdasarkan user_id + schedule_id)
        \DB::table('schedule_confirmations')->updateOrInsert(
            ['user_id' => $userId, 'schedule_id' => $schedule->id],
            ['confirmed_at' => now(), 'cancelled_at' => null, 'updated_at' => now()]
        );
        return response()->json(['message' => 'Konfirmasi kehadiran disimpan.']);
    }

    // ==========================================
    // BATALKAN KONFIRMASI HADIR (orang tua)
    // ==========================================

    public function cancelConfirm(Request $request, PosyanduSchedule $schedule): JsonResponse
    {
        $userId = $request->user()->id;
        \DB::table('schedule_confirmations')->updateOrInsert(
            ['user_id' => $userId, 'schedule_id' => $schedule->id],
            ['cancelled_at' => now(), 'confirmed_at' => null, 'updated_at' => now()]
        );
        return response()->json(['message' => 'Konfirmasi kehadiran dibatalkan.']);
    }

    // ==========================================
    // TEKS BAGIKAN KE WHATSAPP (kader saja)
    // Mengembalikan teks pengumuman jadwal (hanya jadwal, TANPA data kesehatan anak).
    // Kader membuka WhatsApp sendiri via Android share intent.
    // ==========================================

    public function shareText(PosyanduSchedule $schedule): JsonResponse
    {
        $date = \Carbon\Carbon::parse($schedule->scheduled_date)->locale('id')->isoFormat('dddd, D MMMM YYYY');
        $time = $schedule->start_time ? substr($schedule->start_time, 0, 5) : '08.00';
        $endTime = $schedule->end_time ? ' - ' . substr($schedule->end_time, 0, 5) : '';
        $location = $schedule->location ?? 'Posyandu Anggrek 4';

        $text = "*Pengumuman Posyandu Anggrek 4*\n\n"
            . "Jadwal layanan posyandu:\n"
            . "Hari/Tanggal: {$date}\n"
            . "Waktu: {$time}{$endTime} WITA\n"
            . "Tempat: {$location}\n\n"
            . "Yang perlu dibawa:\n"
            . "- Buku KIA (Kesehatan Ibu dan Anak)\n"
            . "- Kartu posyandu atau tunjukkan QR anak di aplikasi DEDIS\n\n"
            . "Harap hadir tepat waktu. Terima kasih.\n"
            . "_Posyandu Anggrek 4, Dusun Bontocinde_";

        return response()->json(['share_text' => $text]);
    }
}

