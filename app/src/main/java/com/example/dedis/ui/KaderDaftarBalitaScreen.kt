package com.example.dedis.ui

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
import androidx.compose.foundation.rememberScrollState
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
import com.example.dedis.ui.components.DedisChoiceChip
import com.example.dedis.ui.components.DedisHeader
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.components.DedisSecondaryButton
import com.example.dedis.ui.components.DedisStatusChip
import com.example.dedis.ui.components.DedisTextField
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisDangerText
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisWhite
import kotlinx.coroutines.launch

@Composable
fun KaderDaftarBalitaScreen(
    modifier: Modifier = Modifier,
    balitaRepository: BalitaRepository? = null,
    onNavigateToRoute: (String) -> Unit = {},
    onNavigateToDetail: (balitaId: Long) -> Unit = {},
    onNavigateToFormulir: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember(balitaRepository) {
        balitaRepository ?: BalitaRepositoryImpl(ApiClient.getBalitaApiService(context))
    }
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }

    // State pemuatan data dari backend
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var balitaList by remember { mutableStateOf<List<ChildDto>>(emptyList()) }

    fun loadData() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.getBalitaList()
            result.onSuccess { list ->
                balitaList = list
                isLoading = false
            }.onFailure { error ->
                errorMessage = error.localizedMessage ?: "Terjadi kesalahan saat memuat data"
                isLoading = false
            }
        }
    }

    LaunchedEffect(AppState.shouldRefreshBalitaList) {
        if (AppState.shouldRefreshBalitaList) {
            AppState.shouldRefreshBalitaList = false
            loadData()
        } else if (balitaList.isEmpty()) {
            loadData()
        }
    }

    val filteredList = balitaList.filter { balita ->
        when (selectedFilter) {
            "Belum" -> balita.statusBadge == "Belum diukur"
            "Perhatian" -> balita.statusBadge == "Perlu perhatian" || balita.statusBadge == "Risiko tinggi"
            else -> true
        }
    }.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
            it.motherName.contains(searchQuery, ignoreCase = true) ||
            it.subtitleLabel.contains(searchQuery, ignoreCase = true)
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
                title = "Balita",
                subtitle = "${balitaList.size} sasaran terdaftar di Posyandu"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Kolom cari
                DedisTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Cari nama balita atau ibu...",
                    leadingIcon = R.drawable.ic_search
                )

                // Chip penyaring
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val countBelum = balitaList.count { it.statusBadge == "Belum diukur" }
                    val countPerhatian = balitaList.count { it.statusBadge == "Perlu perhatian" || it.statusBadge == "Risiko tinggi" }

                    DedisChoiceChip(
                        text = "Semua ${balitaList.size}",
                        isSelected = selectedFilter == "Semua",
                        onClick = { selectedFilter = "Semua" }
                    )
                    DedisChoiceChip(
                        text = "Belum $countBelum",
                        isSelected = selectedFilter == "Belum",
                        onClick = { selectedFilter = "Belum" }
                    )
                    DedisChoiceChip(
                        text = "Perhatian $countPerhatian",
                        isSelected = selectedFilter == "Perhatian",
                        onClick = { selectedFilter = "Perhatian" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Kondisi 1: Sedang Memuat (Loading)
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(color = DedisPrimary)
                        Text(
                            text = "Memuat data balita dari server...",
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
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Gagal memuat data",
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
                            onClick = { loadData() }
                        )
                    }
                }
            }
            // Kondisi 3: Data Kosong
            else if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotBlank() || selectedFilter != "Semua") {
                            "Tidak ada balita yang cocok dengan filter."
                        } else {
                            "Belum ada data sasaran balita terdaftar."
                        },
                        fontSize = 14.sp,
                        color = DedisMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
            // Kondisi Sukses: Tampilkan Daftar Balita dari Backend
            else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (balita in filteredList) {
                        DedisCard(
                            contentPadding = 14.dp,
                            onClick = { onNavigateToDetail(balita.id) }
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
                                    DedisAvatar(initial = balita.initial, size = 44.dp)
                                    Column {
                                        Text(
                                            text = balita.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DedisInk
                                        )
                                        Text(
                                            text = balita.subtitleLabel,
                                            fontSize = 12.sp,
                                            color = DedisMuted
                                        )
                                    }
                                }

                                DedisStatusChip(status = balita.statusBadge, isSmall = true)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Tombol Utama: Tambah Balita
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                DedisPrimaryButton(
                    text = "Tambah Balita",
                    onClick = onNavigateToFormulir,
                    leadingIcon = R.drawable.ic_plus
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KaderDaftarBalitaScreenPreview() {
    DEDISTheme {
        KaderDaftarBalitaScreen()
    }
}
