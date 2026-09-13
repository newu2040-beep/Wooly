package com.example.audio.engine

import android.content.Context
import com.example.data.dao.AudioTrackDao
import com.example.data.model.AudioTrackEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin

object DemoAudioGenerator {

    suspend fun ensureDemoTracks(context: Context, dao: AudioTrackDao) = withContext(Dispatchers.IO) {
        val existingCount = dao.getCount()
        if (existingCount > 0) return@withContext

        val samplesDir = File(context.filesDir, "samples")
        if (!samplesDir.exists()) samplesDir.mkdirs()

        val sampleConfigs = listOf(
            SampleTrackConfig(
                title = "Midnight Dreams",
                artist = "Lofi Sunset",
                style = "Lo-fi",
                rootFreq = 220.0, // A minor vibe
                tempoBpm = 75,
                durationSeconds = 30
            ),
            SampleTrackConfig(
                title = "Ocean Eyes",
                artist = "Ethereal Echoes",
                style = "Reverb",
                rootFreq = 261.63, // C major warm swell
                tempoBpm = 68,
                durationSeconds = 28
            ),
            SampleTrackConfig(
                title = "Vintage Soul",
                artist = "Dusty Grooves",
                style = "Vinyl",
                rootFreq = 196.0, // G minor jazz chords
                tempoBpm = 82,
                durationSeconds = 26
            ),
            SampleTrackConfig(
                title = "Sunset Glow",
                artist = "Pastel Sky",
                style = "Lo-fi",
                rootFreq = 293.66, // D minor chill
                tempoBpm = 72,
                durationSeconds = 24
            )
        )

        for (cfg in sampleConfigs) {
            val file = File(samplesDir, "${cfg.title.replace(" ", "_")}.wav")
            if (!file.exists()) {
                generateMelodyWav(file, cfg)
            }

            dao.insertTrack(
                AudioTrackEntity(
                    title = cfg.title,
                    artist = cfg.artist,
                    originalFilePath = file.absolutePath,
                    processedFilePath = null,
                    durationMs = cfg.durationSeconds * 1000L,
                    fileSizeBytes = file.length(),
                    styleName = cfg.style,
                    isSaved = true,
                    isDemoSample = true,
                    format = "WAV",
                    bitrateKbps = 320,
                    sampleRate = 44100
                )
            )
        }
    }

    private data class SampleTrackConfig(
        val title: String,
        val artist: String,
        val style: String,
        val rootFreq: Double,
        val tempoBpm: Int,
        val durationSeconds: Int
    )

    private fun generateMelodyWav(file: File, config: SampleTrackConfig) {
        val sampleRate = 44100
        val totalFrames = config.durationSeconds * sampleRate
        val totalDataLen = totalFrames * 2 * 2 // stereo 16-bit
        val totalFileLen = totalDataLen + 36

        // Chord progression intervals relative to root
        val chordProgressions = listOf(
            doubleArrayOf(1.0, 1.2, 1.5, 1.8),       // Minor 7th
            doubleArrayOf(0.75, 0.9, 1.125, 1.35),   // Subdominant 7th
            doubleArrayOf(0.888, 1.066, 1.333, 1.6), // Mediant
            doubleArrayOf(0.666, 0.8, 1.0, 1.2)      // Dominant
        )

        FileOutputStream(file).use { out ->
            val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
            header.put('R'.code.toByte())
            header.put('I'.code.toByte())
            header.put('F'.code.toByte())
            header.put('F'.code.toByte())
            header.putInt(totalFileLen)
            header.put('W'.code.toByte())
            header.put('A'.code.toByte())
            header.put('V'.code.toByte())
            header.put('E'.code.toByte())
            header.put('f'.code.toByte())
            header.put('m'.code.toByte())
            header.put('t'.code.toByte())
            header.put(' '.code.toByte())
            header.putInt(16)
            header.putShort(1.toShort())
            header.putShort(2.toShort())
            header.putInt(sampleRate)
            header.putInt(sampleRate * 4)
            header.putShort(4.toShort())
            header.putShort(16.toShort())
            header.put('d'.code.toByte())
            header.put('a'.code.toByte())
            header.put('t'.code.toByte())
            header.put('a'.code.toByte())
            header.putInt(totalDataLen)
            out.write(header.array())

            val buffer = ByteBuffer.allocate(4096 * 4).order(ByteOrder.LITTLE_ENDIAN)
            val beatFrames = (60.0 / config.tempoBpm * sampleRate).toInt()
            val measureFrames = beatFrames * 4

            var frame = 0
            while (frame < totalFrames) {
                buffer.clear()
                val chunkEnd = (frame + 4096).coerceAtMost(totalFrames)

                for (f in frame until chunkEnd) {
                    val chordIdx = (f / measureFrames) % chordProgressions.size
                    val chord = chordProgressions[chordIdx]
                    val beatTime = (f % beatFrames).toDouble() / beatFrames
                    val chordEnvelope = (1.0 - beatTime * 0.4).coerceIn(0.0, 1.0)

                    // Electric Rhodes chime synthesis
                    var sampleL = 0.0
                    var sampleR = 0.0

                    for (i in chord.indices) {
                        val freq = config.rootFreq * chord[i]
                        val phase = 2.0 * PI * freq * (f.toDouble() / sampleRate)
                        val fundamental = sin(phase)
                        val harmonic2 = sin(phase * 2.0) * 0.35
                        val harmonic3 = sin(phase * 3.0) * 0.15
                        val voice = (fundamental + harmonic2 + harmonic3) * 0.18 * chordEnvelope

                        // Slight stereo spread per chord note
                        sampleL += voice * (0.8 + i * 0.1)
                        sampleR += voice * (1.1 - i * 0.1)
                    }

                    // Warm sub-bass on root note
                    val bassFreq = config.rootFreq * 0.5 * chord[0]
                    val bassPhase = 2.0 * PI * bassFreq * (f.toDouble() / sampleRate)
                    val bass = sin(bassPhase) * 0.28
                    sampleL += bass
                    sampleR += bass

                    // Melodic chime note every 2 beats
                    val chimeNote = (f / (beatFrames * 2)) % 4
                    val chimeFreq = config.rootFreq * 2.0 * chord[chimeNote]
                    val chimeEnvelope = (1.0 - ((f % (beatFrames * 2)).toDouble() / (beatFrames * 2))).coerceAtLeast(0.0)
                    val chime = sin(2.0 * PI * chimeFreq * (f.toDouble() / sampleRate)) * 0.14 * (chimeEnvelope * chimeEnvelope)
                    sampleL += chime * 0.7
                    sampleR += chime * 1.1

                    // Fade in / out
                    val fadeFrames = sampleRate * 2
                    var masterFade = 1.0
                    if (f < fadeFrames) masterFade *= (f.toDouble() / fadeFrames)
                    if (totalFrames - f < fadeFrames) masterFade *= ((totalFrames - f).toDouble() / fadeFrames)

                    val pcmL = ((sampleL * masterFade).coerceIn(-1.0, 1.0) * 32000.0).toInt().toShort()
                    val pcmR = ((sampleR * masterFade).coerceIn(-1.0, 1.0) * 32000.0).toInt().toShort()

                    buffer.putShort(pcmL)
                    buffer.putShort(pcmR)
                }

                out.write(buffer.array(), 0, (chunkEnd - frame) * 4)
                frame = chunkEnd
            }
        }
    }
}
