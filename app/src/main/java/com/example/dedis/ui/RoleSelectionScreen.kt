package com.example.dedis.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.components.DedisTextField
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisDangerBg
import com.example.dedis.ui.theme.DedisDangerText
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisLine
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisSoft
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWhite
import com.example.dedis.util.SessionManager

enum class AppRole {
    ORANG_TUA,
    KADER
}

enum class AuthMode {
    MASUK,
    DAFTAR
}

@Composable
fun RoleSelectionScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onSelectOrangTua: (phone: String, name: String) -> Unit = { _, _ -> },
    onSelectKader: (idOrPhone: String, name: String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }

    // State peran terpilih & mode autentikasi
    var selectedRole by remember { mutableStateOf(AppRole.ORANG_TUA) }
    var authMode by remember { mutableStateOf(AuthMode.MASUK) }

    // Input fields state
    var noHpOrId by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var konfirmasiPin by remember { mutableStateOf("") }
    var namaLengkap by remember { mutableStateOf("") }
    var namaAnak by remember { mutableStateOf("") }
    var kodeUndanganKader by remember { mutableStateOf("") }

    // Kontrol visibilitas PIN
    var isPinVisible by remember { mutableStateOf(false) }

    // Pesan error validasi (termasuk notifikasi jika sesi kedaluwarsa 401)
    var errorMessage by remember {
        mutableStateOf(
            if (sessionManager.consumeSessionExpiredNotice()) "Sesi berakhir, silakan masuk lagi." else null
        )
    }

    // Fungsi validasi sebelum navigasi
    fun handleAuthSubmit() {
        errorMessage = null

        // 1. Validasi untuk Orang Tua - Mode Masuk
        if (selectedRole == AppRole.ORANG_TUA && authMode == AuthMode.MASUK) {
            val cleanPhone = noHpOrId.trim()
            if (cleanPhone.isEmpty()) {
                errorMessage = "Nomor WhatsApp / HP wajib diisi"
                return
            }
            if (cleanPhone.length < 10 || !cleanPhone.all { it.isDigit() }) {
                errorMessage = "Nomor HP harus berupa angka minimal 10 digit"
                return
            }
            if (pin.length != 6 || !pin.all { it.isDigit() }) {
                errorMessage = "PIN keamanan harus berupa 6 angka"
                return
            }
            // Validasi berhasil -> Masuk langsung ke Beranda Orang Tua (1 langkah)
            onSelectOrangTua(cleanPhone, "Nur Aini")
        }
        // 2. Validasi untuk Orang Tua - Mode Daftar
        else if (selectedRole == AppRole.ORANG_TUA && authMode == AuthMode.DAFTAR) {
            if (namaLengkap.trim().isEmpty()) {
                errorMessage = "Nama lengkap orang tua wajib diisi"
                return
            }
            val cleanPhone = noHpOrId.trim()
            if (cleanPhone.length < 10 || !cleanPhone.all { it.isDigit() }) {
                errorMessage = "Nomor HP harus berupa angka minimal 10 digit"
                return
            }
            if (namaAnak.trim().isEmpty()) {
                errorMessage = "Nama balita wajib diisi"
                return
            }
            if (pin.length != 6 || !pin.all { it.isDigit() }) {
                errorMessage = "Buat PIN baru berupa 6 digit angka"
                return
            }
            if (pin != konfirmasiPin) {
                errorMessage = "Konfirmasi PIN tidak cocok"
                return
            }
            // Registrasi berhasil -> Masuk langsung ke Beranda Orang Tua
            onSelectOrangTua(cleanPhone, namaLengkap.trim())
        }
        // 3. Validasi untuk Kader - Mode Masuk
        else if (selectedRole == AppRole.KADER && authMode == AuthMode.MASUK) {
            val cleanId = noHpOrId.trim()
            if (cleanId.isEmpty()) {
                errorMessage = "ID Kader atau Nomor WhatsApp wajib diisi"
                return
            }
            if (pin.length != 6 || !pin.all { it.isDigit() }) {
                errorMessage = "PIN Kader harus berupa 6 digit angka"
                return
            }
            // Validasi masuk kader -> Masuk langsung ke Beranda Kader
            onSelectKader(cleanId, "Sari")
        }
        // 4. Validasi untuk Kader - Mode Aktivasi Undangan
        else if (selectedRole == AppRole.KADER && authMode == AuthMode.DAFTAR) {
            if (kodeUndanganKader.trim().length < 6) {
                errorMessage = "Kode undangan resmi kader tidak valid (min 6 karakter)"
                return
            }
            if (namaLengkap.trim().isEmpty()) {
                errorMessage = "Nama kader wajib diisi"
                return
            }
            if (pin.length != 6 || !pin.all { it.isDigit() }) {
                errorMessage = "Buat PIN Kader berupa 6 digit angka"
                return
            }
            // Aktivasi kader berhasil -> Masuk langsung ke Beranda Kader
            onSelectKader(noHpOrId.trim(), namaLengkap.trim())
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DedisWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Layar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Kembali",
                        tint = DedisPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = "POSYANDU ANGGREK 4",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisMuted,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.size(36.dp))
            }

            // Judul dan Keterangan
            Column {
                Text(
                    text = "Masuk ke DEDIS",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisInk
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pilih peran Anda untuk mengakses layanan pemantauan gizi",
                    fontSize = 13.sp,
                    color = DedisMuted
                )
            }

            // ==========================================
            // KARTU PILIHAN PERAN (TIDAK KOSONG & INTERAKTIF)
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Opsi Orang Tua
                val isParentSelected = selectedRole == AppRole.ORANG_TUA
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedRole = AppRole.ORANG_TUA
                            errorMessage = null
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isParentSelected) DedisTint else DedisWhite,
                    border = BorderStroke(
                        width = if (isParentSelected) 2.dp else 1.dp,
                        color = if (isParentSelected) DedisPrimary else DedisLine
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isParentSelected) DedisPrimary else DedisTint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = null,
                                tint = if (isParentSelected) DedisWhite else DedisPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Orang Tua",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isParentSelected) DedisPrimary else DedisInk
                        )
                        Text(
                            text = "Pantau Balita",
                            fontSize = 11.sp,
                            color = DedisMuted
                        )
                    }
                }

                // Opsi Kader
                val isKaderSelected = selectedRole == AppRole.KADER
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedRole = AppRole.KADER
                            errorMessage = null
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isKaderSelected) DedisTint else DedisWhite,
                    border = BorderStroke(
                        width = if (isKaderSelected) 2.dp else 1.dp,
                        color = if (isKaderSelected) DedisPrimary else DedisLine
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isKaderSelected) DedisPrimary else DedisTint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_shield_check),
                                contentDescription = null,
                                tint = if (isKaderSelected) DedisWhite else DedisPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Kader Posyandu",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isKaderSelected) DedisPrimary else DedisInk
                        )
                        Text(
                            text = "Layanan & Ukur",
                            fontSize = 11.sp,
                            color = DedisMuted
                        )
                    }
                }
            }

            // ==========================================
            // TAB MODE (MASUK VS DAFTAR / AKTIVASI)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DedisTint, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                // Tab Masuk
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (authMode == AuthMode.MASUK) DedisWhite else DedisTint)
                        .clickable {
                            authMode = AuthMode.MASUK
                            errorMessage = null
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Masuk dengan PIN",
                        fontSize = 13.sp,
                        fontWeight = if (authMode == AuthMode.MASUK) FontWeight.Bold else FontWeight.Medium,
                        color = if (authMode == AuthMode.MASUK) DedisPrimary else DedisMuted
                    )
                }

                // Tab Daftar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (authMode == AuthMode.DAFTAR) DedisWhite else DedisTint)
                        .clickable {
                            authMode = AuthMode.DAFTAR
                            errorMessage = null
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedRole == AppRole.ORANG_TUA) "Daftar Akun Baru" else "Aktivasi Kader",
                        fontSize = 13.sp,
                        fontWeight = if (authMode == AuthMode.DAFTAR) FontWeight.Bold else FontWeight.Medium,
                        color = if (authMode == AuthMode.DAFTAR) DedisPrimary else DedisMuted
                    )
                }
            }

            // Pesan Error Validasi (jika ada)
            AnimatedVisibility(visible = errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
                Surface(
                    color = DedisDangerBg,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_warning_amber),
                            contentDescription = null,
                            tint = DedisDangerText,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = DedisDangerText
                        )
                    }
                }
            }

            // ==========================================
            // FORM INPUT DENGAN LOGIKA VALIDASI PIN
            // ==========================================
            DedisCard(contentPadding = 18.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    // Jika Mode DAFTAR Orang Tua: Nama Lengkap
                    if (authMode == AuthMode.DAFTAR && selectedRole == AppRole.ORANG_TUA) {
                        DedisTextField(
                            value = namaLengkap,
                            onValueChange = {
                                namaLengkap = it
                                errorMessage = null
                            },
                            label = "Nama Lengkap Orang Tua",
                            placeholder = "cth. Ibu Nur Aini",
                            leadingIcon = R.drawable.ic_person
                        )

                        DedisTextField(
                            value = namaAnak,
                            onValueChange = {
                                namaAnak = it
                                errorMessage = null
                            },
                            label = "Nama Balita",
                            placeholder = "cth. Aisyah Putri",
                            leadingIcon = R.drawable.ic_sprout_leaf
                        )
                    }

                    // Jika Mode DAFTAR / AKTIVASI Kader: Kode Undangan & Nama
                    if (authMode == AuthMode.DAFTAR && selectedRole == AppRole.KADER) {
                        DedisTextField(
                            value = kodeUndanganKader,
                            onValueChange = {
                                kodeUndanganKader = it.uppercase()
                                errorMessage = null
                            },
                            label = "Kode Undangan Resmi",
                            placeholder = "cth. KDR-ANGGREK4",
                            leadingIcon = R.drawable.ic_shield_check
                        )

                        DedisTextField(
                            value = namaLengkap,
                            onValueChange = {
                                namaLengkap = it
                                errorMessage = null
                            },
                            label = "Nama Lengkap Kader",
                            placeholder = "cth. Kader Sari",
                            leadingIcon = R.drawable.ic_person
                        )
                    }

                    // Field Nomor HP / ID Pengguna
                    DedisTextField(
                        value = noHpOrId,
                        onValueChange = {
                            noHpOrId = it
                            errorMessage = null
                        },
                        label = if (selectedRole == AppRole.ORANG_TUA) "Nomor WhatsApp / HP" else "ID Kader / No. HP",
                        placeholder = if (selectedRole == AppRole.ORANG_TUA) "08xxxxxxxxxx" else "cth. KDR-01 atau 08xxx",
                        leadingIcon = R.drawable.ic_person,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (selectedRole == AppRole.ORANG_TUA) KeyboardType.Phone else KeyboardType.Text
                        )
                    )

                    // Field PIN 6-Digit
                    DedisTextField(
                        value = pin,
                        onValueChange = {
                            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                pin = it
                                errorMessage = null
                            }
                        },
                        label = if (authMode == AuthMode.MASUK) "PIN Keamanan (6 Digit)" else "Buat 6-Digit PIN",
                        placeholder = "Masukkan 6 digit angka",
                        leadingIcon = R.drawable.ic_lock,
                        visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        trailingIcon = {
                            Text(
                                text = if (isPinVisible) "Sembunyikan" else "Lihat",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DedisPrimary,
                                modifier = Modifier
                                    .clickable { isPinVisible = !isPinVisible }
                                    .padding(4.dp)
                            )
                        }
                    )

                    // Jika Mode DAFTAR: Konfirmasi PIN
                    if (authMode == AuthMode.DAFTAR) {
                        DedisTextField(
                            value = konfirmasiPin,
                            onValueChange = {
                                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                    konfirmasiPin = it
                                    errorMessage = null
                                }
                            },
                            label = "Konfirmasi PIN 6 Digit",
                            placeholder = "Ulangi 6 digit PIN",
                            leadingIcon = R.drawable.ic_lock,
                            visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Tombol Aksi Utama
                    val buttonTitle = when {
                        authMode == AuthMode.MASUK && selectedRole == AppRole.ORANG_TUA -> "Masuk sebagai Orang Tua"
                        authMode == AuthMode.MASUK && selectedRole == AppRole.KADER -> "Masuk Ruang Kader"
                        authMode == AuthMode.DAFTAR && selectedRole == AppRole.ORANG_TUA -> "Daftar & Masuk"
                        else -> "Aktivasi Akun Kader"
                    }

                    DedisPrimaryButton(
                        text = buttonTitle,
                        onClick = { handleAuthSubmit() }
                    )
                }
            }

            // ==========================================
            // KONTEN BAWAH (AGAR LAYAR TIDAK KOSONG)
            // ==========================================
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DedisSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = null,
                            tint = DedisPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Pusat Layanan Posyandu Anggrek 4",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DedisInk
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Lupa PIN atau balita belum terdata? Kunjungi Posyandu Anggrek 4 setiap tanggal 7 atau hubungi Bidan Desa.",
                            fontSize = 11.sp,
                            color = DedisMuted,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Enkripsi & Keamanan Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_shield_check),
                    contentDescription = null,
                    tint = DedisMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Data kesehatan terenkripsi & sesuai standar antropometri WHO",
                    fontSize = 11.sp,
                    color = DedisMuted
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectionScreenPreview() {
    DEDISTheme {
        RoleSelectionScreen()
    }
}
