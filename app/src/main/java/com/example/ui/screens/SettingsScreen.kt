package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTabPill
import com.example.ui.theme.LocalThemeColors
import com.example.ui.theme.WoolyThemePreset

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    currentTheme: WoolyThemePreset,
    onSelectTheme: (WoolyThemePreset) -> Unit,
    isCompactMode: Boolean,
    onToggleCompactMode: () -> Unit,
    onRequestPermissions: () -> Unit,
    onScanDevice: () -> Unit,
    onClearCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalThemeColors.current

    BoxWithConstraints(
        modifier = modifier
            .testTag("settings_screen")
            .fillMaxSize()
            .background(theme.backgroundBrush)
            .statusBarsPadding()
    ) {
        val isScreenCompact = isCompactMode || maxHeight < 720.dp
        val horizontalPad = if (isScreenCompact) 16.dp else 20.dp

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = horizontalPad, vertical = 12.dp)
        ) {
            item {
                Text(
                    text = "Settings & Appearance",
                    color = theme.textPrimary,
                    fontSize = if (isScreenCompact) 20.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Section: Appearance & Modern Themes
            item {
                Text(
                    text = "APPEARANCE & THEMES",
                    color = theme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Dark / Light Mode Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(theme.glassBg)
                                        .border(1.dp, theme.glassBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                        contentDescription = null,
                                        tint = theme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = if (isDarkMode) "Dark Mode" else "Light Mode",
                                        color = theme.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (isDarkMode) "Deep cosmic twilight backdrop" else "Bright modern glass aesthetic",
                                        color = theme.textMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { onToggleDarkMode() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF130924),
                                    checkedTrackColor = theme.primary,
                                    uncheckedThumbColor = theme.textSecondary,
                                    uncheckedTrackColor = theme.glassBg
                                )
                            )
                        }

                        // Compact Mode Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(theme.glassBg)
                                        .border(1.dp, theme.glassBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AspectRatio,
                                        contentDescription = null,
                                        tint = theme.secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Compact Display Mode",
                                        color = theme.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Auto-scale disc and layout for small phone screens",
                                        color = theme.textMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Switch(
                                checked = isCompactMode,
                                onCheckedChange = { onToggleCompactMode() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF130924),
                                    checkedTrackColor = theme.secondary,
                                    uncheckedThumbColor = theme.textSecondary,
                                    uncheckedTrackColor = theme.glassBg
                                )
                            )
                        }

                        // Modern Theme Presets Bar
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Color Preset",
                                color = theme.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(WoolyThemePreset.entries.toTypedArray()) { preset ->
                                    GlassTabPill(
                                        text = preset.displayName,
                                        selected = preset == currentTheme,
                                        onClick = { onSelectTheme(preset) }
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Section: Permissions & Device Storage
            item {
                Text(
                    text = "STORAGE & FILES ACCESS",
                    color = theme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Ask Full Access Button Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRequestPermissions() },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(theme.glassBg)
                                        .border(1.dp, theme.glassBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = theme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Full Audio & Files Access",
                                        color = theme.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Grant storage access for device music reading",
                                        color = theme.textMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = theme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Scan Device Audio Library
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onScanDevice() },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(theme.glassBg)
                                        .border(1.dp, theme.glassBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FolderOpen,
                                        contentDescription = null,
                                        tint = theme.secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Scan Device Music Library",
                                        color = theme.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Detect all audio tracks on your phone storage",
                                        color = theme.textMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = theme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Clear cache
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onClearCache() },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(theme.glassBg)
                                        .border(1.dp, theme.glassBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CleaningServices,
                                        contentDescription = null,
                                        tint = theme.textSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Clear Temp Previews",
                                        color = theme.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Free up cache space used by live audio previews",
                                        color = theme.textMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = theme.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Audio Processing Specs
            item {
                Text(
                    text = "AUDIO ENGINE SPECIFICATIONS",
                    color = theme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SettingsInfoRow(
                            icon = Icons.Default.GraphicEq,
                            title = "DSP Architecture",
                            subtitle = "Schroeder Reverb, Biquad IIR, Haas 3D, Vinyl Flutter"
                        )
                        SettingsInfoRow(
                            icon = Icons.Default.Lock,
                            title = "Privacy & Processing",
                            subtitle = "100% Offline • On-Device • Zero telemetry or account"
                        )
                        SettingsInfoRow(
                            icon = Icons.Default.Speed,
                            title = "Lossless Master Output",
                            subtitle = "16-bit 44.1kHz / 48kHz WAV & High-Bitrate AAC"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // About Section & Developer Credit
            item {
                Text(
                    text = "ABOUT & CREDITS",
                    color = theme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    contentPadding = PaddingValues(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(theme.pillGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color(0xFF130924),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Text(
                            text = "WOOLY",
                            color = theme.textPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "Offline Audio Transformation Studio • v1.0",
                            color = theme.textSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Developer Credit (Mandatory requirement)
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(theme.secondary.copy(alpha = 0.2f))
                                .border(1.dp, theme.secondary.copy(alpha = 0.5f), CircleShape)
                                .padding(horizontal = 18.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = theme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Made with love by Rahul Shah",
                                    color = theme.secondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(110.dp))
            }
        }
    }
}

@Composable
private fun SettingsInfoRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    val theme = LocalThemeColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(theme.glassBg)
                .border(1.dp, theme.glassBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = theme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
        Column {
            Text(
                text = title,
                color = theme.textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = theme.textMuted,
                fontSize = 11.sp
            )
        }
    }
}
