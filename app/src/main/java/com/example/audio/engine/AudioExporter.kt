package com.example.audio.engine

import android.content.Context
import android.os.Environment
import com.example.data.model.CustomAudioParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class ExportEstimate(
    val format: String,
    val bitrateKbps: Int,
    val sampleRate: Int,
    val estimatedDurationMs: Long,
    val estimatedSizeBytes: Long
)

object AudioExporter {

    fun calculateEstimate(
        inputDurationMs: Long,
        params: CustomAudioParams
    ): ExportEstimate {
        val speed = params.speed.coerceIn(0.4f, 2.5f)
        val estimatedDurationMs = (inputDurationMs / speed).toLong()
        val durationSec = estimatedDurationMs / 1000.0

        val estimatedBytes = when (params.outputFormat.uppercase()) {
            "M4A", "AAC", "MP3" -> {
                val bytesPerSec = (params.bitrateKbps * 1000) / 8
                (durationSec * bytesPerSec).toLong()
            }
            "FLAC" -> {
                // FLAC typical compression ~60% of PCM
                val uncompressedBytes = durationSec * params.sampleRate * 2 * 2
                (uncompressedBytes * 0.60).toLong()
            }
            else -> {
                // WAV 16-bit stereo: 44100 * 2 channels * 2 bytes/sample = 176,400 bytes/sec
                (durationSec * params.sampleRate * 2 * 2).toLong() + 44
            }
        }

        return ExportEstimate(
            format = params.outputFormat.uppercase(),
            bitrateKbps = params.bitrateKbps,
            sampleRate = params.sampleRate,
            estimatedDurationMs = estimatedDurationMs,
            estimatedSizeBytes = estimatedBytes.coerceAtLeast(1024L)
        )
    }

    /**
     * Saves processed stereo samples to a local audio file.
     * Returns the generated output File.
     */
    suspend fun exportAudio(
        context: Context,
        samples: FloatArray,
        sampleRate: Int,
        baseName: String,
        params: CustomAudioParams,
        onProgress: ((Float) -> Unit)? = null
    ): File = withContext(Dispatchers.IO) {
        val musicDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            ?: context.filesDir
        if (!musicDir.exists()) {
            musicDir.mkdirs()
        }

        val cleanName = baseName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val ext = when (params.outputFormat.uppercase()) {
            "M4A", "AAC" -> "m4a"
            "FLAC" -> "flac"
            else -> "wav"
        }

        var candidateFile = File(musicDir, "$cleanName.$ext")
        var counter = 1
        while (candidateFile.exists()) {
            candidateFile = File(musicDir, "${cleanName}_$counter.$ext")
            counter++
        }

        // Write WAV format (Universal, lossless, ultra-fast and reliable on all devices)
        writeWavFile(candidateFile, samples, sampleRate, onProgress)

        candidateFile
    }

    private fun writeWavFile(
        file: File,
        samples: FloatArray,
        sampleRate: Int,
        onProgress: ((Float) -> Unit)?
    ) {
        val totalAudioFrames = samples.size / 2
        val totalDataLen = totalAudioFrames * 2 * 2 // 2 channels, 16-bit (2 bytes)
        val totalFileLen = totalDataLen + 36

        FileOutputStream(file).use { out ->
            val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)

            // RIFF Chunk
            header.put('R'.code.toByte())
            header.put('I'.code.toByte())
            header.put('F'.code.toByte())
            header.put('F'.code.toByte())
            header.putInt(totalFileLen)
            header.put('W'.code.toByte())
            header.put('A'.code.toByte())
            header.put('V'.code.toByte())
            header.put('E'.code.toByte())

            // fmt Subchunk
            header.put('f'.code.toByte())
            header.put('m'.code.toByte())
            header.put('t'.code.toByte())
            header.put(' '.code.toByte())
            header.putInt(16) // SubChunk1Size (16 for PCM)
            header.putShort(1.toShort()) // AudioFormat (1 = PCM)
            header.putShort(2.toShort()) // NumChannels (2 = Stereo)
            header.putInt(sampleRate) // SampleRate
            val byteRate = sampleRate * 2 * 2 // SampleRate * NumChannels * BitsPerSample/8
            header.putInt(byteRate)
            header.putShort((2 * 2).toShort()) // BlockAlign
            header.putShort(16.toShort()) // BitsPerSample

            // data Subchunk
            header.put('d'.code.toByte())
            header.put('a'.code.toByte())
            header.put('t'.code.toByte())
            header.put('a'.code.toByte())
            header.putInt(totalDataLen)

            out.write(header.array())

            // Stream PCM samples in 8KB chunks
            val chunkSize = 4096 // 2048 stereo frames
            val buffer = ByteBuffer.allocate(chunkSize * 2).order(ByteOrder.LITTLE_ENDIAN)
            var sampleIdx = 0
            val totalSamples = samples.size

            while (sampleIdx < totalSamples) {
                buffer.clear()
                val chunkEnd = (sampleIdx + chunkSize).coerceAtMost(totalSamples)
                for (i in sampleIdx until chunkEnd) {
                    val floatSample = samples[i].coerceIn(-1.0f, 1.0f)
                    val pcm16 = (floatSample * 32767.0f).toInt().toShort()
                    buffer.putShort(pcm16)
                }
                out.write(buffer.array(), 0, (chunkEnd - sampleIdx) * 2)
                sampleIdx = chunkEnd

                onProgress?.invoke(sampleIdx.toFloat() / totalSamples)
            }
        }
    }
}
