package com.example.dedis.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.dedis.ui.components.DedisDangerOutlinedButton
import com.example.dedis.ui.components.DedisHeader
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisDangerText
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisLine
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWhite

@Composable
fun OrangTuaProfilScreen(
    modifier: Modifier = Modifier,
    // Data dinamis dari akun yang login
    userName: String = "Nur Aini",
    childCount: Int = 2,
    posyandu: String = "Posyandu Anggrek 4, Borongloe",
    children: List<Pair<Long, String>> = listOf(1L to "Aisyah Putri", 2L to "Nabil"),
    onNavigateToRoute: (String) -> Unit = {},
    onNavigateToUbahProfil: () -> Unit = {},
    onNavigateToKataSandi: () -> Unit = {},
    onNavigateToNotifikasi: () -> Unit = {},
    onNavigateToQrAnak: () -> Unit = {},
    onNavigateToTentang: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Dialog konfirmasi keluar
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = DedisWhite,
            shape = RoundedCornerShape(18.dp),
            title = {
                Text(
                    text = "Keluar dari akun?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisInk
                )
            },
            text = {
                Text(
                    text = "Anda harus masuk kembali untuk menggunakan aplikasi.",
                    fontSize = 13.sp,
                    color = DedisMuted
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Keluar", color = DedisDangerText, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = DedisMuted)
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DedisWhite,
        bottomBar = {
            OrangTuaBottomBar(
                currentRoute = DedisDestinations.PROFIL,
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
            DedisHeader(
                spaceRole = "RUANG ORANG TUA",
                title = "Profil",
                subtitle = "Kelola akun dan pengaturan keluarga"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Kartu nama: avatar inisial, nama, jumlah anak, posyandu (data dinamis)
                DedisCard(contentPadding = 18.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        DedisAvatar(
                            initial = userName.firstOrNull()?.uppercase() ?: "?",
                            size = 56.dp
                        )
                        Column {
                            Text(
                                text = userName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DedisInk
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Orang tua \u2022 $childCount anak terdaftar",
                                fontSize = 13.sp,
                                color = DedisMuted
                            )
                            Text(
                                text = posyandu,
                                fontSize = 12.sp,
                                color = DedisMuted
                            )
                        }
                    }
                }

                // Daftar menu
                DedisCard(contentPadding = 8.dp) {
                    Column {
                        ProfileMenuItem(
                            title = "Ubah profil",
                            iconRes = R.drawable.ic_person,
                            onClick = onNavigateToUbahProfil
                        )
                        HorizontalDivider(color = DedisLine, thickness = 1.dp)
                        ProfileMenuItem(
                            title = "Kata sandi",
                            iconRes = R.drawable.ic_lock,
                            onClick = onNavigateToKataSandi
                        )
                        HorizontalDivider(color = DedisLine, thickness = 1.dp)
                        ProfileMenuItem(
                            title = "Notifikasi",
                            iconRes = R.drawable.ic_bell,
                            onClick = onNavigateToNotifikasi
                        )
                        HorizontalDivider(color = DedisLine, thickness = 1.dp)
                        ProfileMenuItem(
                            title = "QR anak",
                            iconRes = R.drawable.ic_qr_code,
                            onClick = onNavigateToQrAnak
                        )
                        HorizontalDivider(color = DedisLine, thickness = 1.dp)
                        ProfileMenuItem(
                            title = "Tentang DEDIS",
                            iconRes = R.drawable.ic_info,
                            onClick = onNavigateToTentang
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                DedisDangerOutlinedButton(
                    text = "Keluar dari Akun",
                    onClick = { showLogoutDialog = true },
                    leadingIcon = R.drawable.ic_logout
                )

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    title: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(DedisTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = DedisPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DedisInk
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = DedisMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OrangTuaProfilScreenPreview() {
    DEDISTheme { OrangTuaProfilScreen() }
}
