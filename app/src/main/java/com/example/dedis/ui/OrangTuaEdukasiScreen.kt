package com.example.dedis.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import com.example.dedis.ui.components.DedisTextField
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisSoft
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWhite

@Composable
fun OrangTuaEdukasiScreen(
    modifier: Modifier = Modifier,
    onNavigateToRoute: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTopic by remember { mutableStateOf("Semua") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DedisWhite,
        bottomBar = {
            OrangTuaBottomBar(
                currentRoute = DedisDestinations.EDUKASI,
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
                title = "Edukasi Gizi",
                subtitle = "Panduan praktis pencegahan stunting dan MPASI"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Kolom cari
                DedisTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Cari topik atau artikel...",
                    leadingIcon = R.drawable.ic_search
                )

                // Chip topik: Semua, Stunting, MPASI, Imunisasi
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val topics = listOf("Semua", "Stunting", "MPASI", "Imunisasi")
                    for (topic in topics) {
                        DedisChoiceChip(
                            text = topic,
                            isSelected = selectedTopic == topic,
                            onClick = { selectedTopic = topic }
                        )
                    }
                }

                // Kartu bacaan utama "Kenali stunting" (4 menit baca)
                DedisCard(
                    backgroundColor = DedisPrimary,
                    borderColor = DedisPrimary,
                    contentPadding = 20.dp
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(DedisSoft)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Topik Utama",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DedisPrimary
                                )
                            }
                            Text(
                                text = "4 menit baca",
                                fontSize = 12.sp,
                                color = DedisSoft
                            )
                        }

                        Text(
                            text = "Kenali Stunting: Tanda, Penyebab, dan Pencegahannya",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DedisWhite,
                            lineHeight = 26.sp
                        )

                        Text(
                            text = "Pahami cara deteksi dini ukuran tinggi badan dan asupan protein hewani sejak usia dini.",
                            fontSize = 13.sp,
                            color = DedisSoft,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        DedisPrimaryButton(
                            text = "Baca Sekarang",
                            onClick = { /* Buka artikel */ },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Daftar 4 bacaan: ikon topik, judul, durasi, panah
                Text(
                    text = "Daftar Bacaan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisInk
                )

                val articleList = listOf(
                    Triple("Isi Piringku: Menu MPASI Kaya Protein Hewani", "3 menit baca", R.drawable.ic_sprout_leaf),
                    Triple("Jadwal Imunisasi Dasar Lengkap Usia 0–24 Bulan", "5 menit baca", R.drawable.ic_vaccine),
                    Triple("Cara Membaca Grafik Pertumbuhan di Buku KIA", "4 menit baca", R.drawable.ic_ruler_chart),
                    Triple("Sanitasi dan Air Bersih untuk Tumbuh Kembang Anak", "3 menit baca", R.drawable.ic_shield_check)
                )

                for ((judul, durasi, iconRes) in articleList) {
                    DedisCard(contentPadding = 14.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
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
                                        text = judul,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DedisInk,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = durasi,
                                        fontSize = 12.sp,
                                        color = DedisMuted
                                    )
                                }
                            }

                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_right),
                                contentDescription = "Baca",
                                tint = DedisMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Spacer bawah agar konten paling bawah bisa discroll leluasa di atas bottom bar
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrangTuaEdukasiScreenPreview() {
    DEDISTheme {
        OrangTuaEdukasiScreen()
    }
}
