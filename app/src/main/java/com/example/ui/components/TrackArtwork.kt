package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.WoolyLavender
import com.example.ui.theme.WoolyPink
import kotlin.math.abs

@Composable
fun TrackArtwork(
    title: String,
    styleName: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    isPlaying: Boolean = false,
    isVinylStyle: Boolean = styleName.equals("Vinyl", ignoreCase = true)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "disc_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    val currentRotation = if (isPlaying && isVinylStyle) rotation else 0f

    // Pick visual based on style
    val useVinylDrawable = isVinylStyle || title.contains("Vinyl", ignoreCase = true)
    val useGlassOrb = styleName.equals("Lo-fi", ignoreCase = true) ||
            styleName.equals("Reverb", ignoreCase = true) ||
            styleName.equals("Slowed + Reverb", ignoreCase = true) ||
            styleName.equals("Ambient", ignoreCase = true)

    // Clean circular shape for all artwork, eliminating boxy rectangle shapes
    val shape = CircleShape

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .border(
                width = 1.5.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        Color(0x88C084FC),
                        Color(0x88F472B6),
                        Color(0x8838BDF8),
                        Color(0x88C084FC)
                    )
                ),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (useVinylDrawable) {
            Image(
                painter = painterResource(id = R.drawable.wooly_vinyl_disc),
                contentDescription = "Vinyl Record",
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(currentRotation),
                contentScale = ContentScale.Crop
            )
        } else if (useGlassOrb) {
            Image(
                painter = painterResource(id = R.drawable.wooly_glass_orb),
                contentDescription = "Glass Orb Visual",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Procedural gradient art based on title hash
            val hash = abs(title.hashCode())
            val colors = when (hash % 4) {
                0 -> listOf(Color(0xFF6366F1), Color(0xFFA855F7), Color(0xFFEC4899))
                1 -> listOf(Color(0xFF3B82F6), Color(0xFF06B6D4), Color(0xFF10B981))
                2 -> listOf(Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFF8B5CF6))
                else -> listOf(Color(0xFF8B5CF6), Color(0xFFD946EF), Color(0xFF06B6D4))
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.radialGradient(colors)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title.take(2).uppercase(),
                    color = Color.White,
                    fontSize = (size.value * 0.35f).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
