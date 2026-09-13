package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalThemeColors

/**
 * Standard Glassmorphic container with translucent background, subtle border, and soft blur depth.
 * Uses smooth high-radius pill/circular contours (32.dp) with no boxy rectangle shapes.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(30.dp),
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val theme = LocalThemeColors.current
    val bg = backgroundColor ?: theme.glassBg
    val border = borderColor ?: theme.glassBorder

    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = Color(0x33000000),
                spotColor = theme.primary.copy(alpha = 0.3f)
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        bg.copy(alpha = 0.28f),
                        bg.copy(alpha = 0.10f)
                    )
                )
            )
            .border(width = borderWidth, color = border, shape = shape)
            .padding(contentPadding),
        content = content
    )
}

/**
 * Rounded pill-shaped button with pastel gradient or glass surface.
 */
@Composable
fun GlassPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = Icons.AutoMirrored.Filled.ArrowForward,
    isGradient: Boolean = true,
    enabled: Boolean = true,
    testTag: String = "glass_pill_button"
) {
    val theme = LocalThemeColors.current
    val shape = CircleShape
    val brush = if (isGradient) {
        theme.pillGradient
    } else {
        Brush.horizontalGradient(listOf(theme.glassBg, theme.glassBg))
    }

    Box(
        modifier = modifier
            .testTag(testTag)
            .height(52.dp)
            .clip(shape)
            .background(if (enabled) brush else Brush.horizontalGradient(listOf(Color(0x33FFFFFF), Color(0x33FFFFFF))))
            .border(
                width = 1.dp,
                color = if (isGradient) Color(0x77FFFFFF) else theme.glassBorder,
                shape = shape
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = if (isGradient) Color(0xFF0F081D) else theme.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isGradient) Color(0xFF0F081D) else theme.textPrimary,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(18.dp)
                )
            }
        }
    }
}

/**
 * Pill-shaped style chip/tab selector with smooth oval shape.
 */
@Composable
fun GlassTabPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    testTag: String = "tab_pill"
) {
    val theme = LocalThemeColors.current
    val shape = CircleShape
    Box(
        modifier = modifier
            .testTag(testTag)
            .height(40.dp)
            .clip(shape)
            .background(
                if (selected) {
                    theme.pillGradient
                } else {
                    Brush.verticalGradient(listOf(Color(0x18FFFFFF), Color(0x0AFFFFFF)))
                }
            )
            .border(
                width = 1.dp,
                color = if (selected) Color(0x99FFFFFF) else theme.glassBorder,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) Color(0xFF130924) else theme.textSecondary,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 6.dp)
                )
            }
            Text(
                text = text,
                color = if (selected) Color(0xFF130924) else theme.textSecondary,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

/**
 * Slender glass slider for advanced audio settings.
 */
@Composable
fun GlassSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    label: String,
    valueText: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val theme = LocalThemeColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = theme.primary,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = label,
            color = theme.textSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.size(width = 68.dp, height = 20.dp)
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = theme.primary,
                activeTrackColor = theme.primary,
                inactiveTrackColor = theme.glassBorder
            )
        )

        Text(
            text = valueText,
            color = theme.textPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.size(width = 44.dp, height = 20.dp)
        )
    }
}
