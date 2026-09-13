package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalThemeColors = compositionLocalOf {
    getThemeColors(WoolyThemePreset.TWILIGHT, isDark = true)
}

@Composable
fun MyApplicationTheme(
    preset: WoolyThemePreset = WoolyThemePreset.TWILIGHT,
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val themeColors = getThemeColors(preset, isDark = darkTheme)

    val m3ColorScheme = if (darkTheme) {
        darkColorScheme(
            primary = themeColors.primary,
            onPrimary = themeColors.background,
            primaryContainer = themeColors.surfaceElevated,
            onPrimaryContainer = themeColors.primary,
            secondary = themeColors.secondary,
            onSecondary = themeColors.background,
            background = themeColors.background,
            onBackground = themeColors.textPrimary,
            surface = themeColors.surface,
            onSurface = themeColors.textPrimary,
            outline = themeColors.glassBorder
        )
    } else {
        lightColorScheme(
            primary = themeColors.primary,
            onPrimary = themeColors.background,
            primaryContainer = themeColors.surfaceElevated,
            onPrimaryContainer = themeColors.primary,
            secondary = themeColors.secondary,
            onSecondary = themeColors.background,
            background = themeColors.background,
            onBackground = themeColors.textPrimary,
            surface = themeColors.surface,
            onSurface = themeColors.textPrimary,
            outline = themeColors.glassBorder
        )
    }

    CompositionLocalProvider(LocalThemeColors provides themeColors) {
        MaterialTheme(
            colorScheme = m3ColorScheme,
            typography = Typography,
            content = content
        )
    }
}
