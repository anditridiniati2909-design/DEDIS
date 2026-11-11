package com.example.dedis.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisSoft
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWhite

@Composable
fun MasukOrangTuaScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavigateToBeranda: () -> Unit = {},
    onNavigateToDaftar: () -> Unit = {}
) {
    var nomorHp by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // State validasi error per kolom
    var hpError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun handleLogin() {
        var isValid = true

        // Validasi kolom nomor HP
        val cleanHp = nomorHp.trim()
        if (cleanHp.isEmpty()) {
            hpError = "Nomor WhatsApp atau HP wajib diisi"
            isValid = false
        } else if (cleanHp.length < 10) {
            hpError = "Nomor HP minimal 10 digit"
            isValid = false
        } else {
            hpError = null
        }

        // Validasi kolom kata sandi / PIN
        if (password.isEmpty()) {
            passwordError = "Kata sandi wajib diisi"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Kata sandi minimal 6 karakter"
            isValid = false
        } else {
            passwordError = null
        }

        if (isValid) {
            onNavigateToBeranda()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        containerColor = DedisWhite
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Bentuk dekoratif lembut di belakang bagian atas (DESIGN.md - DedisTint)
            Box(
                modifier = Modifier
                    .size(340.dp)
                    .offset(x = 60.dp, y = (-90).dp)
                    .clip(CircleShape)
                    .background(DedisTint.copy(alpha = 0.85f))
                    .align(Alignment.TopEnd)
            )

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(x = (-40).dp, y = 80.dp)
                    .clip(CircleShape)
                    .background(DedisSoft.copy(alpha = 0.4f))
                    .align(Alignment.TopStart)
            )

            // Konten Utama
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar Navigasi
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(40.dp)
                    ) {
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
                        fontWeight = FontWeight.SemiBold,
                        color = DedisMuted,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.size(40.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bagian Atas: Logo DEDIS Baru (±88 dp, sudut membulat), teks DEDIS, tagline
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_dedis),
                        contentDescription = "Logo DEDIS",
                        modifier = Modifier
                            .size(88.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "DEDIS",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = DedisPrimary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Deteksi Dini Stunting & Gizi Balita",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = DedisMuted
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Kartu Putih Form Masuk dengan Border & Sudut Membulat (DESIGN.md)
                DedisCard(contentPadding = 20.dp) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Masuk Akun",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DedisInk
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Gunakan nomor HP atau identitas terdaftar",
                                fontSize = 12.sp,
                                color = DedisMuted
                            )
                        }

                        // Kolom Isian 1: Nomor HP / Email
                        DedisTextField(
                            value = nomorHp,
                            onValueChange = {
                                nomorHp = it
                                if (hpError != null) hpError = null
                            },
                            label = "Nomor WhatsApp / HP",
                            placeholder = "08xxxxxxxxxx",
                            leadingIcon = R.drawable.ic_person,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            isError = hpError != null,
                            errorMessage = hpError
                        )

                        // Kolom Isian 2: Kata Sandi / PIN dengan tombol lihat/sembunyikan
                        DedisTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                if (passwordError != null) passwordError = null
                            },
                            label = "Kata Sandi",
                            placeholder = "Masukkan kata sandi",
                            leadingIcon = R.drawable.ic_lock,
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            isError = passwordError != null,
                            errorMessage = passwordError,
                            trailingIcon = {
                                Text(
                                    text = if (isPasswordVisible) "Sembunyikan" else "Lihat",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DedisPrimary,
                                    modifier = Modifier
                                        .clickable { isPasswordVisible = !isPasswordVisible }
                                        .padding(4.dp)
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Tombol Utama: Lebar Penuh & Jelas
                        DedisPrimaryButton(
                            text = "Masuk",
                            onClick = { handleLogin() }
                        )

                        // Tautan Pindah Layar: Daftar Akun Baru
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Belum punya akun? ",
                                fontSize = 13.sp,
                                color = DedisMuted
                            )
                            Text(
                                text = "Daftar di sini",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DedisPrimary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { onNavigateToDaftar() }
                                    .padding(vertical = 4.dp, horizontal = 2.dp)
                            )
                        }
                    }
                }

                // Ruang santai di bawah
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MasukOrangTuaScreenPreview() {
    DEDISTheme {
        MasukOrangTuaScreen()
    }
}
