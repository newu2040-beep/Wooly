package com.example.audio.engine

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class DecodedAudio(
    val samples: FloatArray, // Interleaved stereo [L0, R0, L1, R1, ...]
    val sampleRate: Int,
    val durationMs: Long,
    val title: String,
    val artist: String
)

object AudioDecoder {

    suspend fun decodeAudio(
        context: Context,
        uri: Uri,
        maxDurationMs: Long = 600_000L // 10 minutes max buffer to protect mobile memory
    ): DecodedAudio = withContext(Dispatchers.IO) {
        val extractor = MediaExtractor()
        val retriever = MediaMetadataRetriever()

        try {
            extractor.setDataSource(context, uri, null)
            retriever.setDataSource(context, uri)
        } catch (e: Exception) {
            // Try file path fallback if direct uri failed
            val path = uri.path
            if (path != null && File(path).exists()) {
                extractor.setDataSource(path)
                retriever.setDataSource(path)
            } else {
                throw e
            }
        }

        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            ?: getFileNameFromUri(context, uri).substringBeforeLast(".")
        val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            ?: "Unknown Artist"
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val fullDurationMs = durationStr?.toLongOrNull() ?: 0L
        retriever.release()

        // Find audio track
        var audioTrackIndex = -1
        var format: MediaFormat? = null
        for (i in 0 until extractor.trackCount) {
            val trackFormat = extractor.getTrackFormat(i)
            val mime = trackFormat.getString(MediaFormat.KEY_MIME) ?: ""
            if (mime.startsWith("audio/")) {
                audioTrackIndex = i
                format = trackFormat
                break
            }
        }

        if (audioTrackIndex < 0 || format == null) {
            extractor.release()
            throw IllegalArgumentException("No valid audio track found in file.")
        }

        extractor.selectTrack(audioTrackIndex)
        val mime = format.getString(MediaFormat.KEY_MIME) ?: ""
        val sampleRate = if (format.containsKey(MediaFormat.KEY_SAMPLE_RATE)) {
            format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
        } else 44100
        val channelCount = if (format.containsKey(MediaFormat.KEY_CHANNEL_COUNT)) {
            format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
        } else 2

        val codec = MediaCodec.createDecoderByType(mime)
        codec.configure(format, null, null, 0)
        codec.start()

        val pcmChunks = mutableListOf<FloatArray>()
        var totalStereoSamples = 0
        val bufferInfo = MediaCodec.BufferInfo()
        var isExtractorEOS = false
        var isDecoderEOS = false
        val timeoutUs = 10_000L

        while (!isDecoderEOS) {
            if (!isExtractorEOS) {
                val inputIndex = codec.dequeueInputBuffer(timeoutUs)
                if (inputIndex >= 0) {
                    val inputBuffer = codec.getInputBuffer(inputIndex)
                    if (inputBuffer != null) {
                        val sampleSize = extractor.readSampleData(inputBuffer, 0)
                        if (sampleSize < 0) {
                            codec.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                            isExtractorEOS = true
                        } else {
                            val presentationTimeUs = extractor.sampleTime
                            codec.queueInputBuffer(inputIndex, 0, sampleSize, presentationTimeUs, 0)
                            extractor.advance()
                        }
                    }
                }
            }

            val outputIndex = codec.dequeueOutputBuffer(bufferInfo, timeoutUs)
            if (outputIndex >= 0) {
                val outputBuffer = codec.getOutputBuffer(outputIndex)
                if (outputBuffer != null && bufferInfo.size > 0) {
                    outputBuffer.position(bufferInfo.offset)
                    outputBuffer.limit(bufferInfo.offset + bufferInfo.size)
                    outputBuffer.order(ByteOrder.LITTLE_ENDIAN)

                    val shortBuf = outputBuffer.asShortBuffer()
                    val shortsCount = shortBuf.remaining()
                    val chunkFrames = shortsCount / channelCount
                    val stereoChunk = FloatArray(chunkFrames * 2)

                    if (channelCount == 1) {
                        for (f in 0 until chunkFrames) {
                            val sample = shortBuf.get() / 32768.0f
                            stereoChunk[f * 2] = sample
                            stereoChunk[f * 2 + 1] = sample
                        }
                    } else {
                        for (f in 0 until chunkFrames) {
                            stereoChunk[f * 2] = shortBuf.get() / 32768.0f
                            stereoChunk[f * 2 + 1] = shortBuf.get() / 32768.0f
                            // Skip extra channels if 5.1 or 7.1
                            for (c in 2 until channelCount) {
                                shortBuf.get()
                            }
                        }
                    }

                    pcmChunks.add(stereoChunk)
                    totalStereoSamples += stereoChunk.size

                    // Check duration limit
                    val currentDurationMs = (totalStereoSamples / 2 * 1000L) / sampleRate
                    if (currentDurationMs >= maxDurationMs) {
                        isDecoderEOS = true
                    }
                }

                codec.releaseOutputBuffer(outputIndex, false)
                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    isDecoderEOS = true
                }
            }
        }

        codec.stop()
        codec.release()
        extractor.release()

        // Combine into single contiguous stereo buffer
        val allSamples = FloatArray(totalStereoSamples)
        var offset = 0
        for (chunk in pcmChunks) {
            System.arraycopy(chunk, 0, allSamples, offset, chunk.size)
            offset += chunk.size
        }

        val calculatedDurationMs = (totalStereoSamples / 2 * 1000L) / sampleRate
        val finalDurationMs = if (fullDurationMs > 0) fullDurationMs else calculatedDurationMs

        DecodedAudio(
            samples = allSamples,
            sampleRate = sampleRate,
            durationMs = finalDurationMs,
            title = title,
            artist = artist
        )
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) return it.getString(nameIndex)
                }
            }
            uri.lastPathSegment ?: "Audio_Track"
        } catch (e: Exception) {
            "Audio_Track"
        }
    }
}
