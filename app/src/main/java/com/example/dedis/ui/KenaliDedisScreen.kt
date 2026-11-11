package com.example.dedis.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.ui.components.DedisPrimaryButton
import com.example.dedis.ui.components.DedisSecondaryButton
import com.example.dedis.ui.theme.DEDISTheme
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisLine
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisSoft
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWhite
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private data class OnboardingPageData(
    val badge: String,
    val title: String,
    val description: String,
    val characterType: CharacterType,
    val iconRes: Int
)

enum class CharacterType {
    GROWTH,    // Karakter mengukur tumbuh kembang
    PROTECTOR, // Karakter pelindung stunting
    NUTRITION  // Karakter panduan nutrisi & jadwal
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KenaliDedisScreen(
    modifier: Modifier = Modifier,
    onNavigateToMasukDaftar: () -> Unit = {}
) {
    val pages = listOf(
        OnboardingPageData(
            badge = "Standar WHO & Kemenkes",
            title = "Pantau Tumbuh Kembang",
            description = "Catat penimbangan berat badan, tinggi, dan lingkar kepala balita secara berkala dengan akurat.",
            characterType = CharacterType.GROWTH,
            iconRes = R.drawable.ic_ruler_chart
        ),
        OnboardingPageData(
            badge = "Akurat & Siaga Dini",
            title = "Deteksi Stunting Cepat",
            description = "Z-Score dihitung otomatis sesuai standar antropometri untuk penanganan tanggap bersama bidan.",
            characterType = CharacterType.PROTECTOR,
            iconRes = R.drawable.ic_shield_check
        ),
        OnboardingPageData(
            badge = "Posyandu Anggrek 4",
            title = "Jadwal & Panduan Gizi",
            description = "Pengingat imunisasi bulanan tanggal 7, jadwal posyandu, serta edukasi MPASI kaya gizi langsung di genggaman.",
            characterType = CharacterType.NUTRITION,
            iconRes = R.drawable.ic_calendar
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val isFirstPage = pagerState.currentPage == 0
    val isLastPage = pagerState.currentPage == pages.size - 1

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        containerColor = DedisWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Logo DEDIS & Tombol "Lewati"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DEDIS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisPrimary
                )

                if (!isLastPage) {
                    Text(
                        text = "Lewati",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DedisMuted,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onNavigateToMasukDaftar() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                } else {
                    Text(
                        text = "POSYANDU",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DedisMuted,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Area Pager Tengah dengan Transisi Halus (Smooth Scaling & Alpha Translation)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                val page = pages[pageIndex]

                // Menghitung offset relatif untuk efek transisi pergeseran halaman
                val pageOffset = (
                    (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                ).absoluteValue

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                        .graphicsLayer {
                            // Transisi Zoom & Fade saat pergeseran slide
                            val scale = 1f - (pageOffset * 0.15f).coerceIn(0f, 1f)
                            scaleX = scale
                            scaleY = scale
                            alpha = 1f - (pageOffset * 0.6f).coerceIn(0f, 1f)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Karakter Animasi Lucu & Ramah Balita DEDIS
                    DedisCharacterIllustration(
                        characterType = page.characterType,
                        iconRes = page.iconRes
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // Chip / Badge kategori di atas judul
                    Surface(
                        color = DedisTint,
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text(
                            text = page.badge,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DedisPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Judul Layar
                    Text(
                        text = page.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DedisInk,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Deskripsi Layar
                    Text(
                        text = page.description,
                        fontSize = 14.sp,
                        color = DedisMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            // Bagian Bawah: Indikator Dots & Tombol Aksi
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Indikator Titik-Titik Pager (Dots Indicator)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 28.dp else 8.dp,
                            label = "dotWidth"
                        )
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) DedisPrimary else DedisLine)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Tombol Navigasi
                if (isLastPage) {
                    // Di Halaman Terakhir: Tombol Sebelumnya (opsional) + Tombol Utama "Mulai Sekarang"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DedisSecondaryButton(
                            text = "Sebelumnya",
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        DedisPrimaryButton(
                            text = "Mulai Sekarang",
                            onClick = onNavigateToMasukDaftar,
                            modifier = Modifier.weight(1.3f)
                        )
                    }
                } else if (isFirstPage) {
                    // Di Halaman Pertama: Tombol "Sebelumnya" DIHILANGKAN, hanya ada tombol "Lanjut" penuh
                    DedisPrimaryButton(
                        text = "Lanjut",
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Di Halaman Tengah: Tombol "Sebelumnya" dan "Lanjut"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DedisSecondaryButton(
                            text = "Sebelumnya",
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        DedisPrimaryButton(
                            text = "Lanjut",
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Karakter Maskot Animasi Ramah Balita DEDIS dengan Efek Mengambang (Floating Animation)
 */
@Composable
private fun DedisCharacterIllustration(
    characterType: CharacterType,
    iconRes: Int,
    modifier: Modifier = Modifier
) {
    // Animasi mengambang / melayang lembut (floating animation)
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatingOffset"
    )

    Box(
        modifier = modifier
            .size(180.dp)
            .offset(y = offsetY.dp),
        contentAlignment = Alignment.Center
    ) {
        // Lingkaran Latar Belakang Luar Bergradasi Lembut
        Box(
            modifier = Modifier
                .size(176.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            DedisSoft.copy(alpha = 0.8f),
                            DedisTint.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Lingkaran Utama Wajah Karakter Balita "Si Dedis Kecil"
        Box(
            modifier = Modifier
                .size(136.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFF7ED)) // Warna kulit hangat lembut bayi
                .border(3.dp, DedisSoft, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Topi Daun Tunas Kecambah Khas DEDIS di atas kepala
                Box(
                    modifier = Modifier
                        .offset(y = (-6).dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(DedisTint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sprout_leaf),
                        contentDescription = null,
                        tint = DedisPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Mata tersenyum ceria (dua lengkung mata tertawa)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(22.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mata Kiri
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(DedisInk)
                    )
                    // Mata Kanan
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(DedisInk)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Pipi Merona Merah Muda & Senyuman Manis
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pipi Merona Kiri
                    Box(
                        modifier = Modifier
                            .size(12.dp, 6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFB4AB).copy(alpha = 0.8f))
                    )

                    // Mulut Senyum
                    Box(
                        modifier = Modifier
                            .size(10.dp, 5.dp)
                            .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                            .background(Color(0xFFC05244))
                    )

                    // Pipi Merona Kanan
                    Box(
                        modifier = Modifier
                            .size(12.dp, 6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFB4AB).copy(alpha = 0.8f))
                    )
                }
            }
        }

        // Badge Lencana Fitur di samping karakter sesuai jenis halaman
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-4).dp, y = (-4).dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(DedisPrimary)
                .border(2.dp, DedisWhite, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = DedisWhite,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KenaliDedisScreenPreview() {
    DEDISTheme {
        KenaliDedisScreen()
    }
}
