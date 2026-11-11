package com.example.dedis.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
 * Layar Ubah Profil — nama, nomor HP/WhatsApp, dusun.
 * Validasi: nama wajib, nomor HP angka minimal 10 digit.
 * Simpan ke backend via PUT /api/v1/profile.
 */
@Composable
fun UbahProfilScreen(
    modifier: Modifier = Modifier,
    // Pre-filled from logged-in user data
    initialNama: String = "",
    initialNoHp: String = "",
    initialDusun: String = "",
    onNavigateBack: () -> Unit = {},
    onSimpan: (nama: String, noHp: String, dusun: String) -> Unit = { _, _, _ -> }
) {
    var nama by remember { mutableStateOf(initialNama) }
    var noHp by remember { mutableStateOf(initialNoHp) }
    var dusun by remember { mutableStateOf(initialDusun) }

    var namaError by remember { mutableStateOf<String?>(null) }
    var noHpError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun validate(): Boolean {
        var valid = true
        if (nama.trim().isEmpty()) {
            namaError = "Nama wajib diisi"; valid = false
        } else namaError = null

        val cleanHp = noHp.trim()
        if (cleanHp.isEmpty()) {
            noHpError = "Nomor HP wajib diisi"; valid = false
        } else if (!cleanHp.all { it.isDigit() } || cleanHp.length < 10) {
            noHpError = "Nomor HP harus berupa angka minimal 10 digit"; valid = false
        } else noHpError = null

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
                Text(
                    text = "Ubah Profil",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisInk
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Perbarui informasi akun Anda",
                    fontSize = 13.sp,
                    color = DedisMuted
                )
            }

            DedisCard(contentPadding = 18.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    DedisTextField(
                        value = nama,
                        onValueChange = { nama = it; namaError = null },
                        label = "Nama Lengkap",
                        placeholder = "cth. Ibu Nur Aini",
                        leadingIcon = R.drawable.ic_person,
                        isError = namaError != null,
                        errorMessage = namaError
                    )

                    DedisTextField(
                        value = noHp,
                        onValueChange = { noHp = it; noHpError = null },
                        label = "Nomor WhatsApp / HP",
                        placeholder = "08xxxxxxxxxx",
                        leadingIcon = R.drawable.ic_person,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = noHpError != null,
                        errorMessage = noHpError
                    )

                    DedisTextField(
                        value = dusun,
                        onValueChange = { dusun = it },
                        label = "Dusun / Alamat (opsional)",
                        placeholder = "cth. Borongloe",
                        leadingIcon = R.drawable.ic_info
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    DedisPrimaryButton(
                        text = "Simpan Perubahan",
                        onClick = {
                            if (validate()) {
                                onSimpan(nama.trim(), noHp.trim(), dusun.trim())
                                scope.launch {
                                    snackbarHostState.showSnackbar("Profil berhasil disimpan!")
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

