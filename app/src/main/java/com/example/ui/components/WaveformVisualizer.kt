package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.WoolyCyan
import com.example.ui.theme.WoolyLavender
import com.example.ui.theme.WoolyPink
import kotlin.math.sin

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 36,
    height: Dp = 48.dp,
    highlightProgress: Float = 0.0f
) {
    val transition = rememberInfiniteTransition(label = "wave_anim")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = (canvasWidth / (barCount * 1.6f)).coerceIn(2.5f, 7.0f)
        val gap = (canvasWidth - (barCount * barWidth)) / (barCount - 1).coerceAtLeast(1)

        val brushActive = Brush.verticalGradient(
            listOf(WoolyPink, WoolyLavender, WoolyCyan)
        )
        val colorInactive = Color(0x33FFFFFF)

        for (i in 0 until barCount) {
            val progressFrac = i.toFloat() / barCount
            val isPassed = progressFrac <= highlightProgress

            val wave = if (isPlaying) {
                val s1 = sin(phase + i * 0.35f)
                val s2 = sin(phase * 1.4f + i * 0.7f)
                (0.35f + 0.35f * s1 + 0.30f * s2).coerceIn(0.12f, 1.0f)
            } else {
                // Static aesthetic mountain curve
                val curve = sin(i.toFloat() / barCount * 3.1415f)
                (curve * 0.65f + 0.2f).coerceIn(0.15f, 0.9f)
            }

            val barHeight = (canvasHeight * wave).coerceAtLeast(6f)
            val x = i * (barWidth + gap)
            val y = (canvasHeight - barHeight) / 2f

            drawRoundRect(
                brush = if (isPassed) brushActive else Brush.verticalGradient(listOf(colorInactive, colorInactive)),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}
