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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.data.AppState
import com.example.dedis.data.model.ChildDto
import com.example.dedis.data.network.ApiClient
import com.example.dedis.data.repository.BalitaRepository
import com.example.dedis.data.repository.BalitaRepositoryImpl
import com.example.dedis.ui.components.DedisAvatar
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.components.DedisHeader
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.components.DedisSecondaryButton
import com.example.dedis.ui.components.DedisStatusChip
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisDangerText
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWhite
import kotlinx.coroutines.launch

@Composable
fun KaderDetailBalitaScreen(
    balitaId: Long = 1L,
    modifier: Modifier = Modifier,
    balitaRepository: BalitaRepository? = null,
    onNavigateBack: () -> Unit = {},
    onNavigateToRoute: (String) -> Unit = {},
    onNavigateToCatatPengukuran: (Long) -> Unit = {},
    onNavigateToHasilPertumbuhan: () -> Unit = {},
    onNavigateToTindakLanjut: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember(balitaRepository) {
        balitaRepository ?: BalitaRepositoryImpl(ApiClient.getBalitaApiService(context))
    }
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var childData by remember { mutableStateOf<ChildDto?>(null) }

    fun loadDetail() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.getBalitaDetail(balitaId)
            result.onSuccess { data ->
                childData = data
                isLoading = false
            }.onFailure { error ->
                errorMessage = error.localizedMessage ?: "Gagal memuat detail balita"
                isLoading = false
            }
        }
    }

    LaunchedEffect(balitaId, AppState.shouldRefreshBalitaDetail) {
        if (AppState.shouldRefreshBalitaDetail) {
            AppState.shouldRefreshBalitaDetail = false
            loadDetail()
        } else if (childData == null) {
            loadDetail()
        }
    }

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
        ) {
            // Header
            DedisHeader(
                spaceRole = "RUANG KADER",
                title = childData?.name ?: "Detail Balita",
                subtitle = childData?.let { "${it.ageInMonths} bulan • ${it.genderLabel}" } ?: "Memuat informasi balita...",
                showBackButton = true,
                onBackClick = onNavigateBack
            )

            // Kondisi 1: Sedang Memuat (Loading)
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(color = DedisPrimary)
                        Text(
                            text = "Memuat data balita...",
                            fontSize = 13.sp,
                            color = DedisMuted
                        )
                    }
                }
            }
            // Kondisi 2: Gagal Memuat (Error + Tombol Coba Lagi)
            else if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Gagal memuat detail balita",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DedisInk
                        )
                        Text(
                            text = errorMessage ?: "Terjadi kesalahan jaringan.",
                            fontSize = 13.sp,
                            color = DedisDangerText,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        DedisSecondaryButton(
                            text = "Coba Lagi",
                            onClick = { loadDetail() }
                        )
                    }
                }
            }
            // Kondisi 3: Data Kosong / Tidak Ditemukan
            else if (childData == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Data balita dengan ID #$balitaId tidak ditemukan.",
                        fontSize = 14.sp,
                        color = DedisMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
            // Kondisi Sukses: Tampilkan Detail Balita
            else {
                val child = childData!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Baris Chip Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status Pertumbuhan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DedisInk
                        )
                        DedisStatusChip(status = child.statusBadge)
                    }

                    // Kartu Pengukuran Terakhir
                    DedisCard {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            val latest = child.latestMeasurement
                            val tglUkur = latest?.measurementDate ?: "Belum pernah diukur"

                            Text(
                                text = "Pengukuran Terakhir ($tglUkur)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = DedisMuted
                            )

                            if (latest != null) {
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
                                                text = "%.1f".format(latest.weightKg).replace('.', ','),
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
                                                text = "%.1f".format(latest.heightCm).replace('.', ','),
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
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(DedisTint, RoundedCornerShape(14.dp))
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Belum ada catatan pengukuran.",
                                        fontSize = 13.sp,
                                        color = DedisMuted
                                    )
                                }
                            }

                            // Tombol "Lihat hasil pertumbuhan"
                            DedisSecondaryButton(
                                text = "Lihat hasil pertumbuhan",
                                onClick = onNavigateToHasilPertumbuhan,
                                leadingIcon = R.drawable.ic_ruler_chart
                            )
                        }
                    }

                    // Kartu Orang Tua
                    DedisCard {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Data Orang Tua",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = DedisInk
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                DedisAvatar(initial = child.motherName.take(1).ifBlank { "I" }, size = 42.dp)
                                Column {
                                    Text(
                                        text = "Ibu ${child.motherName}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DedisInk
                                    )
                                    Text(
                                        text = "Dusun ${child.hamlet ?: child.dusun ?: "Borongloe"}",
                                        fontSize = 12.sp,
                                        color = DedisMuted
                                    )
                                }
                            }
                        }
                    }

                    // Riwayat Pengukuran
                    Text(
                        text = "Riwayat Pengukuran",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DedisInk
                    )

                    val measurements = child.measurements ?: emptyList()
                    if (measurements.isEmpty()) {
                        DedisCard(contentPadding = 14.dp) {
                            Text(
                                text = "Belum ada riwayat pengukuran sebelumnya.",
                                fontSize = 13.sp,
                                color = DedisMuted
                            )
                        }
                    } else {
                        for (item in measurements) {
                            DedisCard(contentPadding = 14.dp) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = item.measurementDate ?: "Tanggal tidak tersedia",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DedisInk
                                        )
                                        val lkText = item.headCircumferenceCm?.let { "LK $it cm • " } ?: ""
                                        val methodText = if (item.measurementMethod == "standing") "Berdiri" else "Telentang"
                                        Text(
                                            text = "$lkText$methodText",
                                            fontSize = 12.sp,
                                            color = DedisMuted
                                        )
                                    }
                                    Text(
                                        text = "%.1f kg • %.1f cm".format(item.weightKg, item.heightCm).replace('.', ','),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DedisPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Tombol Utama: Catat Pengukuran (membawa balitaId)
                    DedisPrimaryButton(
                        text = "Catat Pengukuran",
                        onClick = { onNavigateToCatatPengukuran(balitaId) },
                        leadingIcon = R.drawable.ic_ruler_chart
                    )

                    // Tombol Sekunder: Buat Tindak Lanjut
                    DedisSecondaryButton(
                        text = "Buat Tindak Lanjut",
                        onClick = onNavigateToTindakLanjut,
                        leadingIcon = R.drawable.ic_clipboard_list
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KaderDetailBalitaScreenPreview() {
    DEDISTheme {
        KaderDetailBalitaScreen()
    }
}
