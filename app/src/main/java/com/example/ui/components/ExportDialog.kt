package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.engine.ExportEstimate
import com.example.ui.theme.LocalThemeColors
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportDialog(
    initialFilename: String,
    estimate: ExportEstimate?,
    onDismiss: () -> Unit,
    onExport: (filename: String, format: String, bitrate: Int) -> Unit
) {
    val theme = LocalThemeColors.current
    var filename by remember { mutableStateOf(initialFilename) }
    var selectedFormat by remember { mutableStateOf(estimate?.format ?: "WAV") }
    var selectedBitrate by remember { mutableStateOf(estimate?.bitrateKbps ?: 320) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        val shape = RoundedCornerShape(32.dp)
        Box(
            modifier = Modifier
                .testTag("export_dialog")
                .fillMaxWidth()
                .clip(shape)
                .background(theme.surfaceElevated)
                .border(1.dp, theme.glassBorder, shape)
                .padding(22.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "High-Quality Master Export",
                        color = theme.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(theme.glassBg)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = theme.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom Output Filename
                Text(
                    text = "CUSTOM FILENAME",
                    color = theme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = filename,
                    onValueChange = { filename = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = theme.textPrimary,
                        unfocusedTextColor = theme.textPrimary,
                        focusedBorderColor = theme.primary,
                        unfocusedBorderColor = theme.glassBorder,
                        focusedContainerColor = theme.glassBg,
                        unfocusedContainerColor = theme.glassBg
                    ),
                    shape = CircleShape,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Format Selection
                Text(
                    text = "MASTER FORMAT",
                    color = theme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("WAV", "M4A", "FLAC").forEach { fmt ->
                        val isSel = selectedFormat == fmt
                        val fmtBrush = if (isSel) theme.pillGradient else Brush.horizontalGradient(listOf(theme.glassBg, theme.glassBg))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(CircleShape)
                                .background(fmtBrush)
                                .border(1.dp, if (isSel) Color.White else theme.glassBorder, CircleShape)
                                .clickable { selectedFormat = fmt },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = fmt,
                                color = if (isSel) Color(0xFF130924) else theme.textSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bitrate Selection
                Text(
                    text = "BITRATE QUALITY",
                    color = theme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(128, 192, 256, 320).forEach { br ->
                        val isSel = selectedBitrate == br
                        val brBrush = if (isSel) theme.pillGradient else Brush.horizontalGradient(listOf(theme.glassBg, theme.glassBg))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(CircleShape)
                                .background(brBrush)
                                .border(1.dp, if (isSel) Color.White else theme.glassBorder, CircleShape)
                                .clickable { selectedBitrate = br },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${br}k",
                                color = if (isSel) Color(0xFF130924) else theme.textSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Estimated Output Card
                if (estimate != null) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "ESTIMATED SPECIFICATIONS",
                                color = theme.primary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Format / Sample Rate", color = theme.textMuted, fontSize = 12.sp)
                                Text("$selectedFormat • ${estimate.sampleRate} Hz", color = theme.textPrimary, fontSize = 12.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Estimated Duration", color = theme.textMuted, fontSize = 12.sp)
                                val sec = estimate.estimatedDurationMs / 1000
                                Text("${sec / 60}:${String.format(Locale.US, "%02d", sec % 60)}", color = theme.textPrimary, fontSize = 12.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Estimated File Size", color = theme.textMuted, fontSize = 12.sp)
                                val mb = estimate.estimatedSizeBytes / (1024.0 * 1024.0)
                                Text(String.format(Locale.US, "%.1f MB", mb), color = theme.secondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = theme.textSecondary)
                    }

                    GlassPillButton(
                        text = "Save & Export",
                        icon = Icons.Default.Download,
                        onClick = {
                            onExport(filename, selectedFormat, selectedBitrate)
                        },
                        modifier = Modifier.weight(1.5f),
                        testTag = "confirm_export_button"
                    )
                }
            }
        }
    }
}
