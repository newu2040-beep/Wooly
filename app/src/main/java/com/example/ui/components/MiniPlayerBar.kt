package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioTrackEntity
import com.example.ui.theme.WoolyGlassBorder
import com.example.ui.theme.WoolyLavender
import com.example.ui.theme.WoolyPillGradient
import com.example.ui.theme.WoolyPink
import com.example.ui.theme.WoolyTextMuted
import com.example.ui.theme.WoolyTextPrimary

@Composable
fun MiniPlayerBar(
    track: AudioTrackEntity,
    isPlaying: Boolean,
    currentPosMs: Long,
    totalDurationMs: Long,
    styleName: String,
    onTogglePlay: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (totalDurationMs > 0) {
        (currentPosMs.toFloat() / totalDurationMs).coerceIn(0f, 1f)
    } else 0f

    val shape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .testTag("mini_player_bar")
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(16.dp, shape, ambientColor = Color(0x66000000), spotColor = Color(0x88A855F7))
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xE61F1735), Color(0xEE160F26))
                )
            )
            .border(1.dp, WoolyGlassBorder, shape)
            .clickable(onClick = onClick)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    TrackArtwork(
                        title = track.title,
                        styleName = styleName,
                        size = 46.dp,
                        isPlaying = isPlaying
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp, end = 8.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = track.title,
                            color = WoolyTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = styleName,
                                color = WoolyLavender,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = " • ${track.artist}",
                                color = WoolyTextMuted,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Play / Pause round action button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(WoolyPillGradient)
                        .clickable(onClick = onTogglePlay),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color(0xFF130924),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Slim progress bar along bottom of mini player
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = WoolyPink,
                trackColor = Color(0x22FFFFFF)
            )
        }
    }
}
