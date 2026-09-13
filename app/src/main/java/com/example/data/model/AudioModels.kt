package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The 12 creative audio styles supported by WOOLY.
 */
enum class AudioStyle(
    val displayName: String,
    val subtitle: String,
    val description: String,
    val iconName: String
) {
    NORMAL(
        displayName = "Normal",
        subtitle = "Pure studio sound",
        description = "Original audio without effects, studio master fidelity.",
        iconName = "music_note"
    ),
    LO_FI(
        displayName = "Lo-fi",
        subtitle = "Chill beats. Soft vibes. Better days.",
        description = "Warm low-pass filter, subtle vinyl warmth, tape wow/flutter, and relaxed tempo.",
        iconName = "stars"
    ),
    REVERB(
        displayName = "Reverb",
        subtitle = "Lost in a cathedral",
        description = "Expansive acoustic reverberation with rich decay and spacious stereo reflections.",
        iconName = "graphic_eq"
    ),
    SLOWED_REVERB(
        displayName = "Slowed + Reverb",
        subtitle = "Midnight drift & euphoria",
        description = "0.85x slowed tempo, pitch drop, lush atmospheric reverb, and smoothed highs.",
        iconName = "slow_motion_video"
    ),
    VINYL(
        displayName = "Vinyl",
        subtitle = "Analog warmth & needle dust",
        description = "Authentic turntable crackle, surface noise, warm mid boost, and subtle pitch wow.",
        iconName = "album"
    ),
    SURROUND_3D(
        displayName = "3D Surround",
        subtitle = "Binaural spatial immersion",
        description = "Interaural Haas time-delay, dimensional cross-feed, and immersive spatial widening.",
        iconName = "surround_sound"
    ),
    AMBIENT(
        displayName = "Ambient",
        subtitle = "Weightless dreamscape",
        description = "90% wet atmospheric reverb, super-wide stereo field, and soft high-cut absorption.",
        iconName = "cloud"
    ),
    ECHO(
        displayName = "Echo",
        subtitle = "Rhythmic canyon repeats",
        description = "Ping-pong stereo delay lines with smooth feedback loops and filter decay.",
        iconName = "repeat"
    ),
    NIGHTCORE(
        displayName = "Nightcore",
        subtitle = "Hyper-energy speed up",
        description = "1.28x accelerated tempo, pitched-up vocals, punchy bass, and crisp transient highs.",
        iconName = "bolt"
    ),
    BASS_BOOST(
        displayName = "Bass Boost",
        subtitle = "Subwoofer heavy punch",
        description = "Deep +10dB low-shelf resonant boost at 80Hz with soft-clipping saturation.",
        iconName = "speaker"
    ),
    DREAMY(
        displayName = "Dreamy",
        subtitle = "Shimmering ethereal haze",
        description = "Dual modulated chorus delay, ethereal shimmer reverb, and air sparkle frequencies.",
        iconName = "auto_awesome"
    ),
    CUSTOM(
        displayName = "Custom",
        subtitle = "Craft your signature vibe",
        description = "Full manual control over speed, pitch, reverb, filters, vinyl, delay, and stereo width.",
        iconName = "tune"
    );

    fun toDefaultParams(): CustomAudioParams {
        return when (this) {
            NORMAL -> CustomAudioParams()
            LO_FI -> CustomAudioParams(
                speed = 0.94f,
                reverbWet = 0.35f,
                reverbDecay = 0.5f,
                bassGainDb = 3.5f,
                trebleGainDb = -4.0f,
                stereoWidth = 1.15f,
                vinylCrackle = 0.38f,
                lowPassHz = 3400f,
                highPassHz = 80f,
                warmthSaturation = 0.30f
            )
            REVERB -> CustomAudioParams(
                reverbWet = 0.65f,
                reverbDecay = 0.75f,
                stereoWidth = 1.4f,
                bassGainDb = 1.0f,
                trebleGainDb = 1.5f,
                lowPassHz = 16000f
            )
            SLOWED_REVERB -> CustomAudioParams(
                speed = 0.85f,
                reverbWet = 0.68f,
                reverbDecay = 0.78f,
                lowPassHz = 8500f,
                highPassHz = 40f,
                bassGainDb = 3.0f,
                trebleGainDb = -1.5f,
                stereoWidth = 1.35f,
                warmthSaturation = 0.20f
            )
            VINYL -> CustomAudioParams(
                vinylCrackle = 0.55f,
                lowPassHz = 7000f,
                highPassHz = 60f,
                bassGainDb = 2.5f,
                trebleGainDb = -2.0f,
                reverbWet = 0.15f,
                reverbDecay = 0.35f,
                warmthSaturation = 0.35f
            )
            SURROUND_3D -> CustomAudioParams(
                spatial3D = 0.85f,
                stereoWidth = 1.8f,
                delayMs = 22,
                reverbWet = 0.25f,
                reverbDecay = 0.4f
            )
            AMBIENT -> CustomAudioParams(
                speed = 0.88f,
                reverbWet = 0.82f,
                reverbDecay = 0.90f,
                stereoWidth = 1.7f,
                lowPassHz = 9000f,
                trebleGainDb = -2.0f
            )
            ECHO -> CustomAudioParams(
                delayMs = 320,
                echoFeedback = 0.52f,
                stereoWidth = 1.4f,
                reverbWet = 0.20f
            )
            NIGHTCORE -> CustomAudioParams(
                speed = 1.28f,
                bassGainDb = 4.0f,
                trebleGainDb = 3.0f,
                lowPassHz = 20000f,
                reverbWet = 0.12f
            )
            BASS_BOOST -> CustomAudioParams(
                bassGainDb = 9.5f,
                trebleGainDb = 0.5f,
                warmthSaturation = 0.25f,
                lowPassHz = 19000f
            )
            DREAMY -> CustomAudioParams(
                speed = 0.95f,
                reverbWet = 0.58f,
                reverbDecay = 0.72f,
                delayMs = 180,
                echoFeedback = 0.35f,
                stereoWidth = 1.55f,
                trebleGainDb = 3.0f
            )
            CUSTOM -> CustomAudioParams()
        }
    }
}

/**
 * Advanced audio processing parameters.
 */
data class CustomAudioParams(
    val speed: Float = 1.0f,               // 0.5f to 2.0f
    val pitchSemis: Float = 0.0f,          // -12f to +12f
    val reverbWet: Float = 0.0f,           // 0.0f to 1.0f
    val reverbDecay: Float = 0.5f,         // 0.1f to 0.98f
    val delayMs: Int = 0,                  // 0 to 800 ms
    val echoFeedback: Float = 0.0f,        // 0.0f to 0.85f
    val bassGainDb: Float = 0.0f,          // -12.0f to +15.0f
    val trebleGainDb: Float = 0.0f,        // -12.0f to +15.0f
    val stereoWidth: Float = 1.0f,         // 0.0f (mono) to 2.0f (super wide)
    val spatial3D: Float = 0.0f,           // 0.0f to 1.0f
    val vinylCrackle: Float = 0.0f,        // 0.0f to 1.0f
    val lowPassHz: Float = 20000f,         // 500Hz to 20000Hz
    val highPassHz: Float = 20f,           // 20Hz to 2000Hz
    val warmthSaturation: Float = 0.0f,    // 0.0f to 1.0f
    val volume: Float = 1.0f,              // 0.1f to 2.0f
    val fadeInSec: Float = 0.0f,           // 0.0f to 5.0f
    val fadeOutSec: Float = 0.0f,          // 0.0f to 5.0f
    val outputFormat: String = "WAV",      // "WAV", "M4A", "FLAC"
    val bitrateKbps: Int = 320,            // 128, 192, 256, 320
    val sampleRate: Int = 44100            // 44100 or 48000
)

/**
 * Room entity representing an imported or processed audio track.
 */
@Entity(tableName = "audio_tracks")
data class AudioTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val artist: String = "WOOLY Sound",
    val originalFilePath: String,
    val processedFilePath: String? = null,
    val durationMs: Long = 0,
    val fileSizeBytes: Long = 0,
    val styleName: String = "Normal",
    val isSaved: Boolean = false,
    val isDemoSample: Boolean = false,
    val format: String = "WAV",
    val bitrateKbps: Int = 320,
    val sampleRate: Int = 44100,
    val createdAt: Long = System.currentTimeMillis()
)
