package com.example.dedis.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.components.DedisChoiceChip
import com.example.dedis.ui.components.DedisHeader
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.components.DedisStatusChip
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWhite

@Composable
fun OrangTuaImunisasiScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavigateToJadwal: () -> Unit = {},
    onNavigateToRoute: (String) -> Unit = {}
) {
    var selectedChild by remember { mutableStateOf("Aisyah") }

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
            // Header
            DedisHeader(
                spaceRole = "RUANG ORANG TUA",
                title = "Imunisasi",
                subtitle = "Catatan imunisasi dan vaksinasi anak",
                showBackButton = true,
                onBackClick = onNavigateBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Chip pilihan anak
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DedisChoiceChip(
                        text = "Aisyah Putri",
                        isSelected = selectedChild == "Aisyah",
                        onClick = { selectedChild = "Aisyah" }
                    )
                    DedisChoiceChip(
                        text = "Nabil Pratama",
                        isSelected = selectedChild == "Nabil",
                        onClick = { selectedChild = "Nabil" }
                    )
                }

                // Kartu Tint: "Akan datang" (Peninjauan Buku KIA, 7 Okt 08.00)
                DedisCard(
                    backgroundColor = DedisTint,
                    borderColor = DedisTint,
                    contentPadding = 18.dp
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
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(DedisWhite),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_vaccine),
                                    contentDescription = null,
                                    tint = DedisPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Akan datang",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DedisPrimary
                                )
                                Text(
                                    text = "Peninjauan Buku KIA",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DedisInk
                                )
                                Text(
                                    text = "7 Oktober 2026 • 08.00",
                                    fontSize = 12.sp,
                                    color = DedisMuted
                                )
                            }
                        }

                        DedisStatusChip(status = "Cek", isSmall = true)
                    }
                }

                // Daftar Riwayat Imunisasi
                Text(
                    text = "Riwayat Imunisasi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisInk
                )

                // Daftar riwayat: ikon status, nama, tanggal, chip "Sudah" atau "Cek"
                val riwayatList = listOf(
                    Triple("DPT-HB-Hib 3 & Polio 4", "7 Mei 2025", "Sudah"),
                    Triple("DPT-HB-Hib 2 & Polio 3", "7 April 2025", "Sudah"),
                    Triple("DPT-HB-Hib 1 & Polio 2", "7 Maret 2025", "Sudah"),
                    Triple("BCG & Polio 1", "14 Oktober 2024", "Sudah"),
                    Triple("Hepatitis B (HB0)", "7 September 2024", "Sudah")
                )

                for ((nama, tanggal, status) in riwayatList) {
                    DedisCard(contentPadding = 14.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (status == "Sudah") DedisTint else DedisWhite),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_check),
                                        contentDescription = null,
                                        tint = DedisPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = nama,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DedisInk
                                    )
                                    Text(
                                        text = tanggal,
                                        fontSize = 12.sp,
                                        color = DedisMuted
                                    )
                                }
                            }

                            DedisStatusChip(
                                status = if (status == "Sudah") "Normal" else "Cek",
                                isSmall = true
                            )
                        }
                    }
                }

                // Catatan kecil
                Text(
                    text = "Data contoh. Cocokkan dengan Buku KIA.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = DedisMuted
                )

                // Tombol utama
                DedisPrimaryButton(
                    text = "Lihat Jadwal Posyandu",
                    onClick = onNavigateToJadwal,
                    leadingIcon = R.drawable.ic_calendar
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrangTuaImunisasiScreenPreview() {
    DEDISTheme {
        OrangTuaImunisasiScreen()
    }
}
