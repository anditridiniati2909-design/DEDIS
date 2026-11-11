package com.example.dedis.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisSoft
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWhite
import com.example.dedis.util.SessionManager
import com.example.dedis.util.SplashDestination
import kotlinx.coroutines.delay

@Composable
fun PembukaDedisScreen(
    onNavigateNext: (SplashDestination) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }

    // Animasi muncul (scale & alpha)
    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }

    // Animasi denyut / pulse cincin luar logo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Logika Splash Screen: periksa sesi, tahan tampilan hingga siap berpindah tanpa flicker
    LaunchedEffect(Unit) {
        val destination = sessionManager.determineInitialDestination()
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = LinearEasing)
        )
        delay(600) // Tahan sebentar untuk identitas visual DEDIS
        onNavigateNext(destination)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DedisWhite)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Lingkaran Logo Animasi
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(150.dp)
            ) {
                // Cincin luar berdenyut lembut
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(DedisSoft.copy(alpha = 0.5f))
                )

                // Lingkaran utama Logo Baru DEDIS (Hati Putih dengan Tunas Hijau)
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(scale.value)
                        .clip(CircleShape)
                        .background(DedisPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_dedis),
                        contentDescription = "Logo DEDIS",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Nama Aplikasi
            Text(
                text = "DEDIS",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DedisPrimary,
                letterSpacing = 3.sp,
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Badge Tagline
            Surface(
                color = DedisTint,
                shape = RoundedCornerShape(100.dp),
                modifier = Modifier.alpha(alpha.value)
            ) {
                Text(
                    text = "Deteksi Dini Stunting",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DedisPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Posyandu Anggrek 4 • Dusun Bontocinde\nKabupaten Gowa",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = DedisMuted,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.alpha(alpha.value)
            )
        }

        // Teks versi di bagian bawah
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(DedisPrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Memuat data Posyandu...",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = DedisMuted
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Standar Antropometri WHO & Kemenkes RI",
                fontSize = 10.sp,
                color = DedisMuted.copy(alpha = 0.7f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PembukaDedisScreenPreview() {
    DEDISTheme {
        PembukaDedisScreen()
    }
}
