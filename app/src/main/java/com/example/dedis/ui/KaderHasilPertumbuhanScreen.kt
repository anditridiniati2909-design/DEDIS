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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.data.AppState
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.components.DedisHeader
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.components.DedisSecondaryButton
import com.example.dedis.ui.components.DedisStatusChip
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisDangerBg
import com.example.dedis.ui.theme.DedisDangerText
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWarningBg
import com.example.dedis.ui.theme.DedisWarningText
import com.example.dedis.ui.theme.DedisWhite
import java.util.Locale

@Composable
fun KaderHasilPertumbuhanScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavigateToTindakLanjut: () -> Unit = {},
    onNavigateToRoute: (String) -> Unit = {}
) {
    val child = AppState.latestMeasurementChild
    val measurement = AppState.latestMeasurementResult

    // Format nilai berat dan tinggi
    val beratText = measurement?.let {
        String.format(Locale.getDefault(), "%.1f", it.weightKg).replace('.', ',')
    } ?: "10,2"

    val tinggiText = measurement?.let {
        String.format(Locale.getDefault(), "%.1f", it.heightCm).replace('.', ',')
    } ?: "79,2"

    // Format Z-score & Status
    val statusHfaRaw = measurement?.statusHfa?.lowercase() ?: "pendek"
    val isDangerHfa = statusHfaRaw == "sangat_pendek"
    val isWarningHfa = statusHfaRaw == "pendek" || isDangerHfa

    val statusBadgeText = when (statusHfaRaw) {
        "normal" -> "Normal"
        "pendek" -> "Perlu perhatian"
        "sangat_pendek" -> "Risiko tinggi"
        "tinggi" -> "Normal"
        else -> if (measurement != null) "Normal" else "Perlu perhatian"
    }

    val tbuZScoreText = measurement?.zScoreHfa?.let {
        val sign = if (it > 0) "+" else ""
        "$sign${String.format(Locale.getDefault(), "%.1f", it).replace('.', ',')} SD"
    } ?: "-2,1 SD"

    val tbuStatusText = when (statusHfaRaw) {
        "sangat_pendek" -> "Sangat Pendek"
        "pendek" -> "Pendek"
        "normal" -> "Normal"
        "tinggi" -> "Tinggi"
        else -> "Pendek"
    }

    val bbuZScoreText = measurement?.zScoreWfa?.let {
        val sign = if (it > 0) "+" else ""
        "$sign${String.format(Locale.getDefault(), "%.1f", it).replace('.', ',')} SD"
    } ?: "-1,4 SD"

    val bbuStatusText = when (measurement?.statusWfa?.lowercase()) {
        "sangat_kurang" -> "Sangat Kurang"
        "kurang" -> "Kurang"
        "normal", "gizi_baik" -> "Normal"
        "risiko_berat_lebih" -> "Risiko Lebih"
        else -> "Normal"
    }

    val bbtbZScoreText = measurement?.zScoreWfh?.let {
        val sign = if (it > 0) "+" else ""
        "$sign${String.format(Locale.getDefault(), "%.1f", it).replace('.', ',')} SD"
    } ?: "+0,2 SD"

    val bbtbStatusText = when (measurement?.statusWfh?.lowercase()) {
        "gizi_buruk" -> "Gizi Buruk"
        "gizi_kurang" -> "Gizi Kurang"
        "gizi_baik", "normal" -> "Gizi Baik"
        "berisiko_lebih" -> "Berisiko Lebih"
        "gizi_lebih" -> "Gizi Lebih"
        "obesitas" -> "Obesitas"
        else -> "Gizi Baik"
    }

    val needsAttention = measurement?.needsAttention == true || isWarningHfa

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
                title = "Hasil Pertumbuhan",
                subtitle = "${child?.name ?: "Aisyah Putri"} • ${child?.ageInMonths ?: measurement?.ageInMonths ?: 24} bulan • ${child?.genderLabel ?: "Perempuan"}",
                showBackButton = true,
                onBackClick = onNavigateBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Kartu Hasil Utama
                DedisCard {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kesimpulan Status",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DedisInk
                            )
                            DedisStatusChip(status = statusBadgeText)
                        }

                        // Dua angka besar (Berat dan Tinggi)
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
                                        text = beratText,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DedisInk
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "kg",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
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
                                        text = tinggiText,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DedisInk
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "cm",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DedisMuted,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }
                        }

                        // Indikator Z-Score
                        Text(
                            text = "Indikator Standar WHO (Z-Score)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DedisInk
                        )

                        ZScoreRow(
                            label = "TB/U (Tinggi menurut Umur)",
                            value = tbuZScoreText,
                            status = tbuStatusText,
                            isWarning = isWarningHfa,
                            isDanger = isDangerHfa
                        )

                        ZScoreRow(
                            label = "BB/U (Berat menurut Umur)",
                            value = bbuZScoreText,
                            status = bbuStatusText,
                            isWarning = (measurement?.zScoreWfa != null && measurement.zScoreWfa < -2f),
                            isDanger = (measurement?.zScoreWfa != null && measurement.zScoreWfa < -3f)
                        )

                        ZScoreRow(
                            label = "BB/TB (Berat menurut Tinggi)",
                            value = bbtbZScoreText,
                            status = bbtbStatusText,
                            isWarning = (measurement?.zScoreWfh != null && (measurement.zScoreWfh < -2f || measurement.zScoreWfh > 2f)),
                            isDanger = (measurement?.zScoreWfh != null && measurement.zScoreWfh < -3f)
                        )
                    }
                }

                // Kartu Rekomendasi (Sesuai status)
                if (needsAttention) {
                    DedisCard(
                        backgroundColor = if (isDangerHfa) DedisDangerBg else DedisWarningBg,
                        borderColor = if (isDangerHfa) DedisDangerBg else DedisWarningBg,
                        contentPadding = 16.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_warning_amber),
                                contentDescription = null,
                                tint = if (isDangerHfa) DedisDangerText else DedisWarningText,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = measurement?.attentionReason
                                    ?: "Hasil TB/U berada di bawah -2 SD (indikasi risiko stunting). Diperlukan ukur ulang bersama bidan dan pendampingan gizi keluarga.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDangerHfa) DedisDangerText else DedisWarningText,
                                lineHeight = 18.sp
                            )
                        }
                    }
                } else {
                    DedisCard(
                        backgroundColor = DedisTint,
                        borderColor = DedisTint,
                        contentPadding = 16.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check_circle),
                                contentDescription = null,
                                tint = DedisPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Hasil pertumbuhan balita dalam batas normal standar WHO. Lanjutkan pola asuh dan pemantauan rutin di posyandu.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = DedisPrimary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tombol utama: "Lanjut ke Tindak Lanjut"
                DedisPrimaryButton(
                    text = "Lanjut ke Tindak Lanjut",
                    onClick = onNavigateToTindakLanjut,
                    leadingIcon = R.drawable.ic_clipboard_list
                )

                // Tombol sekunder: Kembali ke Detail Balita
                DedisSecondaryButton(
                    text = "Kembali ke Detail Balita",
                    onClick = onNavigateBack
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ZScoreRow(
    label: String,
    value: String,
    status: String,
    isWarning: Boolean,
    isDanger: Boolean = false
) {
    val bg = when {
        isDanger -> DedisDangerBg
        isWarning -> DedisWarningBg
        else -> DedisTint
    }
    val textColor = when {
        isDanger -> DedisDangerText
        isWarning -> DedisWarningText
        else -> DedisMuted
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = DedisInk
            )
            Text(
                text = value,
                fontSize = 11.sp,
                color = textColor
            )
        }

        DedisStatusChip(status = status, isSmall = true)
    }
}

@Preview(showBackground = true)
@Composable
fun KaderHasilPertumbuhanScreenPreview() {
    DEDISTheme {
        KaderHasilPertumbuhanScreen()
    }
}
