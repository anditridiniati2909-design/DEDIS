package com.example.dedis.util

/**
 * PushNotificationHelper — Placeholder untuk integrasi Firebase Cloud Messaging (FCM).
 *
 * Cara mengaktifkan nanti:
 * 1. Tambahkan `com.google.firebase:firebase-messaging` ke build.gradle.kts.
 * 2. Download `google-services.json` dari Firebase Console, letakkan di folder `app/`.
 * 3. Implementasikan `sendPushNotification` menggunakan FCM HTTP v1 API lewat Cloud Function,
 *    BUKAN dari aplikasi Android secara langsung (agar server key tidak bocor).
 * 4. Backend Laravel: panggil Cloud Function / FCM API saat ada pesan baru.
 *
 * JANGAN menyimpan FCM server key di dalam aplikasi Android.
 */
object PushNotificationHelper {

    /**
     * Kirim notifikasi push ke pengguna tertentu.
     *
     * TODO: Implementasikan via backend Laravel → FCM HTTP v1 API.
     *       Endpoint backend: POST /api/v1/notifications/push
     *       Body: { "to_user_id": Long, "title": String, "body": String }
     *
     * @param toUserId ID pengguna penerima
     * @param title    Judul notifikasi
     * @param body     Isi notifikasi
     */
    fun sendPushNotification(toUserId: Long, title: String, body: String) {
        // TODO: Panggil endpoint backend untuk trigger FCM
        // Contoh:
        // apiService.sendPushNotification(PushNotificationRequest(toUserId, title, body))
    }

    /**
     * Lepas token registrasi FCM saat logout.
     * TODO: Saat FCM diaktifkan di Android, panggil FirebaseMessaging.getInstance().deleteToken()
     */
    fun unregisterToken() {
        // Placeholder implementasi pelepasan token FCM
    }
}

