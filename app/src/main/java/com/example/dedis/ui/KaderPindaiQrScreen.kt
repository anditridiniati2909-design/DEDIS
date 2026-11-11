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
import androidx.compose.material3.Surface
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
import com.example.dedis.ui.components.DedisHeader
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.components.DedisSecondaryButton
import com.example.dedis.ui.components.DedisStatusChip
import com.example.dedis.ui.components.DedisTextField
import com.example.dedis.ui.components.QrCameraScanner
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisSoft
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWhite

@Composable
fun KaderPindaiQrScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavigateToCatatUkur: () -> Unit = {},
    onNavigateToDetail: () -> Unit = {}
) {
    var manualCode by remember { mutableStateOf("") }
    var hasResult by remember { mutableStateOf(false) }
    var isScanningActive by remember { mutableStateOf(true) }

    // Data anak yang berhasil dipindai
    var scannedBalitaName by remember { mutableStateOf("Aisyah Putri") }
    var scannedBalitaSub by remember { mutableStateOf("24 bulan • Ibu Nur Aini") }
    var scannedBalitaId by remember { mutableStateOf("DEDIS-2024-0012") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DedisWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Layar
            DedisHeader(
                spaceRole = "RUANG KADER",
                title = "Pindai QR Balita",
                subtitle = "Arahkan kamera ke QR anak atau cari kode manual",
                showBackButton = true,
                onBackClick = onNavigateBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Kamera Scan QR Nyata dengan CameraX & Google ML Kit
                QrCameraScanner(
                    isScanningActive = isScanningActive,
                    onBarcodeScanned = { barcodeValue ->
                        // Saat QR terbaca oleh kamera
                        manualCode = barcodeValue
                        scannedBalitaId = barcodeValue
                        isScanningActive = false
                        hasResult = true
                    }
                )

                // Tombol reset scan jika kamera sedang dijeda setelah membaca QR
                if (!isScanningActive) {
                    DedisSecondaryButton(
                        text = "Pindai QR Lain",
                        leadingIcon = R.drawable.ic_camera,
                        onClick = {
                            isScanningActive = true
                            hasResult = false
                            manualCode = ""
                        }
                    )
                }

                // Kolom kode manual + tombol "Cari"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DedisTextField(
                        value = manualCode,
                        onValueChange = { manualCode = it },
                        placeholder = "Masukkan kode / ID balita",
                        leadingIcon = R.drawable.ic_search,
                        modifier = Modifier.weight(1f)
                    )

                    DedisPrimaryButton(
                        text = "Cari",
                        onClick = {
                            if (manualCode.isNotBlank()) {
                                scannedBalitaId = manualCode
                                hasResult = true
                                isScanningActive = false
                            }
                        },
                        fillMaxWidth = false,
                        modifier = Modifier.width(90.dp)
                    )
                }

                // Kartu hasil scan / pencarian balita
                if (hasResult) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hasil Pemindaian",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DedisInk
                        )

                        Surface(
                            color = DedisTint,
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "QR Terverifikasi",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DedisPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    DedisCard {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                    DedisAvatar(initial = scannedBalitaName.take(1), size = 46.dp)
                                    Column {
                                        Text(
                                            text = scannedBalitaName,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DedisInk
                                        )
                                        Text(
                                            text = scannedBalitaSub,
                                            fontSize = 13.sp,
                                            color = DedisMuted
                                        )
                                        Text(
                                            text = "ID: $scannedBalitaId",
                                            fontSize = 11.sp,
                                            color = DedisPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                DedisStatusChip(status = "Perlu perhatian", isSmall = true)
                            }

                            // Dua tombol aksi: "Detail" & "Catat Ukur"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DedisSecondaryButton(
                                    text = "Detail",
                                    onClick = onNavigateToDetail,
                                    modifier = Modifier.weight(1f)
                                )

                                DedisPrimaryButton(
                                    text = "Catat Ukur",
                                    onClick = onNavigateToCatatUkur,
                                    leadingIcon = R.drawable.ic_ruler_chart,
                                    modifier = Modifier.weight(1.3f)
                                )
                            }
                        }
                    }
                }

                // Info Bantuan Scanner di bagian bawah
                Surface(
                    color = DedisTint,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(DedisSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_qr_code),
                                contentDescription = null,
                                tint = DedisPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Format QR yang Didukung",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DedisInk
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Dapat memindai QR kartu anak di aplikasi orang tua, kartu posyandu, atau barcode Buku KIA.",
                                fontSize = 11.sp,
                                color = DedisMuted,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KaderPindaiQrScreenPreview() {
    DEDISTheme {
        KaderPindaiQrScreen()
    }
}
