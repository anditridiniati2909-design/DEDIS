package com.example.dedis.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisLine
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWarningBg
import com.example.dedis.ui.theme.DedisWarningText
import com.example.dedis.ui.theme.DedisDangerBg
import com.example.dedis.ui.theme.DedisDangerText
import com.example.dedis.ui.theme.DedisWhite

data class MeasurementPoint(
    val ageLabel: String,      // "22 bln"
    val value: Float,          // cm or kg
    val zScore: Float,         // Z-score
    val dateLabel: String,     // "7 Jul 2026"
    val status: String         // "Normal" / "Perlu perhatian" / "Risiko tinggi"
)

/**
 * GrowthGraphScreen — Kartu grafik batang tumbuh kembang anak.
 * - Maks 6 pengukuran terakhir, urut dari lama ke baru.
 * - Warna batang sesuai DESIGN.md status.
 * - Batang terbaru lebih tegas (lebar lebih besar).
 * - Garis putus-putus WHO reference.
 * - Ketuk batang → keterangan kecil (tanggal, nilai, z-score).
 * - Jika < 2 pengukuran → teks singkat.
 */
@Composable
fun GrowthGraphCard(
    measurements: List<MeasurementPoint>,
    whoReference: Float? = null,
    whoLabel: String = "",
    isHeightMetric: Boolean = true,
    modifier: Modifier = Modifier
) {
    val unit = if (isHeightMetric) "cm" else "kg"

    if (measurements.size < 2) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(DedisTint, RoundedCornerShape(18.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Grafik muncul setelah 2 kali ukur.",
                fontSize = 13.sp,
                color = DedisMuted
            )
        }
        return
    }

    val data = measurements.takeLast(6)
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(600))
    }

    // Colors per status
    fun barColor(status: String): Color = when (status.lowercase()) {
        "normal" -> DedisPrimary
        "perlu perhatian", "perhatian", "cek" -> DedisWarningText
        "risiko tinggi", "sangat pendek" -> DedisDangerText
        else -> DedisMuted
    }
    fun barBg(status: String): Color = when (status.lowercase()) {
        "normal" -> DedisTint
        "perlu perhatian", "perhatian", "cek" -> DedisWarningBg
        "risiko tinggi", "sangat pendek" -> DedisDangerBg
        else -> DedisLine
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DedisWhite, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Grafik ${if (isHeightMetric) "Tinggi Badan" else "Berat Badan"}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = DedisInk
        )

        val minVal = (data.minOf { it.value } * 0.97f)
        val maxVal = maxOf(
            data.maxOf { it.value } * 1.03f,
            whoReference?.times(1.03f) ?: Float.MIN_VALUE
        )
        val range = maxVal - minVal

        val prog = animProgress.value

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .pointerInput(data) {
                    detectTapGestures { offset ->
                        val barAreaWidth = size.width.toFloat()
                        val barWidth = barAreaWidth / (data.size * 2)
                        data.indices.forEach { i ->
                            val centerX = barWidth + i * barAreaWidth / data.size + barWidth * 0.5f
                            if (offset.x in (centerX - barWidth)..(centerX + barWidth)) {
                                selectedIndex = if (selectedIndex == i) -1 else i
                            }
                        }
                    }
                }
        ) {
            val chartHeight = size.height - 28.dp.toPx()
            val barSlotWidth = size.width / data.size
            val barWidth = barSlotWidth * 0.45f
            val lastBarWidth = barSlotWidth * 0.58f

            // WHO reference line (putus-putus)
            whoReference?.let { ref ->
                val refY = chartHeight - ((ref - minVal) / range) * chartHeight
                drawLine(
                    color = DedisMuted,
                    start = Offset(0f, refY),
                    end = Offset(size.width, refY),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
                )
            }

            data.forEachIndexed { i, point ->
                val isLast = (i == data.size - 1)
                val w = if (isLast) lastBarWidth else barWidth
                val centerX = barSlotWidth * i + barSlotWidth / 2f
                val left = centerX - w / 2f
                val barH = ((point.value - minVal) / range) * chartHeight * prog
                val top = chartHeight - barH

                // Bar
                drawRect(
                    color = barColor(point.status).copy(alpha = if (selectedIndex == i) 1f else 0.82f),
                    topLeft = Offset(left, top),
                    size = Size(w, barH)
                )

                // Value label above bar
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        textSize = 11.dp.toPx()
                        color = android.graphics.Color.parseColor(if (isLast) "#203B32" else "#61736B")
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = isLast
                    }
                    drawText(
                        "%.1f".format(point.value),
                        centerX,
                        (top - 4.dp.toPx()).coerceAtLeast(14.dp.toPx()),
                        paint
                    )
                }

                // Age label below
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        textSize = 10.dp.toPx()
                        color = android.graphics.Color.parseColor("#61736B")
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    drawText(point.ageLabel, centerX, chartHeight + 18.dp.toPx(), paint)
                }
            }

            // WHO label
            whoReference?.let { ref ->
                val refY = chartHeight - ((ref - minVal) / range) * chartHeight
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        textSize = 9.dp.toPx()
                        color = android.graphics.Color.parseColor("#61736B")
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                    drawText(whoLabel, size.width - 2.dp.toPx(), refY - 3.dp.toPx(), paint)
                }
            }
        }

        // Tooltip untuk batang yang dipilih
        if (selectedIndex in data.indices) {
            val pt = data[selectedIndex]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DedisTint, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(pt.dateLabel, fontSize = 11.sp, color = DedisMuted)
                        Text(
                            "${pt.value} $unit",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = barColor(pt.status)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Z-Score", fontSize = 10.sp, color = DedisMuted)
                        Text(
                            "${"%.1f".format(pt.zScore)} SD",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = barColor(pt.status)
                        )
                    }
                }
            }
        }
    }
}

