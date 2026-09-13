package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioTrackEntity
import com.example.ui.theme.LocalThemeColors
import java.util.Locale

@Composable
fun FullScreenPlayerModal(
    track: AudioTrackEntity,
    isPlaying: Boolean,
    currentPosMs: Long,
    totalDurationMs: Long,
    styleName: String,
    playbackSpeed: Float,
    volume: Float,
    onTogglePlay: () -> Unit,
    onSeek: (Long) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit
) {
    val theme = LocalThemeColors.current
    val progress = if (totalDurationMs > 0) {
        (currentPosMs.toFloat() / totalDurationMs).coerceIn(0f, 1f)
    } else 0f

    val elapsedText = formatMs(currentPosMs)
    val remainingText = "-" + formatMs((totalDurationMs - currentPosMs).coerceAtLeast(0L))

    BoxWithConstraints(
        modifier = Modifier
            .testTag("fullscreen_player_modal")
            .fillMaxSize()
            .background(theme.backgroundBrush)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        val isScreenCompact = maxHeight < 720.dp
        val discSize = if (isScreenCompact) 170.dp else 250.dp

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = if (isScreenCompact) 6.dp else 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(theme.glassBg)
                        .border(1.dp, theme.glassBorder, CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = theme.textPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PLAYING MASTER",
                        color = theme.textMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = styleName,
                        color = theme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(theme.glassBg)
                        .border(1.dp, theme.glassBorder, CircleShape)
                        .clickable { onShare() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = theme.textPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Central Artwork Visual (Orb or Vinyl Record - 100% Circular)
            Box(
                modifier = Modifier
                    .size(discSize)
                    .shadow(28.dp, CircleShape, spotColor = theme.primary.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                TrackArtwork(
                    title = track.title,
                    styleName = styleName,
                    size = discSize,
                    isPlaying = isPlaying
                )
            }

            // Track Title & Artist
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = track.title,
                    color = theme.textPrimary,
                    fontSize = if (isScreenCompact) 18.sp else 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = track.artist,
                    color = theme.textSecondary,
                    fontSize = if (isScreenCompact) 13.sp else 14.sp
                )
            }

            // Waveform Visualizer
            WaveformVisualizer(
                isPlaying = isPlaying,
                height = if (isScreenCompact) 32.dp else 42.dp,
                highlightProgress = progress,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            // Progress Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = progress,
                    onValueChange = { frac ->
                        onSeek((frac * totalDurationMs).toLong())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = theme.secondary,
                        activeTrackColor = theme.primary,
                        inactiveTrackColor = theme.glassBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = elapsedText, color = theme.textMuted, fontSize = 11.sp)
                    Text(text = remainingText, color = theme.textMuted, fontSize = 11.sp)
                }
            }

            // Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Playback Speed Toggle
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(theme.glassBg)
                        .border(1.dp, theme.glassBorder, CircleShape)
                        .clickable {
                            val nextSpeed = when {
                                playbackSpeed <= 0.86f -> 1.0f
                                playbackSpeed <= 1.05f -> 1.25f
                                else -> 0.85f
                            }
                            onSpeedChange(nextSpeed)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${String.format(Locale.US, "%.2f", playbackSpeed)}x",
                        color = theme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable { onSeek((currentPosMs - 10000L).coerceAtLeast(0L)) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Rewind 10s",
                        tint = theme.textPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Main Play/Pause Button (Circular Pill)
                Box(
                    modifier = Modifier
                        .size(if (isScreenCompact) 60.dp else 68.dp)
                        .shadow(16.dp, CircleShape, spotColor = theme.secondary.copy(alpha = 0.5f))
                        .clip(CircleShape)
                        .background(theme.pillGradient)
                        .clickable(onClick = onTogglePlay),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color(0xFF130924),
                        modifier = Modifier.size(if (isScreenCompact) 32.dp else 36.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable { onSeek((currentPosMs + 10000L).coerceAtMost(totalDurationMs)) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Forward 10s",
                        tint = theme.textPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Output Format Badge
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(theme.glassBg)
                        .border(1.dp, theme.glassBorder, CircleShape)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = track.format,
                        color = theme.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Volume Control
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = if (isScreenCompact) 4.dp else 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeDown,
                    contentDescription = "Volume Down",
                    tint = theme.textMuted,
                    modifier = Modifier.size(16.dp)
                )
                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = theme.primary,
                        activeTrackColor = theme.primary,
                        inactiveTrackColor = theme.glassBorder
                    )
                )
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Volume Up",
                    tint = theme.textMuted,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}
