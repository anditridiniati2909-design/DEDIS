package com.example.dedis.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.BuildConfig
import com.example.dedis.R
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisLine
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisWhite

/**
 * Layar Tentang DEDIS.
 * Menampilkan nama aplikasi, versi (dari BuildConfig), deskripsi singkat,
 * nama mitra posyandu, dan logo.
 */
@Composable
fun TentangScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize().navigationBarsPadding(),
        containerColor = DedisWhite,
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_dedis),
                contentDescription = "Logo DEDIS",
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Text(
                text = "DEDIS",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DedisPrimary
            )

            Text(
                text = "Versi ${BuildConfig.VERSION_NAME}",
                fontSize = 13.sp,
                color = DedisMuted
            )

            DedisCard(contentPadding = 16.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Tentang Aplikasi",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DedisInk
                    )
                    Text(
                        text = "DEDIS (Deteksi Dini Stunting) adalah aplikasi pemantauan tumbuh kembang balita berbasis standar antropometri WHO dan Kemenkes RI. Dirancang untuk membantu orang tua dan kader Posyandu dalam mendeteksi potensi stunting secara dini dan terpadu.",
                        fontSize = 13.sp,
                        color = DedisMuted,
                        lineHeight = 20.sp
                    )
                }
            }

            DedisCard(contentPadding = 16.dp) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_shield_check),
                        contentDescription = null,
                        tint = DedisPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Mitra",
                            fontSize = 12.sp,
                            color = DedisMuted
                        )
                        Text(
                            text = "Posyandu Anggrek 4",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DedisInk
                        )
                        Text(
                            text = "Dusun Bontocinde, Kabupaten Gowa",
                            fontSize = 12.sp,
                            color = DedisMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

