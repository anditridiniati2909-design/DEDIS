package com.example.dedis.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dedis.R
import com.example.dedis.ui.theme.DedisDangerBg
import com.example.dedis.ui.theme.DedisDangerText
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisLine
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisNeutralBg
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisTint
import com.example.dedis.ui.theme.DedisWhite

// ==========================================
// 1. HEADER HALAMAN (DESIGN.md Bagian 3)
// ==========================================
@Composable
fun DedisHeader(
    modifier: Modifier = Modifier,
    spaceRole: String = "RUANG ORANG TUA", // "RUANG ORANG TUA" / "RUANG KADER"
    title: String? = null,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onMessageClick: (() -> Unit)? = null,
    unreadMessageCount: Int = 0
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Baris navigasi atas: "DEDIS" - nama alur - ikon pesan/lonceng
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBackButton) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onBackClick() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Kembali",
                        tint = DedisPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DEDIS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DedisPrimary
                    )
                }
            } else {
                Text(
                    text = "DEDIS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DedisPrimary
                )
            }

            // Nama alur di tengah (11sp, muted)
            Text(
                text = spaceRole,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = DedisMuted,
                letterSpacing = 0.5.sp
            )

            // Aksi kanan: Ikon pesan (opsional) + Ikon lonceng notifikasi
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (onMessageClick != null) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { onMessageClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chat),
                            contentDescription = "Pesan Masuk",
                            tint = DedisPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        if (unreadMessageCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(16.dp)
                                    .background(DedisDangerText, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadMessageCount > 9) "9+" else unreadMessageCount.toString(),
                                    color = DedisWhite,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bell),
                        contentDescription = "Notifikasi",
                        tint = DedisPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Judul layar (26sp, Bold) jika ada
        if (!title.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = DedisInk,
                lineHeight = 32.sp
            )
        }

        // Subjudul opsional (13-14sp, muted)
        if (!subtitle.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = DedisMuted,
                lineHeight = 18.sp
            )
        }
    }
}

// ==========================================
// 2. KARTU DEDIS (DESIGN.md Bagian 2)
// Kartu: putih, border 1 dp line, radius 18, padding 16-18
// ==========================================
@Composable
fun DedisCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = DedisWhite,
    borderColor: Color = DedisLine,
    contentPadding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        shape = shape,
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

// ==========================================
// 3. STATUS CHIP (DESIGN.md Bagian 6)
// Normal: tint / primary
// Perhatian / Perlu perhatian / Cek: warning-bg / warning-text
// Risiko tinggi: danger-bg / danger-text
// Belum diukur: neutral-bg / muted
// ==========================================
@Composable
fun DedisStatusChip(
    status: String,
    modifier: Modifier = Modifier,
    isSmall: Boolean = false
) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "normal" -> Pair(DedisTint, DedisPrimary)
        "perhatian", "perlu perhatian", "cek", "pendek", "pantau" -> Pair(com.example.dedis.ui.theme.DedisWarningBg, com.example.dedis.ui.theme.DedisWarningText)
        "risiko tinggi", "sangat pendek", "bahaya" -> Pair(DedisDangerBg, DedisDangerText)
        else -> Pair(DedisNeutralBg, DedisMuted) // Belum diukur, dll.
    }

    val horizontalPadding = if (isSmall) 10.dp else 14.dp
    val verticalPadding = if (isSmall) 4.dp else 7.dp
    val fontSize = if (isSmall) 11.sp else 12.sp

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(100.dp),
        modifier = modifier
    ) {
        Text(
            text = status,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding)
        )
    }
}

// ==========================================
// 4. PILIHAN FILTER / WAKTU CHIP
// ==========================================
@Composable
fun DedisChoiceChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) DedisPrimary else DedisWhite,
        shape = RoundedCornerShape(100.dp),
        border = if (isSelected) null else BorderStroke(1.dp, DedisLine),
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) DedisWhite else DedisInk,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
        )
    }
}

// ==========================================
// 5. TOMBOL UTAMA & SEKUNDER (DESIGN.md Bagian 2)
// Tombol: tinggi ±54, radius 12.
// Utama = isi primary teks putih.
// Sekunder = putih, border primary.
// ==========================================
@Composable
fun DedisPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: Int? = null,
    fillMaxWidth: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DedisPrimary,
            contentColor = DedisWhite,
            disabledContainerColor = DedisLine,
            disabledContentColor = DedisMuted
        ),
        modifier = modifier
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .height(54.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = null,
                    tint = DedisWhite,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DedisSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: Int? = null,
    fillMaxWidth: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, if (enabled) DedisPrimary else DedisLine),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = DedisWhite,
            contentColor = DedisPrimary,
            disabledContainerColor = DedisWhite,
            disabledContentColor = DedisMuted
        ),
        modifier = modifier
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .height(54.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = null,
                    tint = DedisPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DedisPrimary
            )
        }
    }
}

@Composable
fun DedisDangerOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Int? = null,
    fillMaxWidth: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, DedisDangerText),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = DedisWhite,
            contentColor = DedisDangerText
        ),
        modifier = modifier
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .height(54.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = null,
                    tint = DedisDangerText,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DedisDangerText
            )
        }
    }
}

// ==========================================
// 6. KOLOM ISIAN / TEXT FIELD (DESIGN.md)
// ==========================================
@Composable
fun DedisTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    leadingIcon: Int? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (!label.isNullOrEmpty()) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isError) DedisDangerText else DedisInk
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(DedisWhite, RoundedCornerShape(12.dp))
                .border(
                    width = if (isError) 1.5.dp else 1.dp,
                    color = if (isError) DedisDangerText else DedisLine,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    Icon(
                        painter = painterResource(id = leadingIcon),
                        contentDescription = null,
                        tint = if (isError) DedisDangerText else DedisMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            color = DedisMuted
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = DedisInk
                        ),
                        cursorBrush = SolidColor(if (isError) DedisDangerText else DedisPrimary),
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        visualTransformation = visualTransformation,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    trailingIcon()
                }
            }
        }

        if (isError && !errorMessage.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = DedisDangerText
            )
        }
    }
}

// ==========================================
// 7. AVATAR INISIAL ANAK / PENGGUNA
// ==========================================
@Composable
fun DedisAvatar(
    initial: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    backgroundColor: Color = DedisTint,
    textColor: Color = DedisPrimary
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            fontSize = (size.value * 0.42).sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

