package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.UiNotice
import com.example.ui.WoolyViewModel
import com.example.ui.components.ExportDialog
import com.example.ui.components.FloatingNavBar
import com.example.ui.components.FullScreenPlayerModal
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.NavTab
import com.example.ui.components.PermissionDialog
import com.example.ui.screens.ConverterScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.LocalThemeColors
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.getThemeColors
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class ActiveScreen {
    WELCOME,
    MAIN_TABS,
    CONVERTER
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                WoolyApp()
            }
        }
    }
}

@Composable
fun WoolyApp(viewModel: WoolyViewModel = viewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var currentScreen by remember { mutableStateOf(ActiveScreen.WELCOME) }
    var currentNavTab by remember { mutableStateOf(NavTab.HOME) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Theme & Display preferences
    val themePreset by viewModel.themePreset.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isCompactMode by viewModel.isCompactMode.collectAsState()

    val currentColors = remember(themePreset, isDarkMode) {
        getThemeColors(themePreset, isDarkMode)
    }

    // File picker for local audio files
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.importAudioUri(uri) { importedTrack ->
                viewModel.selectTrackForConversion(importedTrack)
                currentScreen = ActiveScreen.CONVERTER
            }
        }
    }

    // Collect ViewModel states
    val allTracks by viewModel.allTracks.collectAsState()
    val savedTracks by viewModel.savedTracks.collectAsState()
    val recentTracks by viewModel.recentTracks.collectAsState()
    val selectedTrack by viewModel.selectedTrack.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val customParams by viewModel.customParams.collectAsState()

    val isProcessing by viewModel.isProcessing.collectAsState()
    val processingProgress by viewModel.processingProgress.collectAsState()
    val processingStatus by viewModel.processingStatus.collectAsState()

    val showExportDialog by viewModel.showExportDialog.collectAsState()
    val exportEstimate by viewModel.exportEstimate.collectAsState()
    val showFullScreenPlayer by viewModel.showFullScreenPlayer.collectAsState()

    // Player states
    val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
    val currentPlayingTrack by viewModel.playerManager.currentTrack.collectAsState()
    val currentPositionMs by viewModel.playerManager.currentPositionMs.collectAsState()
    val totalDurationMs by viewModel.playerManager.totalDurationMs.collectAsState()
    val currentStyleName by viewModel.playerManager.currentStyle.collectAsState()
    val playbackSpeed by viewModel.playerManager.playbackSpeed.collectAsState()
    val volume by viewModel.playerManager.volume.collectAsState()

    // Observe snackbar/toast notices
    LaunchedEffect(Unit) {
        viewModel.uiNotice.collectLatest { notice ->
            when (notice) {
                is UiNotice.Success -> snackbarHostState.showSnackbar(notice.message)
                is UiNotice.Error -> snackbarHostState.showSnackbar("Error: ${notice.message}")
            }
        }
    }

    CompositionLocalProvider(LocalThemeColors provides currentColors) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(currentColors.backgroundBrush)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    ActiveScreen.WELCOME -> {
                        WelcomeScreen(
                            onGetStarted = {
                                currentScreen = ActiveScreen.MAIN_TABS
                                currentNavTab = NavTab.HOME
                            }
                        )
                    }

                    ActiveScreen.CONVERTER -> {
                        ConverterScreen(
                            track = selectedTrack ?: allTracks.firstOrNull(),
                            selectedStyle = selectedStyle,
                            customParams = customParams,
                            isProcessing = isProcessing,
                            processingProgress = processingProgress,
                            processingStatus = processingStatus,
                            onBack = { currentScreen = ActiveScreen.MAIN_TABS },
                            onStyleSelected = { style -> viewModel.selectStyle(style) },
                            onParamsChanged = { params -> viewModel.updateCustomParams(params) },
                            onResetParams = { viewModel.resetParams() },
                            onPreview = { viewModel.previewConversion() },
                            onExport = { viewModel.openExportDialog() },
                            onSelectTrack = {
                                currentScreen = ActiveScreen.MAIN_TABS
                                currentNavTab = NavTab.LIBRARY
                            }
                        )
                    }

                    ActiveScreen.MAIN_TABS -> {
                        Scaffold(
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            containerColor = androidx.compose.ui.graphics.Color.Transparent
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (currentNavTab) {
                                    NavTab.HOME -> {
                                        HomeScreen(
                                            recentTracks = recentTracks,
                                            allTracks = allTracks,
                                            currentPlayingTrack = currentPlayingTrack,
                                            isPlaying = isPlaying,
                                            isDarkMode = isDarkMode,
                                            currentThemePreset = themePreset,
                                            isCompactMode = isCompactMode,
                                            onTrackSelected = { track ->
                                                viewModel.selectTrackForConversion(track)
                                                currentScreen = ActiveScreen.CONVERTER
                                            },
                                            onStyleSelected = { style ->
                                                viewModel.selectStyle(style)
                                                val targetTrack = selectedTrack ?: allTracks.firstOrNull()
                                                if (targetTrack != null) {
                                                    viewModel.selectTrackForConversion(targetTrack)
                                                }
                                                currentScreen = ActiveScreen.CONVERTER
                                            },
                                            onImportClicked = {
                                                audioPickerLauncher.launch("audio/*")
                                            },
                                            onPlayTrack = { track ->
                                                viewModel.playerManager.playTrack(track)
                                            },
                                            onTogglePlay = {
                                                viewModel.playerManager.togglePlayPause()
                                            },
                                            onToggleDarkMode = { viewModel.toggleDarkMode() },
                                            onSelectThemePreset = { viewModel.setThemePreset(it) },
                                            onToggleCompactMode = { viewModel.toggleCompactMode() },
                                            onRequestPermissions = { showPermissionDialog = true },
                                            onOpenSettings = {
                                                currentNavTab = NavTab.MORE
                                            }
                                        )
                                    }

                                    NavTab.LIBRARY, NavTab.SAVED -> {
                                        LibraryScreen(
                                            allTracks = allTracks,
                                            savedTracks = savedTracks,
                                            recentTracks = recentTracks,
                                            currentTrack = currentPlayingTrack,
                                            isPlaying = isPlaying,
                                            onTrackClick = { track ->
                                                viewModel.selectTrackForConversion(track)
                                                currentScreen = ActiveScreen.CONVERTER
                                            },
                                            onPlayTrack = { track ->
                                                viewModel.playerManager.playTrack(track)
                                            },
                                            onTogglePlay = {
                                                viewModel.playerManager.togglePlayPause()
                                            },
                                            onToggleSave = { track ->
                                                viewModel.toggleSaveStatus(track)
                                            },
                                            onConvertToStyle = { track ->
                                                viewModel.selectTrackForConversion(track)
                                                currentScreen = ActiveScreen.CONVERTER
                                            },
                                            onRenameTrack = { track, newName ->
                                                viewModel.renameTrack(track, newName)
                                            },
                                            onDeleteTrack = { track ->
                                                viewModel.deleteTrack(track)
                                            },
                                            onShareTrack = { track ->
                                                viewModel.shareTrack(context, track)
                                            },
                                            onScanDevice = {
                                                viewModel.scanDeviceAudioLibrary()
                                            }
                                        )
                                    }

                                    NavTab.MORE -> {
                                        SettingsScreen(
                                            isDarkMode = isDarkMode,
                                            onToggleDarkMode = { viewModel.toggleDarkMode() },
                                            currentTheme = themePreset,
                                            onSelectTheme = { viewModel.setThemePreset(it) },
                                            isCompactMode = isCompactMode,
                                            onToggleCompactMode = { viewModel.toggleCompactMode() },
                                            onRequestPermissions = { showPermissionDialog = true },
                                            onScanDevice = { viewModel.scanDeviceAudioLibrary() },
                                            onClearCache = {
                                                scope.launch {
                                                    val cacheDir = context.cacheDir
                                                    cacheDir.listFiles()?.forEach { if (it.name.startsWith("preview_")) it.delete() }
                                                    snackbarHostState.showSnackbar("Temporary preview cache cleared")
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Persistent Mini Player & Floating Nav Bar (visible in main tabs and converter)
            if (currentScreen != ActiveScreen.WELCOME) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Mini Player Bar (shown if track is selected/playing)
                    currentPlayingTrack?.let { track ->
                        MiniPlayerBar(
                            track = track,
                            isPlaying = isPlaying,
                            currentPosMs = currentPositionMs,
                            totalDurationMs = totalDurationMs,
                            styleName = currentStyleName,
                            onTogglePlay = { viewModel.playerManager.togglePlayPause() },
                            onClick = { viewModel.setShowFullScreenPlayer(true) },
                            modifier = Modifier.padding(bottom = if (currentScreen == ActiveScreen.MAIN_TABS) 76.dp else 8.dp)
                        )
                    }

                    // Floating Nav Bar (only in Main Tabs)
                    if (currentScreen == ActiveScreen.MAIN_TABS) {
                        FloatingNavBar(
                            currentTab = currentNavTab,
                            onTabSelected = { tab -> currentNavTab = tab },
                            onCenterAction = {
                                audioPickerLauncher.launch("audio/*")
                            }
                        )
                    }
                }
            }

            // Full Screen Player Modal
            if (showFullScreenPlayer && currentPlayingTrack != null) {
                FullScreenPlayerModal(
                    track = currentPlayingTrack!!,
                    isPlaying = isPlaying,
                    currentPosMs = currentPositionMs,
                    totalDurationMs = totalDurationMs,
                    styleName = currentStyleName,
                    playbackSpeed = playbackSpeed,
                    volume = volume,
                    onTogglePlay = { viewModel.playerManager.togglePlayPause() },
                    onSeek = { pos -> viewModel.playerManager.seekTo(pos) },
                    onSpeedChange = { speed -> viewModel.playerManager.setSpeed(speed) },
                    onVolumeChange = { vol -> viewModel.playerManager.setVolume(vol) },
                    onShare = { viewModel.shareTrack(context, currentPlayingTrack!!) },
                    onDismiss = { viewModel.setShowFullScreenPlayer(false) }
                )
            }

            // Export Dialog
            if (showExportDialog && selectedTrack != null) {
                val initialExportName = "${selectedTrack!!.title}_${selectedStyle.displayName.replace(" ", "_")}"
                ExportDialog(
                    initialFilename = initialExportName,
                    estimate = exportEstimate,
                    onDismiss = { viewModel.closeExportDialog() },
                    onExport = { filename, format, bitrate ->
                        viewModel.convertAndExport(filename, format, bitrate)
                    }
                )
            }

            // Permission Request Dialog
            if (showPermissionDialog) {
                PermissionDialog(
                    onDismiss = { showPermissionDialog = false },
                    onPermissionGranted = {
                        viewModel.scanDeviceAudioLibrary()
                    },
                    onOpenPicker = {
                        audioPickerLauncher.launch("audio/*")
                    }
                )
            }
        }
    }
}
