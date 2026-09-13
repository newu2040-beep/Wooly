package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AudioStyle
import com.example.data.model.AudioTrackEntity
import com.example.data.model.CustomAudioParams
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPillButton
import com.example.ui.components.GlassSlider
import com.example.ui.components.GlassTabPill
import com.example.ui.theme.LocalThemeColors
import java.util.Locale

@Composable
fun ConverterScreen(
    track: AudioTrackEntity?,
    selectedStyle: AudioStyle,
    customParams: CustomAudioParams,
    isProcessing: Boolean,
    processingProgress: Float,
    processingStatus: String,
    onBack: () -> Unit,
    onStyleSelected: (AudioStyle) -> Unit,
    onParamsChanged: (CustomAudioParams) -> Unit,
    onResetParams: () -> Unit,
    onPreview: () -> Unit,
    onExport: () -> Unit,
    onSelectTrack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalThemeColors.current
    var showAdvancedSettings by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .testTag("converter_screen")
            .fillMaxSize()
            .background(theme.backgroundBrush)
            .statusBarsPadding()
    ) {
        val isScreenCompact = maxHeight < 720.dp
        val orbSize = if (isScreenCompact) 130.dp else 210.dp
        val horizontalPad = if (isScreenCompact) 16.dp else 20.dp

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 130.dp)
        ) {
            // Top App Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad, vertical = if (isScreenCompact) 4.dp else 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(theme.glassBg)
                            .border(1.dp, theme.glassBorder, CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = theme.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "Vibe Studio",
                        color = theme.textPrimary,
                        fontSize = if (isScreenCompact) 17.sp else 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (showAdvancedSettings) theme.primary.copy(alpha = 0.25f) else theme.glassBg)
                            .border(1.dp, theme.glassBorder, CircleShape)
                            .clickable { showAdvancedSettings = !showAdvancedSettings },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Settings",
                            tint = if (showAdvancedSettings) theme.primary else theme.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Selected Audio Track Summary Pill (Circular pill contour)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad, vertical = 4.dp)
                        .clip(CircleShape)
                        .background(theme.glassBg)
                        .border(1.dp, theme.glassBorder, CircleShape)
                        .clickable { onSelectTrack() }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track?.title ?: "Select Audio Track",
                                color = theme.textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val durationSec = (track?.durationMs ?: 0L) / 1000
                            val durText = "${durationSec / 60}:${String.format(Locale.US, "%02d", durationSec % 60)}"
                            Text(
                                text = "${track?.artist ?: "Tap to select"} • $durText • ${track?.format ?: "High Quality"}",
                                color = theme.textMuted,
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(theme.primary.copy(alpha = 0.2f))
                                .border(1.dp, theme.primary.copy(alpha = 0.4f), CircleShape)
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Change",
                                color = theme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Central Glowing Sphere / Vinyl Disc Artwork (100% Circular)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = if (isScreenCompact) 8.dp else 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(orbSize)
                            .shadow(28.dp, CircleShape, spotColor = theme.primary.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val isVinyl = selectedStyle == AudioStyle.VINYL
                        if (isVinyl) {
                            Image(
                                painter = painterResource(id = R.drawable.wooly_vinyl_disc),
                                contentDescription = "Vinyl Record",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.sweepGradient(
                                            listOf(theme.primary, theme.secondary, theme.tertiary, theme.primary)
                                        ),
                                        shape = CircleShape
                                    ),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.wooly_glass_orb),
                                contentDescription = "Glass Orb Visual",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.sweepGradient(
                                            listOf(theme.primary, theme.secondary, theme.tertiary, theme.primary)
                                        ),
                                        shape = CircleShape
                                    ),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Style Title & Aesthetic Subtitle
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = selectedStyle.displayName,
                        color = theme.textPrimary,
                        fontSize = if (isScreenCompact) 20.sp else 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = selectedStyle.subtitle,
                        color = theme.primary,
                        fontSize = if (isScreenCompact) 12.sp else 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(if (isScreenCompact) 10.dp else 16.dp))
            }

            // Preset Style Pills Horizontal Bar (Smooth Oval / Circular pills)
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = horizontalPad),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(AudioStyle.entries.toTypedArray()) { style ->
                        GlassTabPill(
                            text = style.displayName,
                            selected = selectedStyle == style,
                            onClick = { onStyleSelected(style) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(if (isScreenCompact) 10.dp else 14.dp))
            }

            // Processing Progress Banner (when active)
            if (isProcessing) {
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPad, vertical = 6.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = processingStatus.ifBlank { "Processing DSP audio..." },
                                    color = theme.textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${(processingProgress * 100).toInt()}%",
                                    color = theme.primary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            LinearProgressIndicator(
                                progress = { processingProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = theme.primary,
                                trackColor = theme.glassBorder
                            )
                        }
                    }
                }
            }

            // Advanced Settings Accordion (Curved pill-glass container)
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad, vertical = 4.dp),
                    shape = RoundedCornerShape(30.dp),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAdvancedSettings = !showAdvancedSettings },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Equalizer,
                                    contentDescription = null,
                                    tint = theme.primary,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .padding(end = 8.dp)
                                )
                                Text(
                                    text = "Fine-Tune DSP Parameters",
                                    color = theme.textPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (showAdvancedSettings) {
                                    IconButton(
                                        onClick = onResetParams,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Reset",
                                            tint = theme.textMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = if (showAdvancedSettings) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expand",
                                    tint = theme.textSecondary
                                )
                            }
                        }

                        AnimatedVisibility(visible = showAdvancedSettings) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            ) {
                                // Reverb Wet
                                GlassSlider(
                                    value = customParams.reverbWet,
                                    onValueChange = { onParamsChanged(customParams.copy(reverbWet = it)) },
                                    valueRange = 0.0f..1.0f,
                                    label = "Reverb",
                                    valueText = "${(customParams.reverbWet * 100).toInt()}%",
                                    icon = Icons.Default.SurroundSound
                                )

                                // Bass Gain
                                GlassSlider(
                                    value = customParams.bassGainDb,
                                    onValueChange = { onParamsChanged(customParams.copy(bassGainDb = it)) },
                                    valueRange = -6.0f..16.0f,
                                    label = "Bass",
                                    valueText = "${customParams.bassGainDb.toInt()} dB",
                                    icon = Icons.Default.GraphicEq
                                )

                                // Treble Gain
                                GlassSlider(
                                    value = customParams.trebleGainDb,
                                    onValueChange = { onParamsChanged(customParams.copy(trebleGainDb = it)) },
                                    valueRange = -16.0f..10.0f,
                                    label = "Treble",
                                    valueText = "${customParams.trebleGainDb.toInt()} dB",
                                    icon = Icons.Default.GraphicEq
                                )

                                // Speed / Resample
                                GlassSlider(
                                    value = customParams.speed,
                                    onValueChange = { onParamsChanged(customParams.copy(speed = it)) },
                                    valueRange = 0.5f..1.5f,
                                    label = "Speed",
                                    valueText = "${String.format(Locale.US, "%.2f", customParams.speed)}x",
                                    icon = Icons.Default.Speed
                                )

                                // LowPass Cutoff
                                GlassSlider(
                                    value = customParams.lowPassHz,
                                    onValueChange = { onParamsChanged(customParams.copy(lowPassHz = it)) },
                                    valueRange = 1000.0f..20000.0f,
                                    label = "Cutoff",
                                    valueText = "${customParams.lowPassHz.toInt()} Hz",
                                    icon = Icons.Default.Tune
                                )

                                // Vinyl Crackle
                                GlassSlider(
                                    value = customParams.vinylCrackle,
                                    onValueChange = { onParamsChanged(customParams.copy(vinylCrackle = it)) },
                                    valueRange = 0.0f..1.0f,
                                    label = "Vinyl",
                                    valueText = "${(customParams.vinylCrackle * 100).toInt()}%",
                                    icon = Icons.Default.GraphicEq
                                )

                                // 3D Surround Width
                                GlassSlider(
                                    value = customParams.spatial3D,
                                    onValueChange = { onParamsChanged(customParams.copy(spatial3D = it)) },
                                    valueRange = 0.0f..1.0f,
                                    label = "3D Width",
                                    valueText = "${(customParams.spatial3D * 100).toInt()}%",
                                    icon = Icons.Default.SurroundSound
                                )

                                // Warmth Saturation
                                GlassSlider(
                                    value = customParams.warmthSaturation,
                                    onValueChange = { onParamsChanged(customParams.copy(warmthSaturation = it)) },
                                    valueRange = 0.0f..1.0f,
                                    label = "Warmth",
                                    valueText = "${(customParams.warmthSaturation * 100).toInt()}%",
                                    icon = Icons.Default.VolumeUp
                                )
                            }
                        }
                    }
                }
            }

            // Action Buttons: Preview & Export (Pill shaped, no rectangles)
            item {
                Spacer(modifier = Modifier.height(if (isScreenCompact) 8.dp else 14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Preview Button (Listen preview)
                    GlassPillButton(
                        text = "Preview",
                        icon = Icons.Default.PlayArrow,
                        isGradient = false,
                        onClick = onPreview,
                        enabled = !isProcessing && track != null,
                        modifier = Modifier.weight(1f),
                        testTag = "preview_button"
                    )

                    // Convert & High Quality Export Button
                    GlassPillButton(
                        text = if (isProcessing) "Exporting..." else "Save / Export",
                        icon = Icons.Default.Download,
                        isGradient = true,
                        onClick = onExport,
                        enabled = !isProcessing && track != null,
                        modifier = Modifier.weight(1.3f),
                        testTag = "export_button"
                    )
                }
            }
        }
    }
}
