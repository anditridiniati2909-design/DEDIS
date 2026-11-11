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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.components.DedisHeader
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.components.DedisTextField
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisWarningBg
import com.example.dedis.ui.theme.DedisWarningText
import com.example.dedis.ui.theme.DedisWhite

@Composable
fun KaderTindakLanjutScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavigateToRoute: (String) -> Unit = {}
) {
    var selectedPendampingan by remember { mutableStateOf("Konseling gizi") }
    var tanggalKontrol by remember { mutableStateOf("10 September 2026") }
    var penanggungJawab by remember { mutableStateOf("Bidan Rina & Kader Sari") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DedisWhite,
        bottomBar = {
            KaderBottomBar(
                currentRoute = DedisDestinations.KADER_DAFTAR_BALITA,
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
                spaceRole = "RUANG KADER",
                title = "Tindak Lanjut",
                subtitle = "Aisyah Putri • 24 bulan",
                showBackButton = true,
                onBackClick = onNavigateBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Kartu peringatan "Langkah pertama: Ukur ulang BB dan TB bersama bidan"
                DedisCard(
                    backgroundColor = DedisWarningBg,
                    borderColor = DedisWarningBg,
                    contentPadding = 16.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(DedisWhite, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_warning_amber),
                                contentDescription = null,
                                tint = DedisWarningText,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Langkah pertama:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DedisWarningText
                            )
                            Text(
                                text = "Ukur ulang BB dan TB bersama bidan",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DedisInk
                            )
                        }
                    }
                }

                // Pilihan pendampingan (radio): Konseling gizi, Konsultasi bidan, Rujukan ke fasilitas kesehatan
                Text(
                    text = "Pilihan Pendampingan",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisInk
                )

                val opsiPendampingan = listOf(
                    Pair("Konseling gizi", "Edukasi menu MPASI dan pola asuh keluarga"),
                    Pair("Konsultasi bidan", "Pemeriksaan medis awal tumbuh kembang"),
                    Pair("Rujukan ke fasilitas kesehatan", "Penanganan lanjutan ke Puskesmas / RSUD")
                )

                for ((opsi, keterangan) in opsiPendampingan) {
                    DedisCard(
                        contentPadding = 12.dp,
                        onClick = { selectedPendampingan = opsi }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedPendampingan == opsi,
                                onClick = { selectedPendampingan = opsi },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = DedisPrimary,
                                    unselectedColor = DedisMuted
                                )
                            )
                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(
                                    text = opsi,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DedisInk
                                )
                                Text(
                                    text = keterangan,
                                    fontSize = 12.sp,
                                    color = DedisMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Isian tanggal kontrol dan penanggung jawab
                DedisTextField(
                    value = tanggalKontrol,
                    onValueChange = { tanggalKontrol = it },
                    label = "Tanggal Kontrol / Kunjungan",
                    placeholder = "Pilih tanggal kontrol",
                    leadingIcon = R.drawable.ic_calendar
                )

                DedisTextField(
                    value = penanggungJawab,
                    onValueChange = { penanggungJawab = it },
                    label = "Penanggung Jawab",
                    placeholder = "Nama bidan / kader pendamping",
                    leadingIcon = R.drawable.ic_person
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tombol "Simpan tindak lanjut"
                DedisPrimaryButton(
                    text = "Simpan Tindak Lanjut",
                    onClick = onNavigateBack,
                    leadingIcon = R.drawable.ic_check
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KaderTindakLanjutScreenPreview() {
    DEDISTheme {
        KaderTindakLanjutScreen()
    }
}
