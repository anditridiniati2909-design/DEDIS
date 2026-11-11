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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.ui.components.DedisAvatar
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.components.DedisChoiceChip
import com.example.dedis.ui.components.DedisHeader
import com.example.dedis.ui.components.DedisSecondaryButton
import com.example.dedis.ui.components.DedisStatusChip
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisLine
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisSoft
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWhite

@Composable
fun OrangTuaBerandaScreen(
    modifier: Modifier = Modifier,
    parentName: String = "Ibu Nur Aini",
    onNavigateToRoute: (String) -> Unit = {},
    onNavigateToDetailBalita: () -> Unit = {},
    onNavigateToImunisasi: () -> Unit = {}
) {
    var selectedChild by remember { mutableStateOf("Aisyah") }
    var showQrDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DedisWhite,
        bottomBar = {
            OrangTuaBottomBar(
                currentRoute = DedisDestinations.BERANDA,
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
            // Header: DEDIS - RUANG ORANG TUA - Lonceng
            DedisHeader(
                spaceRole = "RUANG ORANG TUA",
                title = null,
                subtitle = null
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Sapaan
                Column {
                    Text(
                        text = "Halo, $parentName",
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

                // Pilihan anak (chip Aisyah / Nabil)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DedisChoiceChip(
                        text = "Aisyah Putri (24 bln)",
                        isSelected = selectedChild == "Aisyah",
                        onClick = { selectedChild = "Aisyah" }
                    )
                    DedisChoiceChip(
                        text = "Nabil (8 bln)",
                        isSelected = selectedChild == "Nabil",
                        onClick = { selectedChild = "Nabil" }
                    )
                }

                // Kartu Anak Utama
                DedisCard {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Baris profil anak: inisial, nama, umur, status chip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                DedisAvatar(
                                    initial = if (selectedChild == "Aisyah") "A" else "N",
                                    size = 46.dp
                                )
                                Column {
                                    Text(
                                        text = if (selectedChild == "Aisyah") "Aisyah Putri" else "Nabil Pratama",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DedisInk
                                    )
                                    Text(
                                        text = if (selectedChild == "Aisyah") "24 bulan • Perempuan" else "8 bulan • Laki-laki",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = DedisMuted
                                    )
                                }
                            }

                            DedisStatusChip(
                                status = if (selectedChild == "Aisyah") "Perlu perhatian" else "Normal"
                            )
                        }

                        // Dua angka besar (berat kg, tinggi cm)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .background(DedisTint, RoundedCornerShape(14.dp))
                                    .padding(vertical = 12.dp)
                            ) {
                                Text(
                                    text = "Berat Badan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = DedisMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = if (selectedChild == "Aisyah") "10,2" else "8,4",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DedisInk
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "kg",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DedisMuted,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .background(DedisTint, RoundedCornerShape(14.dp))
                                    .padding(vertical = 12.dp)
                            ) {
                                Text(
                                    text = "Tinggi Badan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = DedisMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = if (selectedChild == "Aisyah") "79,2" else "71,0",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DedisInk
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "cm",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DedisMuted,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }
                        }

                        // Tombol "Lihat grafik"
                        DedisSecondaryButton(
                            text = "Lihat grafik",
                            onClick = onNavigateToDetailBalita,
                            leadingIcon = R.drawable.ic_ruler_chart
                        )
                    }
                }

                // Kartu jadwal posyandu berikutnya
                DedisCard(
                    onClick = { onNavigateToRoute(DedisDestinations.JADWAL) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(DedisTint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_calendar),
                                    contentDescription = null,
                                    tint = DedisPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Posyandu berikutnya",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = DedisMuted
                                )
                                Text(
                                    text = "Rabu, 7 Oktober",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DedisInk
                                )
                                Text(
                                    text = "08.00 - 11.00 • Posyandu Anggrek 4",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = DedisMuted
                                )
                            }
                        }

                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_right),
                            contentDescription = "Buka Jadwal",
                            tint = DedisMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Menu cepat 4 ikon: Imunisasi, Edukasi, Chat kader, QR anak
                Text(
                    text = "Menu Cepat",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisInk
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickMenuItem(
                        label = "Imunisasi",
                        iconRes = R.drawable.ic_vaccine,
                        onClick = onNavigateToImunisasi
                    )
                    QuickMenuItem(
                        label = "Edukasi",
                        iconRes = R.drawable.ic_book_education,
                        onClick = { onNavigateToRoute(DedisDestinations.EDUKASI) }
                    )
                    QuickMenuItem(
                        label = "Chat kader",
                        iconRes = R.drawable.ic_chat,
                        onClick = { onNavigateToRoute(DedisDestinations.PESAN) }
                    )
                    QuickMenuItem(
                        label = "QR anak",
                        iconRes = R.drawable.ic_qr_code,
                        onClick = { showQrDialog = true }
                    )
                }

                // Spacer bawah agar konten paling bawah bisa discroll leluasa di atas bottom bar
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }

    // Dialog QR Anak
    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            confirmButton = {
                TextButton(onClick = { showQrDialog = false }) {
                    Text("Tutup", color = DedisPrimary, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "QR Anak - Aisyah Putri",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisInk
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tunjukkan QR ini kepada kader posyandu saat pencatatan.",
                        fontSize = 13.sp,
                        color = DedisMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .background(DedisTint, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_qr_code),
                            contentDescription = "QR Code",
                            tint = DedisPrimary,
                            modifier = Modifier.size(110.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "ID: DEDIS-2024-0012",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DedisInk
                    )
                }
            },
            containerColor = DedisWhite,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
private fun QuickMenuItem(
    label: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(DedisTint),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = DedisPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = DedisInk
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OrangTuaBerandaScreenPreview() {
    DEDISTheme {
        OrangTuaBerandaScreen()
    }
}
