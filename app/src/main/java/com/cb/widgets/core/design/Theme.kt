package com.cb.widgets.core.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.cb.widgets.core.data.ThemeMode

@Composable
fun isDarkFor(themeMode: ThemeMode): Boolean = when (themeMode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}

@Composable
fun CbTheme(
    themeMode: ThemeMode,
    reduceEffects: Boolean = false,
    content: @Composable () -> Unit
) {
    val dark = isDarkFor(themeMode)
    val palette = if (dark) DarkSkeuPalette else LightSkeuPalette
    val scheme = if (dark) {
        darkColorScheme(
            primary = palette.accent,
            onPrimary = palette.onAccent,
            background = palette.bgBottom,
            onBackground = palette.text,
            surface = palette.bgTop,
            onSurface = palette.text,
            onSurfaceVariant = palette.textSecondary
        )
    } else {
        lightColorScheme(
            primary = palette.accent,
            onPrimary = palette.onAccent,
            background = palette.bgBottom,
            onBackground = palette.text,
            surface = palette.bgTop,
            onSurface = palette.text,
            onSurfaceVariant = palette.textSecondary
        )
    }
    MaterialTheme(colorScheme = scheme, typography = CbTypography) {
        CompositionLocalProvider(
            LocalSkeu provides palette,
            LocalReduceEffects provides reduceEffects,
            LocalContentColor provides palette.text
        ) {
            content()
        }
    }
}
