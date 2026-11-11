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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.ui.components.DedisAvatar
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisDangerBg
import com.example.dedis.ui.theme.DedisDangerText
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisLine
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWhite

data class ConversationPreview(
    val partnerId: String,
    val partnerName: String,
    val childName: String,
    val lastMessage: String,
    val lastAt: String,
    val unreadCount: Int
)

/**
 * KaderInboxScreen — Daftar percakapan masuk untuk kader.
 * Dibuka dari ikon pesan di header Beranda kader (badge jumlah pesan belum dibaca).
 * Tab navigasi bawah kader TIDAK berubah.
 */
@Composable
fun KaderInboxScreen(
    modifier: Modifier = Modifier,
    conversations: List<ConversationPreview> = sampleConversations(),
    onNavigateBack: () -> Unit = {},
    onOpenConversation: (partnerId: String, partnerName: String) -> Unit = { _, _ -> }
) {
    Scaffold(
        modifier = modifier.fillMaxSize().navigationBarsPadding(),
        containerColor = DedisWhite,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DedisWhite)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Kembali",
                            tint = DedisPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pesan Masuk", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DedisInk)

                    val totalUnread = conversations.sumOf { it.unreadCount }
                    if (totalUnread > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(DedisDangerText),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (totalUnread > 9) "9+" else totalUnread.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DedisWhite
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (conversations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada pesan masuk", fontSize = 14.sp, color = DedisMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(conversations) { conv ->
                    ConversationItem(
                        conv = conv,
                        onClick = { onOpenConversation(conv.partnerId, conv.partnerName) }
                    )
                    Divider(color = DedisLine, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun ConversationItem(conv: ConversationPreview, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DedisAvatar(
            initial = conv.partnerName.firstOrNull()?.uppercase() ?: "?",
            size = 46.dp
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = conv.partnerName,
                    fontSize = 14.sp,
                    fontWeight = if (conv.unreadCount > 0) FontWeight.Bold else FontWeight.Medium,
                    color = DedisInk
                )
                Text(text = conv.lastAt, fontSize = 11.sp, color = DedisMuted)
            }
            Text(
                text = "Anak: ${conv.childName}",
                fontSize = 11.sp,
                color = DedisMuted
            )
            Text(
                text = conv.lastMessage,
                fontSize = 12.sp,
                color = if (conv.unreadCount > 0) DedisInk else DedisMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (conv.unreadCount > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(DedisPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conv.unreadCount.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisWhite
                )
            }
        }
    }
}

/**
 * KaderChatScreen — Tampilan percakapan kader dengan satu orang tua.
 * Dibuka dari KaderInboxScreen.
 */
@Composable
fun KaderChatScreen(
    modifier: Modifier = Modifier,
    partnerName: String = "Ibu Nur Aini",
    onNavigateBack: () -> Unit = {}
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage("1", "partner", partnerName,
                "Bu Sari, apakah ada hasil pengukuran Aisyah bulan ini?",
                "Hari ini, 09.10", false)
        )
    }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        modifier = modifier.fillMaxSize().navigationBarsPadding(),
        containerColor = DedisWhite,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DedisWhite)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(painterResource(R.drawable.ic_arrow_back), "Kembali", tint = DedisPrimary, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                DedisAvatar(initial = partnerName.firstOrNull()?.uppercase() ?: "?", size = 36.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(partnerName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DedisInk)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    if (msg.isFromMe) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
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
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
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

            Surface(color = DedisWhite, shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(DedisTint, RoundedCornerShape(24.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (messageText.isEmpty()) Text("Tulis pesan...", fontSize = 14.sp, color = DedisMuted)
                        BasicTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            textStyle = TextStyle(fontSize = 14.sp, color = DedisInk),
                            cursorBrush = SolidColor(DedisPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    val canSend = messageText.isNotBlank()
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (canSend) DedisPrimary else DedisLine)
                            .clickable(enabled = canSend) {
                                messages.add(
                                    ChatMessage(
                                        id = System.currentTimeMillis().toString(),
                                        senderId = "kader",
                                        senderName = "Kader",
                                        content = messageText.trim(),
                                        timeLabel = "Baru saja",
                                        isFromMe = true
                                    )
                                )
                                messageText = ""
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painterResource(R.drawable.ic_send), "Kirim", tint = DedisWhite, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

private fun sampleConversations() = listOf(
    ConversationPreview("1", "Ibu Nur Aini", "Aisyah Putri", "Kapan jadwal ukur ulang bu?", "09.15", 2),
    ConversationPreview("2", "Ibu Rahmawati", "Siti Nurhaliza", "Terima kasih Bu Sari", "Kemarin", 0)
)

@Preview(showBackground = true)
@Composable
fun KaderInboxScreenPreview() {
    DEDISTheme { KaderInboxScreen() }
}

