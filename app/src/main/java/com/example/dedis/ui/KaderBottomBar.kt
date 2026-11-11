package com.example.dedis.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.ui.theme.DedisLine
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWhite

@Composable
fun KaderBottomBar(
    currentRoute: String,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Alur Kader: Beranda · Balita · Layanan · Laporan · Profil (DESIGN.md Bagian 3)
    val navItems = listOf(
        Triple("Beranda", DedisDestinations.KADER_BERANDA, R.drawable.ic_home),
        Triple("Balita", DedisDestinations.KADER_DAFTAR_BALITA, R.drawable.ic_sprout_leaf),
        Triple("Layanan", DedisDestinations.KADER_PENGUKURAN, R.drawable.ic_clipboard_list),
        Triple("Laporan", DedisDestinations.KADER_LAPORAN, R.drawable.ic_ruler_chart),
        Triple("Profil", DedisDestinations.KADER_PROFIL, R.drawable.ic_person)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DedisWhite,
        border = BorderStroke(width = 1.dp, color = DedisLine)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // Mencegah bottom bar tenggelam di balik tombol navigasi sistem Android
                .padding(vertical = 6.dp, horizontal = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for ((label, route, iconRes) in navItems) {
                    val isSelected = currentRoute == route
                    val itemBg = if (isSelected) DedisTint else Color.Transparent
                    val itemColor = if (isSelected) DedisPrimary else DedisMuted
                    val textWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(itemBg)
                            .clickable {
                                if (!isSelected) {
                                    onNavigateToRoute(route)
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = label,
                            tint = itemColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = textWeight,
                            color = itemColor
                        )
                    }
                }
            }
        }
    }
}
