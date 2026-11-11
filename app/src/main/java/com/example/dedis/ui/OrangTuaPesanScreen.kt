package com.example.dedis.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.ui.components.DedisHeader
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisLine
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisSoft
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWhite

data class ChatMessage(
    val id: String,
    val senderId: String,  // "me" or partner id
    val senderName: String,
    val content: String,
    val timeLabel: String,
    val isFromMe: Boolean
)

@Composable
fun OrangTuaPesanScreen(
    modifier: Modifier = Modifier,
    currentUserId: String = "me",
    onNavigateToRoute: (String) -> Unit = {}
) {
    var messageText by remember { mutableStateOf("") }
    var selectedPartner by remember { mutableStateOf("kader") } // "kader" or "bidan"

    // Pesan dibuat dalam list yang bisa berubah (dalam implementasi nyata: diambil dari API)
    val messages = remember {
        mutableStateListOf(
            ChatMessage("1", "kader", "Kader Sari",
                "Selamat pagi Ibu Nur Aini. Jangan lupa jadwal posyandu Aisyah hari Rabu, 7 Oktober ya bu. Mohon bawa Buku KIA-nya.",
                "Hari ini, 09.15", false),
            ChatMessage("2", "me", "Saya",
                "Selamat pagi Bu Sari. Baik, kami akan hadir jam 8 pagi bersama Aisyah. Terima kasih sudah diingatkan.",
                "09.17", true)
        )
    }
    val bidanMessages = remember { mutableStateListOf<ChatMessage>() }

    val activeMessages = if (selectedPartner == "kader") messages else bidanMessages
    val listState = rememberLazyListState()

    // Scroll to bottom when messages change
    LaunchedEffect(activeMessages.size) {
        if (activeMessages.isNotEmpty()) {
            listState.animateScrollToItem(activeMessages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DedisWhite,
        bottomBar = {
            OrangTuaBottomBar(
                currentRoute = DedisDestinations.PESAN,
                onNavigateToRoute = onNavigateToRoute
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            DedisHeader(
                spaceRole = "RUANG ORANG TUA",
                title = "Pesan & Konsultasi",
                subtitle = "Komunikasi langsung dengan kader dan bidan"
            )

            // Chip pilih penerima — aktif = primary, tidak aktif = putih + border line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ChatPartnerChip(
                    label = "Kader Sari",
                    isSelected = selectedPartner == "kader",
                    onClick = { selectedPartner = "kader" }
                )
                ChatPartnerChip(
                    label = "Bidan Rina",
                    isSelected = selectedPartner == "bidan",
                    onClick = { selectedPartner = "bidan" }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Daftar pesan
            if (activeMessages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada pesan. Kirim pesan pertama Anda.",
                        fontSize = 13.sp,
                        color = DedisMuted
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // Penanda tanggal pertama
                    item {
                        Text(
                            text = activeMessages.first().timeLabel.substringBefore(",").let {
                                if (it.length > 10) activeMessages.first().timeLabel else it
                            },
                            fontSize = 11.sp,
                            color = DedisMuted,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    items(activeMessages) { msg ->
                        if (msg.isFromMe) {
                            // Pesan dari saya — kanan, hijau primary
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.82f)
                                        .background(DedisPrimary, RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(msg.content, fontSize = 13.sp, color = DedisWhite, lineHeight = 18.sp)
                                }
                            }
                        } else {
                            // Pesan dari partner — kiri, tint
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.82f)
                                        .background(DedisTint, RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Text(msg.senderName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DedisPrimary)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(msg.content, fontSize = 13.sp, color = DedisInk, lineHeight = 18.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Input bar — tombol kirim ikon panah (bukan centang), nonaktif jika teks kosong
            Surface(
                color = DedisWhite,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Custom text field (agar bisa multiline & terlihat rapi)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(DedisTint, RoundedCornerShape(24.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (messageText.isEmpty()) {
                            Text("Tulis pesan...", fontSize = 14.sp, color = DedisMuted)
                        }
                        BasicTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            textStyle = TextStyle(fontSize = 14.sp, color = DedisInk),
                            cursorBrush = SolidColor(DedisPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Tombol kirim — ikon panah bukan centang; abu-abu jika kosong
                    val canSend = messageText.isNotBlank()
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (canSend) DedisPrimary else DedisLine)
                            .clickable(enabled = canSend) {
                                val newMsg = ChatMessage(
                                    id = System.currentTimeMillis().toString(),
                                    senderId = "me",
                                    senderName = "Saya",
                                    content = messageText.trim(),
                                    timeLabel = "Baru saja",
                                    isFromMe = true
                                )
                                if (selectedPartner == "kader") messages.add(newMsg)
                                else bidanMessages.add(newMsg)
                                messageText = ""
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_send),
                            contentDescription = "Kirim",
                            tint = DedisWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/** Chip penerima: aktif = primary solid; tidak aktif = putih + border line (bukan abu-abu). */
@Composable
private fun ChatPartnerChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) DedisPrimary else DedisWhite,
        shape = RoundedCornerShape(100.dp),
        shadowElevation = 0.dp,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, DedisLine) else null,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) DedisWhite else DedisInk,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OrangTuaPesanScreenPreview() {
    DEDISTheme { OrangTuaPesanScreen() }
}
