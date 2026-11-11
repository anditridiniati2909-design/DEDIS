package com.example.dedis.ui

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dedis.R
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.components.DedisChoiceChip
import com.example.dedis.ui.components.DedisHeader
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.components.DedisSecondaryButton
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisLine
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisSoft
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWhite
import com.example.dedis.worker.ReminderWorker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

@Composable
fun OrangTuaJadwalScreen(
    modifier: Modifier = Modifier,
    // Data jadwal (akan diambil dari backend; ini nilai default/contoh)
    scheduleId: Long = 1L,
    scheduleDate: String = "Rabu, 7 Oktober",
    scheduleTime: String = "08.00 - 11.00 WITA",
    scheduleLocation: String = "Balai Warga • Posyandu Anggrek 4",
    // H-3 date labels
    hMinus3Label: String = "H-3 (4 Okt)",
    hMinus1Label: String = "H-1 (6 Okt)",
    // Tanggal aktual untuk WorkManager (isi dari backend)
    hMinus3Date: LocalDate = LocalDate.now().plusDays(3),
    hMinus1Date: LocalDate = LocalDate.now().plusDays(1),
    onNavigateToRoute: (String) -> Unit = {},
    onConfirmAttendance: () -> Unit = {},
    onCancelAttendance: () -> Unit = {}
) {
    var isReminderActive by remember { mutableStateOf(false) }
    var reminderOffset by remember { mutableStateOf("H-1") }
    var isConfirmed by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Runtime permission launcher for Android 13+ POST_NOTIFICATIONS
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) scheduleReminder(context, scheduleId, scheduleDate, reminderOffset, hMinus3Date, hMinus1Date)
    }

    fun handleReminderToggle(active: Boolean) {
        isReminderActive = active
        if (active) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                scheduleReminder(context, scheduleId, scheduleDate, reminderOffset, hMinus3Date, hMinus1Date)
            }
        } else {
            WorkManager.getInstance(context).cancelAllWorkByTag("reminder_$scheduleId")
        }
    }

    fun handleOffsetChange(offset: String) {
        reminderOffset = offset
        if (isReminderActive) {
            WorkManager.getInstance(context).cancelAllWorkByTag("reminder_$scheduleId")
            scheduleReminder(context, scheduleId, scheduleDate, offset, hMinus3Date, hMinus1Date)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DedisWhite,
        bottomBar = {
            OrangTuaBottomBar(
                currentRoute = DedisDestinations.JADWAL,
                onNavigateToRoute = onNavigateToRoute
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            DedisHeader(
                spaceRole = "RUANG ORANG TUA",
                title = "Jadwal",
                subtitle = "Jadwal pelayanan posyandu dan imunisasi"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Kartu Hijau: Posyandu Berikutnya
                DedisCard(backgroundColor = DedisPrimary, borderColor = DedisPrimary, contentPadding = 20.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Posyandu berikutnya", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = DedisSoft)
                        Text(scheduleDate, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DedisWhite)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(painterResource(R.drawable.ic_clock), null, tint = DedisSoft, modifier = Modifier.size(18.dp))
                            Text(scheduleTime, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DedisWhite)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(painterResource(R.drawable.ic_home), null, tint = DedisSoft, modifier = Modifier.size(18.dp))
                            Text(scheduleLocation, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DedisWhite)
                        }
                    }
                }

                // Kartu Ingatkan Saya – sakelar + chip H-3/H-1
                DedisCard {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Ingatkan saya", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DedisInk)
                                Text("Notifikasi pengingat sebelum jadwal", fontSize = 12.sp, color = DedisMuted)
                            }
                            Switch(
                                checked = isReminderActive,
                                onCheckedChange = { handleReminderToggle(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = DedisWhite,
                                    checkedTrackColor = DedisPrimary,
                                    uncheckedThumbColor = DedisMuted,
                                    uncheckedTrackColor = DedisTint
                                )
                            )
                        }

                        if (isReminderActive) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                DedisChoiceChip(
                                    text = hMinus3Label,
                                    isSelected = reminderOffset == "H-3",
                                    onClick = { handleOffsetChange("H-3") }
                                )
                                DedisChoiceChip(
                                    text = hMinus1Label,
                                    isSelected = reminderOffset == "H-1",
                                    onClick = { handleOffsetChange("H-1") }
                                )
                            }
                            Text(
                                text = "Pengingat akan muncul pukul 08.00",
                                fontSize = 12.sp,
                                color = DedisMuted
                            )
                        }
                    }
                }

                // Kartu Yang perlu dibawa
                DedisCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Yang perlu dibawa", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DedisInk)
                        BawaItem("Buku KIA (Kesehatan Ibu dan Anak)", "Untuk pencatatan imunisasi dan KMS")
                        BawaItem("Kartu posyandu", "Atau tunjukkan QR anak di aplikasi")
                    }
                }

                // Tombol Konfirmasi Hadir / Sudah Dikonfirmasi
                if (isConfirmed) {
                    DedisSecondaryButton(
                        text = "Sudah dikonfirmasi",
                        leadingIcon = R.drawable.ic_check,
                        onClick = {
                            isConfirmed = false
                            onCancelAttendance()
                        }
                    )
                } else {
                    DedisPrimaryButton(
                        text = "Konfirmasi Akan Hadir",
                        leadingIcon = R.drawable.ic_check,
                        onClick = {
                            isConfirmed = true
                            onConfirmAttendance()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun BawaItem(title: String, subtitle: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(DedisTint),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(R.drawable.ic_check), null, tint = DedisPrimary, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DedisInk)
            Text(subtitle, fontSize = 12.sp, color = DedisMuted)
        }
    }
}

/** Jadwalkan WorkManager pada waktu target pukul 08:00. */
private fun scheduleReminder(
    context: Context,
    scheduleId: Long,
    scheduleDate: String,
    offset: String,
    hMinus3Date: LocalDate,
    hMinus1Date: LocalDate
) {
    val targetDate = if (offset == "H-3") hMinus3Date else hMinus1Date
    val targetDateTime = LocalDateTime.of(targetDate.year, targetDate.monthValue, targetDate.dayOfMonth, 8, 0)
    val delayMs = targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis()
    if (delayMs <= 0) return

    val data = Data.Builder()
        .putString(ReminderWorker.KEY_SCHEDULE_DATE, scheduleDate)
        .putString(ReminderWorker.KEY_OFFSET_LABEL, offset)
        .putLong(ReminderWorker.KEY_SCHEDULE_ID, scheduleId)
        .build()

    val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .addTag("reminder_$scheduleId")
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}

@Preview(showBackground = true)
@Composable
fun OrangTuaJadwalScreenPreview() {
    DEDISTheme { OrangTuaJadwalScreen() }
}
