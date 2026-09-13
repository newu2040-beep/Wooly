package com.example.audio.engine

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import com.example.data.model.AudioTrackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class AudioPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs = _currentPositionMs.asStateFlow()

    private val _totalDurationMs = MutableStateFlow(0L)
    val totalDurationMs = _totalDurationMs.asStateFlow()

    private val _currentTrack = MutableStateFlow<AudioTrackEntity?>(null)
    val currentTrack = _currentTrack.asStateFlow()

    private val _currentStyle = MutableStateFlow("Normal")
    val currentStyle = _currentStyle.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed = _playbackSpeed.asStateFlow()

    private val _volume = MutableStateFlow(1.0f)
    val volume = _volume.asStateFlow()

    fun playTrack(track: AudioTrackEntity, styleName: String = track.styleName) {
        _currentTrack.value = track
        _currentStyle.value = styleName

        val filePath = track.processedFilePath ?: track.originalFilePath
        playFile(filePath)
    }

    fun playFile(path: String) {
        try {
            stop()

            val player = MediaPlayer()
            val file = File(path)
            if (file.exists()) {
                player.setDataSource(file.absolutePath)
            } else {
                player.setDataSource(context, Uri.parse(path))
            }

            player.setOnPreparedListener { mp ->
                _totalDurationMs.value = mp.duration.toLong().coerceAtLeast(1L)
                applyPlaybackParams(mp)
                mp.start()
                _isPlaying.value = true
                startProgressTracker()
            }

            player.setOnCompletionListener {
                _isPlaying.value = false
                _currentPositionMs.value = 0L
                stopProgressTracker()
            }

            player.setOnErrorListener { _, _, _ ->
                _isPlaying.value = false
                stopProgressTracker()
                true
            }

            mediaPlayer = player
            player.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: return
        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
            stopProgressTracker()
        } else {
            player.start()
            _isPlaying.value = true
            startProgressTracker()
        }
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer?.let { player ->
            player.seekTo(positionMs.toInt())
            _currentPositionMs.value = positionMs
        }
    }

    fun setSpeed(speed: Float) {
        _playbackSpeed.value = speed
        mediaPlayer?.let { applyPlaybackParams(it) }
    }

    fun setVolume(vol: Float) {
        _volume.value = vol.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(_volume.value, _volume.value)
    }

    fun stop() {
        stopProgressTracker()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
        _isPlaying.value = false
        _currentPositionMs.value = 0L
    }

    private fun applyPlaybackParams(player: MediaPlayer) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val params = player.playbackParams ?: PlaybackParams()
                params.speed = _playbackSpeed.value
                player.playbackParams = params
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        player.setVolume(_volume.value, _volume.value)
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch {
            while (isActive) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        _currentPositionMs.value = player.currentPosition.toLong()
                    }
                }
                delay(200)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }
}
