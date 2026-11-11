package com.example.dedis.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DedisPrimary,
    onPrimary = DedisWhite,
    primaryContainer = DedisTint,
    onPrimaryContainer = DedisPrimary,
    secondary = DedisMuted,
    onSecondary = DedisWhite,
    background = DedisWhite,
    onBackground = DedisInk,
    surface = DedisWhite,
    onSurface = DedisInk,
    outline = DedisLine
)

@Composable
fun DEDISTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DedisWhite.toArgb()
                window.navigationBarColor = DedisWhite.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = true
                controller.isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DedisTypography,
        content = content
    )
}
