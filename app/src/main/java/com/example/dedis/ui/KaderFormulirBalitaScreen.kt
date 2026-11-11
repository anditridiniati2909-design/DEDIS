package com.example.dedis.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.components.DedisChoiceChip
import com.example.dedis.ui.components.DedisHeader
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.components.DedisTextField
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisWhite

@Composable
fun KaderFormulirBalitaScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavigateToRoute: (String) -> Unit = {}
) {
    var namaAnak by remember { mutableStateOf("") }
    var tanggalLahir by remember { mutableStateOf("") }
    var jenisKelamin by remember { mutableStateOf("Perempuan") }
    var namaIbu by remember { mutableStateOf("") }
    var dusun by remember { mutableStateOf("") }
    var isPersetujuanChecked by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DedisWhite,
        bottomBar = {
            KaderBottomBar(
                currentRoute = DedisDestinations.KADER_FORMULIR,
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
                title = "Tambah Balita",
                subtitle = "Pendaftaran sasaran balita baru di Posyandu",
                showBackButton = true,
                onBackClick = onNavigateBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Isian: nama anak
                DedisTextField(
                    value = namaAnak,
                    onValueChange = { namaAnak = it },
                    label = "Nama Lengkap Anak",
                    placeholder = "Contoh: Aisyah Putri"
                )

                // Isian: tanggal lahir
                DedisTextField(
                    value = tanggalLahir,
                    onValueChange = { tanggalLahir = it },
                    label = "Tanggal Lahir",
                    placeholder = "Contoh: 7 September 2024",
                    leadingIcon = R.drawable.ic_calendar
                )

                // Jenis kelamin (chip Perempuan / Laki-laki)
                Column {
                    Text(
                        text = "Jenis Kelamin",
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
                            text = "Perempuan",
                            isSelected = jenisKelamin == "Perempuan",
                            onClick = { jenisKelamin = "Perempuan" },
                            modifier = Modifier.weight(1f)
                        )
                        DedisChoiceChip(
                            text = "Laki-laki",
                            isSelected = jenisKelamin == "Laki-laki",
                            onClick = { jenisKelamin = "Laki-laki" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Isian: nama ibu/pengasuh
                DedisTextField(
                    value = namaIbu,
                    onValueChange = { namaIbu = it },
                    label = "Nama Ibu atau Pengasuh",
                    placeholder = "Contoh: Nur Aini",
                    leadingIcon = R.drawable.ic_person
                )

                // Isian: dusun
                DedisTextField(
                    value = dusun,
                    onValueChange = { dusun = it },
                    label = "Dusun / RW",
                    placeholder = "Contoh: Borongloe RW 04",
                    leadingIcon = R.drawable.ic_home
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Kotak persetujuan orang tua
                DedisCard(contentPadding = 12.dp) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { isPersetujuanChecked = !isPersetujuanChecked }
                    ) {
                        Checkbox(
                            checked = isPersetujuanChecked,
                            onCheckedChange = { isPersetujuanChecked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = DedisPrimary,
                                uncheckedColor = DedisMuted
                            )
                        )
                        Text(
                            text = "Orang tua atau wali telah menyetujui pendataan balita untuk pemantauan tumbuh kembang posyandu.",
                            fontSize = 12.sp,
                            color = DedisInk,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Catatan alur
                Text(
                    text = "Undangan akun orang tua dibuat sebagai langkah terpisah setelah data tersimpan.",
                    fontSize = 12.sp,
                    color = DedisMuted,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tombol utama "Simpan data balita"
                DedisPrimaryButton(
                    text = "Simpan Data Balita",
                    onClick = onNavigateBack,
                    enabled = namaAnak.isNotBlank() && isPersetujuanChecked
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KaderFormulirBalitaScreenPreview() {
    DEDISTheme {
        KaderFormulirBalitaScreen()
    }
}
