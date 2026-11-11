package com.example.dedis.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dedis.R

/**
 * ReminderWorker — digunakan oleh WorkManager untuk mengirim
 * notifikasi lokal pengingat jadwal Posyandu pada waktu yang ditentukan (08:00 pagi).
 *
 * Data yang diperlukan (workDataOf):
 *   - KEY_SCHEDULE_DATE  : String "Rabu, 7 Oktober"
 *   - KEY_OFFSET_LABEL   : String "H-3" atau "H-1 (besok)"
 *   - KEY_SCHEDULE_ID    : Long
 */
class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "dedis_schedule_reminder"
        const val KEY_SCHEDULE_DATE  = "schedule_date"
        const val KEY_OFFSET_LABEL   = "offset_label"
        const val KEY_SCHEDULE_ID    = "schedule_id"
    }

    override suspend fun doWork(): Result {
        val scheduleDate = inputData.getString(KEY_SCHEDULE_DATE) ?: "jadwal"
        val offsetLabel  = inputData.getString(KEY_OFFSET_LABEL)  ?: "H-3"

        createNotificationChannel()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_calendar)
            .setContentTitle("Pengingat Posyandu Anggrek 4 ($offsetLabel)")
            .setContentText("$scheduleDate, 08.00 WITA. Jangan lupa bawa Buku KIA.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Posyandu Anggrek 4 ($offsetLabel), $scheduleDate, pukul 08.00 WITA di Balai Warga. Bawa Buku KIA dan kartu posyandu.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(inputData.getLong(KEY_SCHEDULE_ID, 0L).toInt(), notification)
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pengingat Jadwal Posyandu",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi pengingat jadwal layanan Posyandu Anggrek 4"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}

