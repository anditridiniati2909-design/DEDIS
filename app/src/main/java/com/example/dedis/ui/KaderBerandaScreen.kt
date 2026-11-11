package com.example.dedis.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.ui.components.DedisAvatar
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.components.DedisHeader
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.components.DedisStatusChip
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisDangerBg
import com.example.dedis.ui.theme.DedisDangerText
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWarningBg
import com.example.dedis.ui.theme.DedisWarningText
import com.example.dedis.ui.theme.DedisWhite

@Composable
fun KaderBerandaScreen(
    modifier: Modifier = Modifier,
    kaderName: String = "Sari",
    onNavigateToRoute: (String) -> Unit = {},
    onNavigateToDaftarBalita: () -> Unit = {},
    onNavigateToFormulir: () -> Unit = {},
    onNavigateToPengukuran: () -> Unit = {},
    onNavigateToPindaiQr: () -> Unit = {},
    onNavigateToDetailBalita: () -> Unit = {},
    onNavigateToInbox: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DedisWhite,
        bottomBar = {
            KaderBottomBar(
                currentRoute = DedisDestinations.KADER_BERANDA,
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
            // Header: DEDIS - RUANG KADER - Pesan Masuk (badge) + Lonceng
            DedisHeader(
                spaceRole = "RUANG KADER",
                title = null,
                subtitle = null,
                onMessageClick = onNavigateToInbox,
                unreadMessageCount = 2
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Sapaan: "Selamat bertugas, Sari", Posyandu Anggrek 4
                Column {
                    Text(
                        text = "Selamat bertugas, $kaderName",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DedisInk
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Posyandu Anggrek 4",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = DedisMuted
                    )
                }

                // Kartu Ringkasan: sudah diukur 92 dari 115 balita; Normal 80, Pendek 9, Sangat pendek 3
                DedisCard {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Ringkasan Posyandu September 2026",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DedisInk
                        )

                        // Angka Besar: 92 dari 115 balita
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "92",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = DedisPrimary
                            )
                            Text(
                                text = "/ 115 balita diukur",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DedisInk,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        // 3 Metrik status: Normal 80, Pendek 9, Sangat pendek 3
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Normal 80
                            StatusSummaryBox(
                                count = "80",
                                label = "Normal",
                                bgColor = DedisTint,
                                textColor = DedisPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            // Pendek 9
                            StatusSummaryBox(
                                count = "9",
                                label = "Pendek",
                                bgColor = DedisWarningBg,
                                textColor = DedisWarningText,
                                modifier = Modifier.weight(1f)
                            )
                            // Sangat pendek 3
                            StatusSummaryBox(
                                count = "3",
                                label = "Sangat pendek",
                                bgColor = DedisDangerBg,
                                textColor = DedisDangerText,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Tombol Utama: Mulai Layanan
                DedisPrimaryButton(
                    text = "Mulai Layanan Pengukuran",
                    onClick = onNavigateToPengukuran,
                    leadingIcon = R.drawable.ic_ruler_chart
                )

                // Aksi Cepat: Pindai QR, Tambah Balita, Lihat Balita
                Text(
                    text = "Aksi Cepat",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisInk
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "Pindai QR",
                        subtitle = "Cari anak cepat",
                        iconRes = R.drawable.ic_camera,
                        onClick = onNavigateToPindaiQr,
                        modifier = Modifier.weight(1f)
                    )

                    QuickActionCard(
                        title = "Tambah Balita",
                        subtitle = "Sasaran baru",
                        iconRes = R.drawable.ic_plus,
                        onClick = onNavigateToFormulir,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Daftar "Perlu ditinjau"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Perlu Ditinjau",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DedisInk
                    )

                    Text(
                        text = "Lihat semua",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DedisPrimary,
                        modifier = Modifier.clickable { onNavigateToDaftarBalita() }
                    )
                }

                // Item Perlu Ditinjau 1
                DedisCard(
                    contentPadding = 14.dp,
                    onClick = onNavigateToDetailBalita
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DedisAvatar(initial = "A", size = 44.dp)
                            Column {
                                Text(
                                    text = "Aisyah Putri",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DedisInk
                                )
                                Text(
                                    text = "24 bln • Ibu Nur Aini",
                                    fontSize = 12.sp,
                                    color = DedisMuted
                                )
                            }
                        }

                        DedisStatusChip(status = "Perlu perhatian", isSmall = true)
                    }
                }

                // Item Perlu Ditinjau 2
                DedisCard(
                    contentPadding = 14.dp,
                    onClick = onNavigateToDetailBalita
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DedisAvatar(initial = "S", size = 44.dp)
                            Column {
                                Text(
                                    text = "Siti Nurhaliza",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DedisInk
                                )
                                Text(
                                    text = "18 bln • Ibu Rahmawati",
                                    fontSize = 12.sp,
                                    color = DedisMuted
                                )
                            }
                        }

                        DedisStatusChip(status = "Risiko tinggi", isSmall = true)
                    }
                }

                // Berikan spacer bawah yang cukup agar kartu paling bawah bisa discroll leluasa di atas bottom bar
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun StatusSummaryBox(
    count: String,
    label: String,
    bgColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = count,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DedisCard(
        modifier = modifier,
        contentPadding = 14.dp,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DedisTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = DedisPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisInk
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = DedisMuted
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KaderBerandaScreenPreview() {
    DEDISTheme {
        KaderBerandaScreen()
    }
}
