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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioTrackEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTabPill
import com.example.ui.components.TrackArtwork
import com.example.ui.theme.LocalThemeColors
import java.util.Locale

enum class LibraryTab(val title: String) {
    MY_MUSIC("My Music"),
    SAVED("Saved"),
    HISTORY("History")
}

@Composable
fun LibraryScreen(
    allTracks: List<AudioTrackEntity>,
    savedTracks: List<AudioTrackEntity>,
    recentTracks: List<AudioTrackEntity>,
    currentTrack: AudioTrackEntity?,
    isPlaying: Boolean,
    onTrackClick: (AudioTrackEntity) -> Unit,
    onPlayTrack: (AudioTrackEntity) -> Unit,
    onTogglePlay: () -> Unit,
    onToggleSave: (AudioTrackEntity) -> Unit,
    onConvertToStyle: (AudioTrackEntity) -> Unit,
    onRenameTrack: (AudioTrackEntity, String) -> Unit,
    onDeleteTrack: (AudioTrackEntity) -> Unit,
    onShareTrack: (AudioTrackEntity) -> Unit,
    onScanDevice: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val theme = LocalThemeColors.current
    var selectedTab by remember { mutableStateOf(LibraryTab.MY_MUSIC) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchInput by remember { mutableStateOf(false) }

    var trackToRename by remember { mutableStateOf<AudioTrackEntity?>(null) }
    var renameInput by remember { mutableStateOf("") }

    var trackToDelete by remember { mutableStateOf<AudioTrackEntity?>(null) }

    val rawList = when (selectedTab) {
        LibraryTab.MY_MUSIC -> allTracks
        LibraryTab.SAVED -> savedTracks
        LibraryTab.HISTORY -> recentTracks
    }

    val displayList = remember(rawList, searchQuery) {
        if (searchQuery.isBlank()) rawList
        else rawList.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.artist.contains(searchQuery, ignoreCase = true) ||
            it.styleName.contains(searchQuery, ignoreCase = true)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .testTag("library_screen")
            .fillMaxSize()
            .background(theme.backgroundBrush)
            .statusBarsPadding()
    ) {
        val isScreenCompact = maxHeight < 720.dp
        val horizontalPad = if (isScreenCompact) 16.dp else 20.dp

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 130.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad, vertical = if (isScreenCompact) 6.dp else 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Library",
                            color = theme.textPrimary,
                            fontSize = if (isScreenCompact) 20.sp else 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${displayList.size} tracks",
                            color = theme.textMuted,
                            fontSize = 11.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(theme.glassBg)
                                .border(1.dp, theme.glassBorder, CircleShape)
                                .clickable { onScanDevice() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = "Scan Device Music",
                                tint = theme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(theme.glassBg)
                                .border(1.dp, theme.glassBorder, CircleShape)
                                .clickable { showSearchInput = !showSearchInput },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = if (showSearchInput) theme.primary else theme.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Search Bar (Pill shaped, zero rectangles)
            if (showSearchInput) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Filter your music tracks...", color = theme.textMuted) },
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
            }

            // Tab Selector Pills: My Music | Saved | History
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LibraryTab.entries.forEach { tab ->
                        GlassTabPill(
                            text = tab.title,
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // List of Tracks
            if (displayList.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPad, vertical = 16.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(theme.primary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = theme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No tracks in ${selectedTab.title}",
                                color = theme.textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Converted masters, device imports or saved favorites will appear here.",
                                color = theme.textSecondary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(theme.pillGradient)
                                    .clickable { onScanDevice() }
                                    .padding(horizontal = 18.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Scan Device Music",
                                    color = Color(0xFF130924),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                items(displayList) { track ->
                    val isCurrent = currentTrack?.id == track.id
                    val isThisPlaying = isCurrent && isPlaying
                    var menuExpanded by remember { mutableStateOf(false) }

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPad, vertical = 4.dp)
                            .clickable { onTrackClick(track) },
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
                                // Circular Artwork (never rectangle)
                                TrackArtwork(
                                    title = track.title,
                                    styleName = track.styleName,
                                    size = if (isScreenCompact) 44.dp else 50.dp,
                                    isPlaying = isThisPlaying
                                )

                                Column(
                                    modifier = Modifier
                                        .padding(start = 12.dp, end = 6.dp)
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
                                        val sec = track.durationMs / 1000
                                        val durStr = "${sec / 60}:${String.format(Locale.US, "%02d", sec % 60)}"
                                        Text(
                                            text = " • $durStr • ${track.format}",
                                            color = theme.textMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Quick Re-Edit / Redit / Transform Button
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(theme.glassBg)
                                        .border(1.dp, theme.glassBorder, CircleShape)
                                        .clickable { onConvertToStyle(track) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = "Re-edit audio style",
                                        tint = theme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Play / Pause Button (Listen Preview)
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
                                        contentDescription = "Play / Preview",
                                        tint = if (isThisPlaying) Color(0xFF130924) else theme.textPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // More Options Dropdown Menu (Rename, Delete, Share)
                                Box {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .clickable { menuExpanded = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "Options",
                                            tint = theme.textSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = menuExpanded,
                                        onDismissRequest = { menuExpanded = false },
                                        modifier = Modifier
                                            .background(theme.surfaceElevated)
                                            .border(1.dp, theme.glassBorder, RoundedCornerShape(16.dp))
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Re-Edit & Style", color = theme.textPrimary) },
                                            leadingIcon = { Icon(Icons.Default.Tune, null, tint = theme.primary) },
                                            onClick = {
                                                menuExpanded = false
                                                onConvertToStyle(track)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Share Audio", color = theme.textPrimary) },
                                            leadingIcon = { Icon(Icons.Default.Share, null, tint = theme.primary) },
                                            onClick = {
                                                menuExpanded = false
                                                onShareTrack(track)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Rename", color = theme.textPrimary) },
                                            leadingIcon = { Icon(Icons.Default.Edit, null, tint = theme.primary) },
                                            onClick = {
                                                menuExpanded = false
                                                trackToRename = track
                                                renameInput = track.title
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    if (track.isSaved) "Remove from Saved" else "Save to Favorites",
                                                    color = theme.textPrimary
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    if (track.isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                                    null,
                                                    tint = theme.secondary
                                                )
                                            },
                                            onClick = {
                                                menuExpanded = false
                                                onToggleSave(track)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Delete Track", color = Color(0xFFEF4444)) },
                                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444)) },
                                            onClick = {
                                                menuExpanded = false
                                                trackToDelete = track
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Rename Dialog (Smooth Pill / Circular shape)
        trackToRename?.let { track ->
            AlertDialog(
                onDismissRequest = { trackToRename = null },
                title = { Text("Rename Track", color = theme.textPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = renameInput,
                        onValueChange = { renameInput = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = theme.textPrimary,
                            unfocusedTextColor = theme.textPrimary,
                            focusedBorderColor = theme.primary,
                            unfocusedBorderColor = theme.glassBorder
                        ),
                        shape = CircleShape,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        onRenameTrack(track, renameInput)
                        trackToRename = null
                    }) {
                        Text("Save", color = theme.primary, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { trackToRename = null }) {
                        Text("Cancel", color = theme.textSecondary)
                    }
                },
                containerColor = theme.surfaceElevated,
                shape = RoundedCornerShape(28.dp)
            )
        }

        // Delete Confirmation Dialog
        trackToDelete?.let { track ->
            AlertDialog(
                onDismissRequest = { trackToDelete = null },
                title = { Text("Delete Track?", color = theme.textPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Are you sure you want to remove \"${track.title}\"? The local exported audio master will also be removed from storage.",
                        color = theme.textSecondary
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteTrack(track)
                        trackToDelete = null
                    }) {
                        Text("Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { trackToDelete = null }) {
                        Text("Cancel", color = theme.textSecondary)
                    }
                },
                containerColor = theme.surfaceElevated,
                shape = RoundedCornerShape(28.dp)
            )
        }
    }
}
