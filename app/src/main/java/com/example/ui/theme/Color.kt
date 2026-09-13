package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class WoolyThemePreset(val displayName: String, val description: String) {
    TWILIGHT("Wooly Twilight", "Dreamy lavender, soft pink & deep violet"),
    OBSIDIAN("Midnight Obsidian", "Pure pitch black, neon cyan & emerald"),
    SUNSET("Sunset Amber", "Warm peach, coral rose & golden dusk"),
    CYBER("Cyber Neon", "Electric blue, hot fuchsia & hyper violet"),
    AURORA("Nordic Aurora", "Deep boreal teal, seafoam & ice mint")
}

// Wooly Twilight Palette (Default)
val WoolyBackgroundDark = Color(0xFF090614)
val WoolyBackgroundGradientEnd = Color(0xFF130D23)
val WoolySurfaceDark = Color(0xFF16102B)
val WoolySurfaceElevated = Color(0xFF20173D)

val WoolyGlassBg = Color(0x22FFFFFF)
val WoolyGlassBgHover = Color(0x33FFFFFF)
val WoolyGlassBorder = Color(0x2EFFFFFF)
val WoolyGlassBorderHighlight = Color(0x60FFFFFF)

val WoolyLavender = Color(0xFFD8B4FE)
val WoolyPink = Color(0xFFF472B6)
val WoolyPurple = Color(0xFFA855F7)
val WoolyPeach = Color(0xFFFDA4AF)
val WoolyCyan = Color(0xFF38BDF8)
val WoolyVioletDark = Color(0xFF2E1065)

val WoolyTextPrimary = Color(0xFFF8FAFC)
val WoolyTextSecondary = Color(0xFFCBD5E1)
val WoolyTextTertiary = Color(0xFF94A3B8)
val WoolyTextMuted = Color(0xFF64748B)

val WoolySuccess = Color(0xFF34D399)
val WoolyError = Color(0xFFF87171)

// Gradients
val WoolyPillGradient = Brush.horizontalGradient(
    listOf(Color(0xFFC084FC), Color(0xFFF472B6))
)

val WoolyFeaturedGradient = Brush.linearGradient(
    listOf(Color(0xFF2D1B4E), Color(0xFF1E1435), Color(0xFF130E26))
)

val WoolyOrbGradient = Brush.radialGradient(
    listOf(Color(0xFFFFD1DC), Color(0xFFC4B5FD), Color(0xFF4C1D95))
)

val WoolyBackgroundBrush = Brush.verticalGradient(
    listOf(
        Color(0xFF090614),
        Color(0xFF120B24),
        Color(0xFF180E2E),
        Color(0xFF0E081A)
    )
)

data class ThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val glassBg: Color,
    val glassBorder: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val pillGradient: Brush,
    val backgroundBrush: Brush
)

fun getThemeColors(preset: WoolyThemePreset, isDark: Boolean): ThemeColors {
    return if (isDark) {
        when (preset) {
            WoolyThemePreset.TWILIGHT -> ThemeColors(
                background = Color(0xFF090614),
                surface = Color(0xFF16102B),
                surfaceElevated = Color(0xFF20173D),
                glassBg = Color(0x22FFFFFF),
                glassBorder = Color(0x2EFFFFFF),
                primary = Color(0xFFD8B4FE),
                secondary = Color(0xFFF472B6),
                tertiary = Color(0xFF38BDF8),
                textPrimary = Color(0xFFF8FAFC),
                textSecondary = Color(0xFFCBD5E1),
                textMuted = Color(0xFF94A3B8),
                pillGradient = Brush.horizontalGradient(listOf(Color(0xFFC084FC), Color(0xFFF472B6))),
                backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF090614), Color(0xFF140D27), Color(0xFF180E2E)))
            )
            WoolyThemePreset.OBSIDIAN -> ThemeColors(
                background = Color(0xFF050507),
                surface = Color(0xFF111116),
                surfaceElevated = Color(0xFF1A1A22),
                glassBg = Color(0x18FFFFFF),
                glassBorder = Color(0x24FFFFFF),
                primary = Color(0xFF2DD4BF),
                secondary = Color(0xFF38BDF8),
                tertiary = Color(0xFFA7F3D0),
                textPrimary = Color(0xFFF9FAFB),
                textSecondary = Color(0xFFD1D5DB),
                textMuted = Color(0xFF6B7280),
                pillGradient = Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF06B6D4))),
                backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF050507), Color(0xFF0A0F14), Color(0xFF05080C)))
            )
            WoolyThemePreset.SUNSET -> ThemeColors(
                background = Color(0xFF110807),
                surface = Color(0xFF1E100E),
                surfaceElevated = Color(0xFF2B1614),
                glassBg = Color(0x22FFFFFF),
                glassBorder = Color(0x30FFFFFF),
                primary = Color(0xFFFDBA74),
                secondary = Color(0xFFFB7185),
                tertiary = Color(0xFFFDE047),
                textPrimary = Color(0xFFFFF7ED),
                textSecondary = Color(0xFFFED7AA),
                textMuted = Color(0xFF9A7B72),
                pillGradient = Brush.horizontalGradient(listOf(Color(0xFFFB923C), Color(0xFFF43F5E))),
                backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF110807), Color(0xFF1F0D0C), Color(0xFF27100F)))
            )
            WoolyThemePreset.CYBER -> ThemeColors(
                background = Color(0xFF06031A),
                surface = Color(0xFF110B30),
                surfaceElevated = Color(0xFF1A1045),
                glassBg = Color(0x28FFFFFF),
                glassBorder = Color(0x3DFFFFFF),
                primary = Color(0xFFE879F9),
                secondary = Color(0xFF60A5FA),
                tertiary = Color(0xFFF43F5E),
                textPrimary = Color(0xFFFAF5FF),
                textSecondary = Color(0xFFE9D5FF),
                textMuted = Color(0xFF9373B2),
                pillGradient = Brush.horizontalGradient(listOf(Color(0xFFEC4899), Color(0xFF8B5CF6))),
                backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF06031A), Color(0xFF0F0730), Color(0xFF160940)))
            )
            WoolyThemePreset.AURORA -> ThemeColors(
                background = Color(0xFF030D14),
                surface = Color(0xFF091C28),
                surfaceElevated = Color(0xFF0F2736),
                glassBg = Color(0x20FFFFFF),
                glassBorder = Color(0x2EFFFFFF),
                primary = Color(0xFF5EEAD4),
                secondary = Color(0xFF38BDF8),
                tertiary = Color(0xFFA7F3D0),
                textPrimary = Color(0xFFF0FDFA),
                textSecondary = Color(0xFFCCFBF1),
                textMuted = Color(0xFF648F89),
                pillGradient = Brush.horizontalGradient(listOf(Color(0xFF14B8A6), Color(0xFF0284C7))),
                backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF030D14), Color(0xFF061822), Color(0xFF0A2230)))
            )
        }
    } else {
        // Crisp Modern Light Glassmorphism Mode
        when (preset) {
            WoolyThemePreset.TWILIGHT -> ThemeColors(
                background = Color(0xFFF5F3FF),
                surface = Color(0xFFEDE9FE),
                surfaceElevated = Color(0xFFE0E7FF),
                glassBg = Color(0x66FFFFFF),
                glassBorder = Color(0x55A78BFA),
                primary = Color(0xFF7C3AED),
                secondary = Color(0xFFDB2777),
                tertiary = Color(0xFF0284C7),
                textPrimary = Color(0xFF1E1B4B),
                textSecondary = Color(0xFF4338CA),
                textMuted = Color(0xFF6B7280),
                pillGradient = Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))),
                backgroundBrush = Brush.verticalGradient(listOf(Color(0xFFF5F3FF), Color(0xFFEDE9FE), Color(0xFFF3E8FF)))
            )
            WoolyThemePreset.OBSIDIAN -> ThemeColors(
                background = Color(0xFFF8FAFC),
                surface = Color(0xFFF1F5F9),
                surfaceElevated = Color(0xFFE2E8F0),
                glassBg = Color(0x77FFFFFF),
                glassBorder = Color(0x4494A3B8),
                primary = Color(0xFF0F766E),
                secondary = Color(0xFF0369A1),
                tertiary = Color(0xFF047857),
                textPrimary = Color(0xFF0F172A),
                textSecondary = Color(0xFF334155),
                textMuted = Color(0xFF64748B),
                pillGradient = Brush.horizontalGradient(listOf(Color(0xFF0D9488), Color(0xFF0284C7))),
                backgroundBrush = Brush.verticalGradient(listOf(Color(0xFFF8FAFC), Color(0xFFF1F5F9), Color(0xFFE2E8F0)))
            )
            WoolyThemePreset.SUNSET -> ThemeColors(
                background = Color(0xFFFFF7ED),
                surface = Color(0xFFFEEDDB),
                surfaceElevated = Color(0xFFFED7AA),
                glassBg = Color(0x70FFFFFF),
                glassBorder = Color(0x44FB923C),
                primary = Color(0xFFC2410C),
                secondary = Color(0xFFBE123C),
                tertiary = Color(0xFFB45309),
                textPrimary = Color(0xFF431407),
                textSecondary = Color(0xFF7C2D12),
                textMuted = Color(0xFF9A3412),
                pillGradient = Brush.horizontalGradient(listOf(Color(0xFFEA580C), Color(0xFFE11D48))),
                backgroundBrush = Brush.verticalGradient(listOf(Color(0xFFFFF7ED), Color(0xFFFFEDD5), Color(0xFFFEE2E2)))
            )
            WoolyThemePreset.CYBER -> ThemeColors(
                background = Color(0xFFFAF5FF),
                surface = Color(0xFFF3E8FF),
                surfaceElevated = Color(0xFFE9D5FF),
                glassBg = Color(0x66FFFFFF),
                glassBorder = Color(0x55C084FC),
                primary = Color(0xFF9333EA),
                secondary = Color(0xFF2563EB),
                tertiary = Color(0xFFBE185D),
                textPrimary = Color(0xFF3B0764),
                textSecondary = Color(0xFF581C87),
                textMuted = Color(0xFF7E22CE),
                pillGradient = Brush.horizontalGradient(listOf(Color(0xFFD946EF), Color(0xFF6366F1))),
                backgroundBrush = Brush.verticalGradient(listOf(Color(0xFFFAF5FF), Color(0xFFF3E8FF), Color(0xFFEDE9FE)))
            )
            WoolyThemePreset.AURORA -> ThemeColors(
                background = Color(0xFFF0FDFA),
                surface = Color(0xFFCCFBF1),
                surfaceElevated = Color(0xFF99F6E4),
                glassBg = Color(0x70FFFFFF),
                glassBorder = Color(0x442DD4BF),
                primary = Color(0xFF0F766E),
                secondary = Color(0xFF0284C7),
                tertiary = Color(0xFF059669),
                textPrimary = Color(0xFF134E4A),
                textSecondary = Color(0xFF115E59),
                textMuted = Color(0xFF0D9488),
                pillGradient = Brush.horizontalGradient(listOf(Color(0xFF0D9488), Color(0xFF0284C7))),
                backgroundBrush = Brush.verticalGradient(listOf(Color(0xFFF0FDFA), Color(0xFFE6FFFA), Color(0xFFCCFBF1)))
            )
        }
    }
}
