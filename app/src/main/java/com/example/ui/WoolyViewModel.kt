package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.dsp.AudioProcessor
import com.example.audio.engine.AudioDecoder
import com.example.audio.engine.AudioExporter
import com.example.audio.engine.AudioPlayerManager
import com.example.audio.engine.DemoAudioGenerator
import com.example.audio.engine.ExportEstimate
import com.example.data.WoolyDatabase
import com.example.data.model.AudioStyle
import com.example.data.model.AudioTrackEntity
import com.example.data.model.CustomAudioParams
import com.example.data.repository.AudioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

sealed class UiNotice {
    data class Success(val message: String) : UiNotice()
    data class Error(val message: String) : UiNotice()
}

class WoolyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = WoolyDatabase.getDatabase(application)
    private val repository = AudioRepository(db.audioTrackDao())
    val playerManager = AudioPlayerManager(application)

    val allTracks = repository.allTracks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val savedTracks = repository.savedTracks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val processedTracks = repository.processedTracks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val recentTracks = repository.recentTracks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Converter Screen State
    private val _selectedTrack = MutableStateFlow<AudioTrackEntity?>(null)
    val selectedTrack = _selectedTrack.asStateFlow()

    private val _selectedStyle = MutableStateFlow(AudioStyle.LO_FI)
    val selectedStyle = _selectedStyle.asStateFlow()

    private val _customParams = MutableStateFlow(AudioStyle.LO_FI.toDefaultParams())
    val customParams = _customParams.asStateFlow()

    // Processing State
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _processingProgress = MutableStateFlow(0.0f)
    val processingProgress = _processingProgress.asStateFlow()

    private val _processingStatus = MutableStateFlow("")
    val processingStatus = _processingStatus.asStateFlow()

    // Player UI State
    private val _showFullScreenPlayer = MutableStateFlow(false)
    val showFullScreenPlayer = _showFullScreenPlayer.asStateFlow()

    // Export Dialog State
    private val _showExportDialog = MutableStateFlow(false)
    val showExportDialog = _showExportDialog.asStateFlow()

    private val _exportEstimate = MutableStateFlow<ExportEstimate?>(null)
    val exportEstimate = _exportEstimate.asStateFlow()

    // Theme & Display Mode State
    private val _themePreset = MutableStateFlow(com.example.ui.theme.WoolyThemePreset.TWILIGHT)
    val themePreset = _themePreset.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _isCompactMode = MutableStateFlow(false)
    val isCompactMode = _isCompactMode.asStateFlow()

    // Toast / Snack Notices
    private val _uiNotice = MutableSharedFlow<UiNotice>()
    val uiNotice = _uiNotice.asSharedFlow()

    init {
        viewModelScope.launch {
            DemoAudioGenerator.ensureDemoTracks(getApplication(), db.audioTrackDao())
        }
    }

    fun setThemePreset(preset: com.example.ui.theme.WoolyThemePreset) {
        _themePreset.value = preset
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setDarkMode(dark: Boolean) {
        _isDarkMode.value = dark
    }

    fun toggleCompactMode() {
        _isCompactMode.value = !_isCompactMode.value
    }

    fun setCompactMode(compact: Boolean) {
        _isCompactMode.value = compact
    }

    /**
     * Scans device audio files from MediaStore when storage permission is granted.
     */
    fun scanDeviceAudioLibrary() {
        viewModelScope.launch(Dispatchers.IO) {
            val contentResolver = getApplication<Application>().contentResolver
            val projection = arrayOf(
                android.provider.MediaStore.Audio.Media._ID,
                android.provider.MediaStore.Audio.Media.TITLE,
                android.provider.MediaStore.Audio.Media.ARTIST,
                android.provider.MediaStore.Audio.Media.DURATION,
                android.provider.MediaStore.Audio.Media.DATA,
                android.provider.MediaStore.Audio.Media.SIZE
            )
            val selection = "${android.provider.MediaStore.Audio.Media.IS_MUSIC} != 0"
            try {
                contentResolver.query(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    "${android.provider.MediaStore.Audio.Media.DATE_ADDED} DESC"
                )?.use { cursor ->
                    val titleCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.TITLE)
                    val artistCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.ARTIST)
                    val durCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DURATION)
                    val dataCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DATA)
                    val sizeCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.SIZE)

                    var addedCount = 0
                    while (cursor.moveToNext() && addedCount < 60) {
                        val path = cursor.getString(dataCol)
                        if (path != null && File(path).exists()) {
                            val title = cursor.getString(titleCol) ?: "Audio Track"
                            val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                            val duration = cursor.getLong(durCol)
                            val size = cursor.getLong(sizeCol)

                            val track = AudioTrackEntity(
                                title = title,
                                artist = artist,
                                originalFilePath = path,
                                processedFilePath = null,
                                durationMs = if (duration > 0) duration else 180_000L,
                                fileSizeBytes = size,
                                styleName = "Original",
                                isSaved = false,
                                isDemoSample = false,
                                format = "Device File"
                            )
                            repository.insertTrack(track)
                            addedCount++
                        }
                    }
                    if (addedCount > 0) {
                        _uiNotice.emit(UiNotice.Success("Imported $addedCount tracks from device"))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectTrackForConversion(track: AudioTrackEntity) {
        _selectedTrack.value = track
        updateEstimate()
    }

    fun selectStyle(style: AudioStyle) {
        _selectedStyle.value = style
        if (style != AudioStyle.CUSTOM) {
            _customParams.value = style.toDefaultParams()
        }
        updateEstimate()
    }

    fun updateCustomParams(newParams: CustomAudioParams) {
        _customParams.value = newParams
        _selectedStyle.value = AudioStyle.CUSTOM
        updateEstimate()
    }

    fun resetParams() {
        _customParams.value = _selectedStyle.value.toDefaultParams()
        updateEstimate()
    }

    private fun updateEstimate() {
        val track = _selectedTrack.value ?: return
        val estimate = AudioExporter.calculateEstimate(track.durationMs, _customParams.value)
        _exportEstimate.value = estimate
    }

    /**
     * Imports an audio file chosen by the user from device storage.
     */
    fun importAudioUri(uri: Uri, onImported: (AudioTrackEntity) -> Unit) {
        viewModelScope.launch {
            _isProcessing.value = true
            _processingStatus.value = "Reading audio file..."
            try {
                val decoded = AudioDecoder.decodeAudio(getApplication(), uri, maxDurationMs = 600_000L)
                val track = AudioTrackEntity(
                    title = decoded.title.ifBlank { "Imported Track" },
                    artist = decoded.artist.ifBlank { "Local Audio" },
                    originalFilePath = uri.toString(),
                    processedFilePath = null,
                    durationMs = decoded.durationMs,
                    fileSizeBytes = (decoded.samples.size * 2).toLong(),
                    styleName = "Normal",
                    isSaved = false,
                    isDemoSample = false,
                    format = "Original",
                    sampleRate = decoded.sampleRate
                )
                val newId = repository.insertTrack(track)
                val savedEntity = track.copy(id = newId)
                _selectedTrack.value = savedEntity
                _uiNotice.emit(UiNotice.Success("Audio imported successfully"))
                onImported(savedEntity)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiNotice.emit(UiNotice.Error("Could not decode audio: ${e.localizedMessage ?: "Unsupported format"}"))
            } finally {
                _isProcessing.value = false
                _processingStatus.value = ""
            }
        }
    }

    /**
     * Preview 25 seconds of the converted audio in real-time.
     */
    fun previewConversion() {
        val track = _selectedTrack.value ?: return
        viewModelScope.launch {
            _isProcessing.value = true
            _processingStatus.value = "Preparing preview..."
            _processingProgress.value = 0.15f
            try {
                val uri = if (track.originalFilePath.startsWith("content://") || track.originalFilePath.startsWith("file://")) {
                    Uri.parse(track.originalFilePath)
                } else {
                    Uri.fromFile(File(track.originalFilePath))
                }

                // Decode up to 25 seconds for preview
                val decoded = withContext(Dispatchers.IO) {
                    AudioDecoder.decodeAudio(getApplication(), uri, maxDurationMs = 25_000L)
                }
                _processingProgress.value = 0.5f
                _processingStatus.value = "Transforming sound vibes..."

                val processor = AudioProcessor(decoded.sampleRate)
                val processedSamples = withContext(Dispatchers.Default) {
                    processor.processBuffer(decoded.samples, _customParams.value) { p ->
                        _processingProgress.value = 0.5f + p * 0.35f
                    }
                }

                _processingStatus.value = "Buffering preview..."
                val previewFile = File(getApplication<Application>().cacheDir, "preview_temp.wav")
                withContext(Dispatchers.IO) {
                    val previewParams = _customParams.value.copy(outputFormat = "WAV")
                    AudioExporter.exportAudio(
                        context = getApplication(),
                        samples = processedSamples,
                        sampleRate = decoded.sampleRate,
                        baseName = "wooly_preview",
                        params = previewParams
                    )
                }
                _processingProgress.value = 1.0f

                playerManager.playFile(previewFile.absolutePath)
                _uiNotice.emit(UiNotice.Success("Playing ${selectedStyle.value.displayName} preview"))
            } catch (e: Exception) {
                e.printStackTrace()
                _uiNotice.emit(UiNotice.Error("Preview error: ${e.localizedMessage ?: "Processing failed"}"))
            } finally {
                _isProcessing.value = false
                _processingStatus.value = ""
                _processingProgress.value = 0.0f
            }
        }
    }

    /**
     * Converts the full audio file with genuine DSP and saves it locally.
     */
    fun convertAndExport(
        customFilename: String,
        outputFormat: String = _customParams.value.outputFormat,
        bitrate: Int = _customParams.value.bitrateKbps,
        onComplete: ((AudioTrackEntity) -> Unit)? = null
    ) {
        val track = _selectedTrack.value ?: return
        viewModelScope.launch {
            _isProcessing.value = true
            _processingProgress.value = 0.05f
            _processingStatus.value = "Decoding audio samples..."

            try {
                val uri = if (track.originalFilePath.startsWith("content://") || track.originalFilePath.startsWith("file://")) {
                    Uri.parse(track.originalFilePath)
                } else {
                    Uri.fromFile(File(track.originalFilePath))
                }

                val decoded = withContext(Dispatchers.IO) {
                    AudioDecoder.decodeAudio(getApplication(), uri, maxDurationMs = 900_000L) // 15 min max
                }

                _processingProgress.value = 0.25f
                _processingStatus.value = "Applying ${_selectedStyle.value.displayName} DSP processing..."

                val updatedParams = _customParams.value.copy(
                    outputFormat = outputFormat,
                    bitrateKbps = bitrate
                )

                val processor = AudioProcessor(decoded.sampleRate)
                val processedSamples = withContext(Dispatchers.Default) {
                    processor.processBuffer(decoded.samples, updatedParams) { progress ->
                        _processingProgress.value = 0.25f + progress * 0.50f
                    }
                }

                _processingProgress.value = 0.78f
                _processingStatus.value = "Writing high-quality master file..."

                val exportName = customFilename.ifBlank {
                    "${track.title}_${_selectedStyle.value.displayName}"
                }

                val savedFile = withContext(Dispatchers.IO) {
                    AudioExporter.exportAudio(
                        context = getApplication(),
                        samples = processedSamples,
                        sampleRate = decoded.sampleRate,
                        baseName = exportName,
                        params = updatedParams
                    ) { fileProgress ->
                        _processingProgress.value = 0.78f + fileProgress * 0.20f
                    }
                }

                _processingProgress.value = 1.0f
                _processingStatus.value = "Finalizing..."

                val effectiveDuration = (decoded.durationMs / updatedParams.speed.coerceIn(0.4f, 2.5f)).toLong()

                val processedTrack = AudioTrackEntity(
                    title = exportName,
                    artist = track.artist,
                    originalFilePath = track.originalFilePath,
                    processedFilePath = savedFile.absolutePath,
                    durationMs = effectiveDuration,
                    fileSizeBytes = savedFile.length(),
                    styleName = _selectedStyle.value.displayName,
                    isSaved = true,
                    format = outputFormat,
                    bitrateKbps = bitrate,
                    sampleRate = decoded.sampleRate
                )

                val newId = repository.insertTrack(processedTrack)
                val fullTrack = processedTrack.copy(id = newId)

                _uiNotice.emit(UiNotice.Success("Exported to: ${savedFile.name}"))
                playerManager.playTrack(fullTrack)
                onComplete?.invoke(fullTrack)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiNotice.emit(UiNotice.Error("Conversion failed: ${e.localizedMessage ?: "Unknown error"}"))
            } finally {
                _isProcessing.value = false
                _processingProgress.value = 0.0f
                _processingStatus.value = ""
                _showExportDialog.value = false
            }
        }
    }

    fun toggleSaveStatus(track: AudioTrackEntity) {
        viewModelScope.launch {
            repository.updateSavedStatus(track.id, !track.isSaved)
        }
    }

    fun renameTrack(track: AudioTrackEntity, newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            repository.renameTrack(track.id, newTitle.trim())
            _uiNotice.emit(UiNotice.Success("Track renamed to \"$newTitle\""))
        }
    }

    fun deleteTrack(track: AudioTrackEntity) {
        viewModelScope.launch {
            track.processedFilePath?.let { path ->
                val f = File(path)
                if (f.exists()) f.delete()
            }
            repository.deleteTrack(track)
            if (playerManager.currentTrack.value?.id == track.id) {
                playerManager.stop()
            }
            _uiNotice.emit(UiNotice.Success("Track deleted"))
        }
    }

    fun shareTrack(context: Context, track: AudioTrackEntity) {
        val path = track.processedFilePath ?: track.originalFilePath
        val file = File(path)
        if (!file.exists()) {
            viewModelScope.launch {
                _uiNotice.emit(UiNotice.Error("File not found on device"))
            }
            return
        }

        try {
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/*"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Share ${track.title}")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            viewModelScope.launch {
                _uiNotice.emit(UiNotice.Error("Failed to share audio: ${e.localizedMessage}"))
            }
        }
    }

    fun setShowFullScreenPlayer(show: Boolean) {
        _showFullScreenPlayer.value = show
    }

    fun openExportDialog() {
        updateEstimate()
        _showExportDialog.value = true
    }

    fun closeExportDialog() {
        _showExportDialog.value = false
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.stop()
    }
}
