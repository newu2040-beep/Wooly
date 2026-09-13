package com.example.ui.screens

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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AudioStyle
import com.example.data.model.AudioTrackEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.TrackArtwork
import com.example.ui.theme.LocalThemeColors
import com.example.ui.theme.WoolyThemePreset

@Composable
fun HomeScreen(
    recentTracks: List<AudioTrackEntity>,
    allTracks: List<AudioTrackEntity>,
    currentPlayingTrack: AudioTrackEntity?,
    isPlaying: Boolean,
    isDarkMode: Boolean,
    currentThemePreset: WoolyThemePreset,
    isCompactMode: Boolean,
    onTrackSelected: (AudioTrackEntity) -> Unit,
    onStyleSelected: (AudioStyle) -> Unit,
    onImportClicked: () -> Unit,
    onPlayTrack: (AudioTrackEntity) -> Unit,
    onTogglePlay: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onSelectThemePreset: (WoolyThemePreset) -> Unit,
    onToggleCompactMode: () -> Unit,
    onRequestPermissions: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalThemeColors.current
    var searchQuery by remember { mutableStateOf("") }
    var showThemeDialog by remember { mutableStateOf(false) }

    val filteredRecent = remember(recentTracks, searchQuery) {
        if (searchQuery.isBlank()) recentTracks
        else recentTracks.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.artist.contains(searchQuery, ignoreCase = true) ||
            it.styleName.contains(searchQuery, ignoreCase = true)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .testTag("home_screen")
            .fillMaxSize()
            .background(theme.backgroundBrush)
            .statusBarsPadding()
    ) {
        val isScreenCompact = isCompactMode || maxHeight < 720.dp
        val featuredCardHeight = if (isScreenCompact) 150.dp else 190.dp
        val vinylSize = if (isScreenCompact) 100.dp else 130.dp
        val horizontalPad = if (isScreenCompact) 16.dp else 20.dp

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header with App Title, Dark/Light Toggle, Theme Palette, Settings
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad, vertical = if (isScreenCompact) 8.dp else 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "WOOLY",
                            color = theme.textPrimary,
                            fontSize = if (isScreenCompact) 22.sp else 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = currentThemePreset.displayName,
                            color = theme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dark / Light Mode Toggle Button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(theme.glassBg)
                                .border(1.dp, theme.glassBorder, CircleShape)
                                .clickable { onToggleDarkMode() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Dark/Light Mode",
                                tint = theme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Theme Preset Palette Switcher
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(theme.glassBg)
                                .border(1.dp, theme.glassBorder, CircleShape)
                                .clickable { showThemeDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Select Modern Theme",
                                tint = theme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Settings Icon Button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(theme.glassBg)
                                .border(1.dp, theme.glassBorder, CircleShape)
                                .clickable { onOpenSettings() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = theme.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Search Bar (Pill shaped, no rectangle)
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search songs, artists or vibes...",
                            color = theme.textMuted,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = theme.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    },
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad, vertical = 4.dp)
                )
            }

            // Featured Showcase Card: Vinyl Vibes (Curved pill contours, no rectangular frames)
            item {
                val cardShape = RoundedCornerShape(32.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad, vertical = if (isScreenCompact) 8.dp else 12.dp)
                        .shadow(16.dp, cardShape, ambientColor = Color(0x33000000), spotColor = theme.primary.copy(alpha = 0.4f))
                        .clip(cardShape)
                        .background(theme.surfaceElevated)
                        .border(1.dp, theme.glassBorder, cardShape)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(if (isScreenCompact) 16.dp else 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1.2f)) {
                            // Featured Pill Badge
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(theme.primary.copy(alpha = 0.2f))
                                    .border(1.dp, theme.primary.copy(alpha = 0.5f), CircleShape)
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "FEATURED VIBE",
                                    color = theme.primary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Vinyl Warmth",
                                color = theme.textPrimary,
                                fontSize = if (isScreenCompact) 18.sp else 22.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = "Turn any digital song into vintage turntable warmth.",
                                color = theme.textSecondary,
                                fontSize = if (isScreenCompact) 12.sp else 13.sp,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(if (isScreenCompact) 8.dp else 12.dp))

                            // Try Now Pill Button
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(theme.pillGradient)
                                    .clickable {
                                        onStyleSelected(AudioStyle.VINYL)
                                        val firstTrack = allTracks.firstOrNull()
                                        if (firstTrack != null) onTrackSelected(firstTrack)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Transform",
                                        color = Color(0xFF130924),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF130924),
                                        modifier = Modifier
                                            .size(14.dp)
                                            .padding(start = 4.dp)
                                    )
                                }
                            }
                        }

                        // Right 3D Circular Vinyl Visual Asset - zero rectangular artifacts
                        Box(
                            modifier = Modifier
                                .weight(0.85f)
                                .size(vinylSize),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.wooly_vinyl_disc),
                                contentDescription = "Vinyl Record Disc",
                                modifier = Modifier
                                    .size(vinylSize)
                                    .offset(x = 6.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 1.5.dp,
                                        brush = Brush.sweepGradient(
                                            listOf(
                                                theme.primary,
                                                theme.secondary,
                                                theme.tertiary,
                                                theme.primary
                                            )
                                        ),
                                        shape = CircleShape
                                    ),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Quick Styles Section (Circular style discs)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Styles",
                        color = theme.textPrimary,
                        fontSize = if (isScreenCompact) 16.sp else 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "See all",
                        color = theme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable {
                            onStyleSelected(AudioStyle.LO_FI)
                            val track = allTracks.firstOrNull()
                            if (track != null) onTrackSelected(track)
                        }
                    )
                }

                val popularStyles = listOf(
                    AudioStyle.LO_FI,
                    AudioStyle.REVERB,
                    AudioStyle.SLOWED_REVERB,
                    AudioStyle.VINYL,
                    AudioStyle.SURROUND_3D,
                    AudioStyle.NIGHTCORE,
                    AudioStyle.AMBIENT,
                    AudioStyle.BASS_BOOST
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = horizontalPad),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(popularStyles) { style ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    onStyleSelected(style)
                                    val track = allTracks.firstOrNull()
                                    if (track != null) onTrackSelected(track)
                                }
                        ) {
                            TrackArtwork(
                                title = style.displayName,
                                styleName = style.displayName,
                                size = if (isScreenCompact) 56.dp else 66.dp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = style.displayName,
                                color = theme.textPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Recently Used / Converted Tracks Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Tracks",
                        color = theme.textPrimary,
                        fontSize = if (isScreenCompact) 16.sp else 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${recentTracks.size} available",
                        color = theme.textMuted,
                        fontSize = 12.sp
                    )
                }
            }

            if (filteredRecent.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPad, vertical = 6.dp),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No audio tracks yet",
                                color = theme.textSecondary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Import an audio file or select a preset to begin transforming!",
                                color = theme.textMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredRecent) { track ->
                    val isCurrent = currentPlayingTrack?.id == track.id
                    val isThisPlaying = isCurrent && isPlaying

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPad, vertical = 4.dp)
                            .clickable { onTrackSelected(track) },
                        shape = RoundedCornerShape(28.dp),
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Circular Artwork
                                TrackArtwork(
                                    title = track.title,
                                    styleName = track.styleName,
                                    size = if (isScreenCompact) 44.dp else 50.dp,
                                    isPlaying = isThisPlaying
                                )

                                Column(
                                    modifier = Modifier
                                        .padding(start = 12.dp, end = 8.dp)
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = track.title,
                                        color = theme.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = track.styleName,
                                            color = theme.primary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = " • ${track.artist}",
                                            color = theme.textMuted,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Quick Re-Edit / Transform action button
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(theme.glassBg)
                                        .border(1.dp, theme.glassBorder, CircleShape)
                                        .clickable { onTrackSelected(track) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = "Edit style",
                                        tint = theme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Play / Pause Circle Button (Preview / Listen)
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isThisPlaying) theme.pillGradient
                                            else Brush.horizontalGradient(listOf(theme.glassBg, theme.glassBg))
                                        )
                                        .border(1.dp, theme.glassBorder, CircleShape)
                                        .clickable {
                                            if (isCurrent) {
                                                onTogglePlay()
                                            } else {
                                                onPlayTrack(track)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isThisPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Listen / Preview",
                                        tint = if (isThisPlaying) Color(0xFF130924) else theme.textPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Device File Permissions & Scanner Card
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad)
                        .clip(CircleShape)
                        .background(theme.glassBg)
                        .border(1.dp, theme.primary.copy(alpha = 0.5f), CircleShape)
                        .clickable { onRequestPermissions() }
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = theme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Grant Full Audio & Files Access",
                            color = theme.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Import audio helper pill
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad)
                        .clip(CircleShape)
                        .background(theme.glassBg)
                        .border(1.dp, theme.glassBorder, CircleShape)
                        .clickable { onImportClicked() }
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = theme.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Import Audio from Device Storage",
                            color = theme.textSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Modern Theme Selector Dialog
        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                title = {
                    Text(
                        text = "Choose Modern Theme",
                        color = theme.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        WoolyThemePreset.entries.forEach { preset ->
                            val isSelected = preset == currentThemePreset
                            val itemBgBrush = if (isSelected) theme.pillGradient else Brush.horizontalGradient(listOf(theme.glassBg, theme.glassBg))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(CircleShape)
                                    .background(itemBgBrush)
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0x99FFFFFF) else theme.glassBorder,
                                        CircleShape
                                    )
                                    .clickable {
                                        onSelectThemePreset(preset)
                                        showThemeDialog = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = preset.displayName,
                                            color = if (isSelected) Color(0xFF130924) else theme.textPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = preset.description,
                                            color = if (isSelected) Color(0xFF331F4D) else theme.textMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeDialog = false }) {
                        Text("Done", color = theme.primary)
                    }
                },
                containerColor = theme.surfaceElevated,
                shape = RoundedCornerShape(28.dp)
            )
        }
    }
}
