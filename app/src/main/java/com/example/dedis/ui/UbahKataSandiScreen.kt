package com.example.dedis.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.components.DedisTextField
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisWhite
import kotlinx.coroutines.launch

/**
 * Layar Ubah Kata Sandi.
 * Validasi: sandi lama wajib, sandi baru min 8, konfirmasi harus sama.
 * Error per kolom di bawah field yang salah.
 */
@Composable
fun UbahKataSandiScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onSimpan: (oldPassword: String, newPassword: String) -> Unit = { _, _ -> }
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var oldPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    var oldVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun validate(): Boolean {
        var valid = true
        if (oldPassword.isEmpty()) {
            oldPasswordError = "Kata sandi lama wajib diisi"; valid = false
        } else oldPasswordError = null

        if (newPassword.length < 8) {
            newPasswordError = "Kata sandi baru minimal 8 karakter"; valid = false
        } else newPasswordError = null

        if (confirmPassword != newPassword) {
            confirmPasswordError = "Konfirmasi kata sandi tidak cocok"; valid = false
        } else confirmPasswordError = null

        return valid
    }

    Scaffold(
        modifier = modifier.fillMaxSize().navigationBarsPadding(),
        containerColor = DedisWhite,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Kembali",
                        tint = DedisPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text("Ubah Kata Sandi", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = DedisInk)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Minimal 8 karakter untuk keamanan akun", fontSize = 13.sp, color = DedisMuted)
            }

            DedisCard(contentPadding = 18.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    DedisTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it; oldPasswordError = null },
                        label = "Kata Sandi Lama",
                        placeholder = "Masukkan kata sandi saat ini",
                        leadingIcon = R.drawable.ic_lock,
                        visualTransformation = if (oldVisible) VisualTransformation.None else PasswordVisualTransformation('*'),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = oldPasswordError != null,
                        errorMessage = oldPasswordError,
                        trailingIcon = {
                            Text(
                                text = if (oldVisible) "Sembunyikan" else "Lihat",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DedisPrimary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { oldVisible = !oldVisible }
                                    .padding(4.dp)
                            )
                        }
                    )

                    DedisTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it; newPasswordError = null },
                        label = "Kata Sandi Baru",
                        placeholder = "Minimal 8 karakter",
                        leadingIcon = R.drawable.ic_lock,
                        visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation('*'),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = newPasswordError != null,
                        errorMessage = newPasswordError,
                        trailingIcon = {
                            Text(
                                text = if (newVisible) "Sembunyikan" else "Lihat",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DedisPrimary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { newVisible = !newVisible }
                                    .padding(4.dp)
                            )
                        }
                    )

                    DedisTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; confirmPasswordError = null },
                        label = "Konfirmasi Kata Sandi Baru",
                        placeholder = "Ulangi kata sandi baru",
                        leadingIcon = R.drawable.ic_lock,
                        visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation('*'),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = confirmPasswordError != null,
                        errorMessage = confirmPasswordError
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    DedisPrimaryButton(
                        text = "Simpan Kata Sandi",
                        onClick = {
                            if (validate()) {
                                onSimpan(oldPassword, newPassword)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Kata sandi berhasil diubah!")
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

