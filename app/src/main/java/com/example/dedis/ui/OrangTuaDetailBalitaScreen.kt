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
import com.example.dedis.ui.components.DedisAvatar
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.components.DedisChoiceChip
import com.example.dedis.ui.components.DedisHeader
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.components.DedisStatusChip
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisLine
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWarningBg
import com.example.dedis.ui.theme.DedisWarningText
import com.example.dedis.ui.theme.DedisWhite

@Composable
fun OrangTuaDetailBalitaScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavigateToRoute: (String) -> Unit = {}
) {
    var selectedMetric by remember { mutableStateOf("Tinggi") }

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
                title = "Pertumbuhan Anak",
                subtitle = "Aisyah Putri • 24 bulan • Perempuan",
                showBackButton = true,
                onBackClick = onNavigateBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tab Pilihan Metrik
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DedisChoiceChip(
                        text = "Tinggi badan (cm)",
                        isSelected = selectedMetric == "Tinggi",
                        onClick = { selectedMetric = "Tinggi" },
                        modifier = Modifier.weight(1f)
                    )
                    DedisChoiceChip(
                        text = "Berat badan (kg)",
                        isSelected = selectedMetric == "Berat",
                        onClick = { selectedMetric = "Berat" },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Kartu Grafik dan Angka Terakhir
                DedisCard {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (selectedMetric == "Tinggi") "Tinggi Saat Ini" else "Berat Saat Ini",
                                    fontSize = 12.sp,
                                    color = DedisMuted
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = if (selectedMetric == "Tinggi") "79,2" else "10,2",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DedisInk
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (selectedMetric == "Tinggi") "cm" else "kg",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DedisMuted,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }

                            DedisStatusChip(status = "Perlu perhatian")
                        }

                        // Visualisasi Tren Grafik Sederhana
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DedisTint)
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Standar WHO: 85,7 cm", fontSize = 11.sp, color = DedisMuted)
                                    Text("Z-Score: -2,1 SD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DedisWarningText)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Titik 22 bln
                                    ChartPoint("78,0 cm", "22 bln", false)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(2.dp)
                                            .background(DedisPrimary)
                                    )
                                    // Titik 23 bln
                                    ChartPoint("78,6 cm", "23 bln", false)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(2.dp)
                                            .background(DedisPrimary)
                                    )
                                    // Titik 24 bln
                                    ChartPoint("79,2 cm", "24 bln", true)
                                }
                            }
                        }

                        Text(
                            text = "Tinggi bertambah 0,6 cm dari bulan lalu. Perlu pemantauan lanjutan bersama bidan.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = DedisInk
                        )
                    }
                }

                // ==========================================
                // GRAFIK BATANG — disisipkan di sini (sesuai permintaan)
                // ==========================================
                val sampleMeasurements = if (selectedMetric == "Tinggi") {
                    listOf(
                        MeasurementPoint("22 bln", 78.0f, -2.5f, "7 Jul 2026", "Perlu perhatian"),
                        MeasurementPoint("23 bln", 78.6f, -2.3f, "7 Agt 2026", "Perlu perhatian"),
                        MeasurementPoint("24 bln", 79.2f, -2.1f, "7 Sep 2026", "Perlu perhatian")
                    )
                } else {
                    listOf(
                        MeasurementPoint("22 bln", 9.4f, -0.8f, "7 Jul 2026", "Normal"),
                        MeasurementPoint("23 bln", 9.8f, -0.6f, "7 Agt 2026", "Normal"),
                        MeasurementPoint("24 bln", 10.2f, -0.4f, "7 Sep 2026", "Normal")
                    )
                }

                GrowthGraphCard(
                    measurements = sampleMeasurements,
                    whoReference = if (selectedMetric == "Tinggi") 85.7f else 11.5f,
                    whoLabel = if (selectedMetric == "Tinggi") "WHO 85,7 cm" else "WHO 11,5 kg",
                    isHeightMetric = selectedMetric == "Tinggi"
                )

                // Kartu Peringatan Singkat
                DedisCard(
                    backgroundColor = DedisWarningBg,
                    borderColor = DedisWarningBg,
                    contentPadding = 14.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_warning_amber),
                            contentDescription = null,
                            tint = DedisWarningText,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Jadwal ukur ulang dan konsultasi gizi bersama Bidan Rina: 10 September 2026.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = DedisWarningText
                        )
                    }
                }

                // Riwayat Pengukuran
                Text(
                    text = "Riwayat Pengukuran",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisInk
                )

                val historyList = listOf(
                    Triple("7 September 2026 (24 bln)", "10,2 kg • 79,2 cm", "Berdiri"),
                    Triple("7 Agustus 2026 (23 bln)", "9,8 kg • 78,6 cm", "Telentang"),
                    Triple("7 Juli 2026 (22 bln)", "9,4 kg • 78,0 cm", "Telentang")
                )

                for ((tanggal, hasil, cara) in historyList) {
                    DedisCard(contentPadding = 14.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = tanggal,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DedisInk
                                )
                                Text(
                                    text = "Metode: $cara",
                                    fontSize = 12.sp,
                                    color = DedisMuted
                                )
                            }
                            Text(
                                text = hasil,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DedisPrimary
                            )
                        }
                    }
                }

                // Satu Tombol Utama
                DedisPrimaryButton(
                    text = "Tanya Kader Posyandu",
                    onClick = { onNavigateToRoute(DedisDestinations.PESAN) },
                    leadingIcon = R.drawable.ic_chat
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ChartPoint(value: String, label: String, isHighlight: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 10.sp, fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal, color = if (isHighlight) DedisWarningText else DedisMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .size(if (isHighlight) 10.dp else 6.dp)
                .clip(CircleShape)
                .background(if (isHighlight) DedisWarningText else DedisPrimary)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 10.sp, color = DedisMuted)
    }
}

@Preview(showBackground = true)
@Composable
fun OrangTuaDetailBalitaScreenPreview() {
    DEDISTheme {
        OrangTuaDetailBalitaScreen()
    }
}
