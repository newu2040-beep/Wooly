package com.example

import com.example.audio.dsp.AudioProcessor
import com.example.audio.dsp.BiquadFilter
import com.example.audio.engine.AudioExporter
import com.example.data.model.AudioStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testBiquadLowPassFilter() {
    val filter = BiquadFilter()
    filter.setLowPass(1000f, 44100f)
    var output = 0f
    for (i in 0 until 100) {
      output = filter.process(1.0f)
    }
    assertTrue(!output.isNaN())
    assertTrue(!output.isInfinite())
  }

  @Test
  fun testAudioProcessorBuffer() {
    val processor = AudioProcessor(44100)
    val inputSamples = FloatArray(44100 * 2) { 0.1f } // 1 second stereo
    val params = AudioStyle.LO_FI.toDefaultParams()

    var progressReported = 0f
    val processed = processor.processBuffer(inputSamples, params) { p ->
      progressReported = p
    }

    assertNotNull(processed)
    assertTrue(processed.isNotEmpty())
    assertTrue(progressReported > 0.9f)
  }

  @Test
  fun testExportEstimateCalculation() {
    val params = AudioStyle.VINYL.toDefaultParams()
    val estimate = AudioExporter.calculateEstimate(60_000L, params)

    assertEquals("WAV", estimate.format)
    assertTrue(estimate.estimatedDurationMs > 0)
    assertTrue(estimate.estimatedSizeBytes > 1024)
  }
}
