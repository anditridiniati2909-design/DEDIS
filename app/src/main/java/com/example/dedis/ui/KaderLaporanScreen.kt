package com.example.dedis.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.components.DedisHeader
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.components.DedisStatusChip
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisWhite

@Composable
fun KaderLaporanScreen(
    modifier: Modifier = Modifier,
    onNavigateToRoute: (String) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DedisWhite,
        bottomBar = {
            KaderBottomBar(
                currentRoute = DedisDestinations.KADER_LAPORAN,
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
                title = "Laporan Bulanan",
                subtitle = "Rekapitulasi Posyandu Anggrek 4 • September 2026"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Kartu Capaian
                DedisCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Cakupan Pengukuran (D/S)",
                            fontSize = 13.sp,
                            color = DedisMuted
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "80%",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = DedisPrimary
                            )
                            Spacer(modifier = Modifier.padding(start = 6.dp))
                            Text(
                                text = "92 dari 115 sasaran ditimbang",
                                fontSize = 13.sp,
                                color = DedisMuted,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }

                // Rincian Hasil
                Text(
                    text = "Distribusi Status Gizi",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisInk
                )

                DedisCard(contentPadding = 14.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Balita Normal", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DedisInk)
                        DedisStatusChip(status = "Normal (80)", isSmall = true)
                    }
                }

                DedisCard(contentPadding = 14.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Balita Pendek (Stunting)", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DedisInk)
                        DedisStatusChip(status = "Perhatian (9)", isSmall = true)
                    }
                }

                DedisCard(contentPadding = 14.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Balita Sangat Pendek (Severely Stunted)", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DedisInk)
                        DedisStatusChip(status = "Risiko tinggi (3)", isSmall = true)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tombol Unduh / Bagikan Laporan
                DedisPrimaryButton(
                    text = "Ekspor Laporan ke Bidan",
                    onClick = { },
                    leadingIcon = R.drawable.ic_clipboard_list
                )

                // Spacer bawah agar konten paling bawah bisa discroll leluasa di atas bottom bar
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KaderLaporanScreenPreview() {
    DEDISTheme {
        KaderLaporanScreen()
    }
}

