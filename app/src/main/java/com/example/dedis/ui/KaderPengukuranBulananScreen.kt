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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.data.AppState
import com.example.dedis.data.model.ChildDto
import com.example.dedis.data.network.ApiClient
import com.example.dedis.data.repository.BalitaRepository
import com.example.dedis.data.repository.BalitaRepositoryImpl
import com.example.dedis.data.repository.PengukuranRepository
import com.example.dedis.data.repository.PengukuranRepositoryImpl
import com.example.dedis.ui.components.DedisAvatar
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.components.DedisChoiceChip
import com.example.dedis.ui.components.DedisHeader
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.components.DedisStatusChip
import com.example.dedis.ui.components.DedisTextField
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisDangerBg
import com.example.dedis.ui.theme.DedisDangerText
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisWhite
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun KaderPengukuranBulananScreen(
    balitaId: Long = 1L,
    modifier: Modifier = Modifier,
    balitaRepository: BalitaRepository? = null,
    pengukuranRepository: PengukuranRepository? = null,
    onNavigateBack: () -> Unit = {},
    onNavigateToHasil: () -> Unit = {},
    onNavigateToRoute: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val bRepo = remember(balitaRepository) {
        balitaRepository ?: BalitaRepositoryImpl(ApiClient.getBalitaApiService(context))
    }
    val pRepo = remember(pengukuranRepository) {
        pengukuranRepository ?: PengukuranRepositoryImpl(ApiClient.getBalitaApiService(context))
    }

    // Data balita terpilih
    var childData by remember { mutableStateOf<ChildDto?>(null) }
    var isChildLoading by remember { mutableStateOf(true) }

    // Isian formulir
    val todayDate = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    var tanggalUkur by remember { mutableStateOf(todayDate) }
    var beratBadan by remember { mutableStateOf("") }
    var tinggiBadan by remember { mutableStateOf("") }
    var lingkarKepala by remember { mutableStateOf("") }
    var metodeUkur by remember { mutableStateOf("Berdiri") }

    // Error validasi per kolom
    var beratError by remember { mutableStateOf<String?>(null) }
    var tinggiError by remember { mutableStateOf<String?>(null) }
    var tanggalError by remember { mutableStateOf<String?>(null) }

    // State simpan ke backend
    var isSaving by remember { mutableStateOf(false) }
    var saveErrorMessage by remember { mutableStateOf<String?>(null) }

    // Muat data balita untuk info di bagian atas
    LaunchedEffect(balitaId) {
        isChildLoading = true
        bRepo.getBalitaDetail(balitaId).onSuccess { child ->
            childData = child
            isChildLoading = false
            // Atur metode ukur default sesuai standar WHO
            metodeUkur = if (child.ageInMonths >= 24) "Berdiri" else "Telentang"
        }.onFailure {
            isChildLoading = false
        }
    }

    fun validate(): Boolean {
        var isValid = true

        // 1. Validasi Tanggal
        if (tanggalUkur.isBlank()) {
            tanggalError = "Tanggal pengukuran wajib diisi"
            isValid = false
        } else {
            tanggalError = null
        }

        // 2. Validasi Berat Badan (1 - 30 kg)
        val weightVal = beratBadan.replace(',', '.').toFloatOrNull()
        if (beratBadan.isBlank()) {
            beratError = "Berat badan wajib diisi"
            isValid = false
        } else if (weightVal == null) {
            beratError = "Format berat badan tidak valid"
            isValid = false
        } else if (weightVal < 1f || weightVal > 30f) {
            beratError = "Berat badan harus antara 1 sampai 30 kg"
            isValid = false
        } else {
            beratError = null
        }

        // 3. Validasi Tinggi Badan (40 - 130 cm)
        val heightVal = tinggiBadan.replace(',', '.').toFloatOrNull()
        if (tinggiBadan.isBlank()) {
            tinggiError = "Tinggi badan wajib diisi"
            isValid = false
        } else if (heightVal == null) {
            tinggiError = "Format tinggi badan tidak valid"
            isValid = false
        } else if (heightVal < 40f || heightVal > 130f) {
            tinggiError = "Tinggi badan harus antara 40 sampai 130 cm"
            isValid = false
        } else {
            tinggiError = null
        }

        return isValid
    }

    fun simpanPengukuran() {
        if (!validate()) return

        val w = beratBadan.replace(',', '.').toFloat()
        val h = tinggiBadan.replace(',', '.').toFloat()
        val lk = lingkarKepala.replace(',', '.').toFloatOrNull()
        val methodCode = if (metodeUkur == "Berdiri") "standing" else "recumbent"

        coroutineScope.launch {
            isSaving = true
            saveErrorMessage = null

            val result = pRepo.saveMeasurement(
                childId = balitaId,
                weightKg = w,
                heightCm = h,
                measurementMethod = methodCode,
                measurementDate = tanggalUkur,
                headCircumferenceCm = lk
            )

            result.onSuccess { measurement ->
                isSaving = false
                // Simpan hasil ke state bersama agar layar Hasil Pertumbuhan dapat menampilkannya
                AppState.latestMeasurementResult = measurement
                AppState.latestMeasurementChild = childData
                AppState.shouldRefreshBalitaList = true
                AppState.shouldRefreshBalitaDetail = true
                onNavigateToHasil()
            }.onFailure { error ->
                isSaving = false
                saveErrorMessage = error.localizedMessage ?: "Gagal menyimpan data pengukuran. Silakan coba lagi."
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DedisWhite,
        bottomBar = {
            KaderBottomBar(
                currentRoute = DedisDestinations.KADER_PENGUKURAN,
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
                title = "Catat Pengukuran",
                subtitle = childData?.let { "${it.name} • ${it.ageInMonths} bulan" } ?: "Pencatatan antropometri balita",
                showBackButton = true,
                onBackClick = onNavigateBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Kartu info balita terpilih
                DedisCard {
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
                                initial = childData?.initial ?: "A",
                                size = 44.dp
                            )
                            Column {
                                Text(
                                    text = childData?.name ?: "Memuat nama...",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DedisInk
                                )
                                Text(
                                    text = childData?.let { "${it.ageInMonths} bulan • Ibu ${it.motherName}" } ?: "...",
                                    fontSize = 12.sp,
                                    color = DedisMuted
                                )
                            }
                        }

                        childData?.let {
                            DedisStatusChip(status = it.statusBadge, isSmall = true)
                        }
                    }
                }

                // Pesan Error Simpan jika gagal
                if (saveErrorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DedisDangerBg, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_warning_amber),
                                contentDescription = null,
                                tint = DedisDangerText,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = saveErrorMessage!!,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = DedisDangerText
                            )
                        }
                    }
                }

                // Tanggal Pengukuran
                DedisTextField(
                    value = tanggalUkur,
                    onValueChange = {
                        tanggalUkur = it
                        if (tanggalError != null) tanggalError = null
                    },
                    label = "Tanggal Pengukuran",
                    placeholder = "YYYY-MM-DD",
                    leadingIcon = R.drawable.ic_calendar,
                    isError = tanggalError != null,
                    errorMessage = tanggalError
                )

                // Isian: Berat Badan (kg)
                DedisTextField(
                    value = beratBadan,
                    onValueChange = {
                        beratBadan = it
                        if (beratError != null) beratError = null
                    },
                    label = "Berat Badan (kg)",
                    placeholder = "Contoh: 10.2",
                    leadingIcon = R.drawable.ic_sprout_leaf,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = beratError != null,
                    errorMessage = beratError
                )

                // Isian: Tinggi / Panjang Badan (cm)
                DedisTextField(
                    value = tinggiBadan,
                    onValueChange = {
                        tinggiBadan = it
                        if (tinggiError != null) tinggiError = null
                    },
                    label = "Tinggi / Panjang Badan (cm)",
                    placeholder = "Contoh: 79.2",
                    leadingIcon = R.drawable.ic_ruler_chart,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = tinggiError != null,
                    errorMessage = tinggiError
                )

                // Isian: Lingkar Kepala (cm) - Opsional
                DedisTextField(
                    value = lingkarKepala,
                    onValueChange = { lingkarKepala = it },
                    label = "Lingkar Kepala (cm) • Opsional",
                    placeholder = "Contoh: 46.2",
                    leadingIcon = R.drawable.ic_shield_check,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                // Cara Ukur (chip: Berdiri / Telentang)
                Column {
                    Text(
                        text = "Cara Pengukuran Tinggi",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = DedisInk
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DedisChoiceChip(
                            text = "Berdiri (Usia ≥ 24 bln)",
                            isSelected = metodeUkur == "Berdiri",
                            onClick = { metodeUkur = "Berdiri" },
                            modifier = Modifier.weight(1f)
                        )
                        DedisChoiceChip(
                            text = "Telentang (Usia < 24 bln)",
                            isSelected = metodeUkur == "Telentang",
                            onClick = { metodeUkur = "Telentang" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tombol utama: "Simpan & Hitung Status"
                DedisPrimaryButton(
                    text = if (isSaving) "Menyimpan & Menghitung..." else "Simpan & Hitung Status",
                    onClick = { simpanPengukuran() },
                    enabled = !isSaving,
                    leadingIcon = if (!isSaving) R.drawable.ic_check else null
                )

                if (isSaving) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = DedisPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    }
                }

                // Spacer bawah
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KaderPengukuranBulananScreenPreview() {
    DEDISTheme {
        KaderPengukuranBulananScreen()
    }
}
