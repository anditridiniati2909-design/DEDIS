package com.example.dedis.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisLine
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisWhite
import kotlinx.coroutines.launch

/**
 * Layar Pengaturan Notifikasi.
 * 4 sakelar: Pengingat jadwal, Pesan baru, Hasil pengukuran, Pengingat lewat WhatsApp.
 * Simpan preferensi ke backend via PUT /api/v1/notifications.
 */
@Composable
fun NotifikasiScreen(
    modifier: Modifier = Modifier,
    initialReminderSchedule: Boolean = false,
    initialNewMessage: Boolean = false,
    initialNewMeasurement: Boolean = false,
    initialWhatsappReminder: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onSimpan: (reminderSchedule: Boolean, newMessage: Boolean, newMeasurement: Boolean, whatsappReminder: Boolean) -> Unit = { _, _, _, _ -> }
) {
    var reminderSchedule by remember { mutableStateOf(initialReminderSchedule) }
    var newMessage by remember { mutableStateOf(initialNewMessage) }
    var newMeasurement by remember { mutableStateOf(initialNewMeasurement) }
    var whatsappReminder by remember { mutableStateOf(initialWhatsappReminder) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize().navigationBarsPadding(),
        containerColor = DedisWhite,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Kembali",
                        tint = DedisPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column {
                Text("Notifikasi", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = DedisInk)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Atur pengingat yang ingin Anda terima", fontSize = 13.sp, color = DedisMuted)
            }

            DedisCard(contentPadding = 4.dp) {
                Column {
                    NotifToggleItem(
                        title = "Pengingat jadwal",
                        subtitle = "Notifikasi H-3 dan H-1 sebelum posyandu",
                        checked = reminderSchedule,
                        onCheckedChange = { reminderSchedule = it }
                    )
                    androidx.compose.material3.Divider(color = DedisLine, thickness = 1.dp)
                    NotifToggleItem(
                        title = "Pesan baru",
                        subtitle = "Saat kader atau bidan mengirim pesan",
                        checked = newMessage,
                        onCheckedChange = { newMessage = it }
                    )
                    androidx.compose.material3.Divider(color = DedisLine, thickness = 1.dp)
                    NotifToggleItem(
                        title = "Hasil pengukuran",
                        subtitle = "Saat kader mencatat pengukuran anak Anda",
                        checked = newMeasurement,
                        onCheckedChange = { newMeasurement = it }
                    )
                    androidx.compose.material3.Divider(color = DedisLine, thickness = 1.dp)
                    NotifToggleItem(
                        title = "Pengingat lewat WhatsApp",
                        subtitle = "Setujui pengiriman pengingat ke nomor Anda (default: mati)",
                        checked = whatsappReminder,
                        onCheckedChange = { whatsappReminder = it }
                    )
                }
            }

            DedisPrimaryButton(
                text = "Simpan Pengaturan",
                onClick = {
                    onSimpan(reminderSchedule, newMessage, newMeasurement, whatsappReminder)
                    scope.launch {
                        snackbarHostState.showSnackbar("Pengaturan notifikasi disimpan!")
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun NotifToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DedisInk)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 12.sp, color = DedisMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = DedisPrimary,
                uncheckedTrackColor = DedisLine
            )
        )
    }
}

