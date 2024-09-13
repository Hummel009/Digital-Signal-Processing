package com.github.hummel.dsp.lab1

import kotlin.math.abs
import kotlin.math.sin

fun modulateFrequency(signal: FloatArray, modulator: FloatArray, type: String): FloatArray {
	require(signal.size == modulator.size) { "Arrays must be of the same length" }

	return when (type) {
		"sine" -> FloatArray(signal.size) { n ->
			val modulatedFrequency = signal[n] + 50.0f * modulator[n]
			sin(2 * PI * modulatedFrequency * n / sampleRate + phase)
		}

		"pulse" -> FloatArray(signal.size) { n ->
			val modulatedFrequency = signal[n] + 50.0f * modulator[n]
			val modValue = (2 * PI * modulatedFrequency * n / sampleRate + phase) % (2 * PI)

			if (modValue / (2 * PI) <= 0.5) amplitude else -amplitude
		}

		"triangle" -> FloatArray(signal.size) { n ->
			val modulatedFrequency = signal[n] + 50.0f * modulator[n]
			val modValue = (2 * PI * modulatedFrequency * n / sampleRate + phase + 3 * PI / 2) % (2 * PI)

			(2 * amplitude / PI) * (abs(modValue - PI) - (PI / 2))
		}

		"sawtooth" -> FloatArray(signal.size) { n ->
			val modulatedFrequency = signal[n] + 50.0f * modulator[n]
			val modValue = (2 * PI * modulatedFrequency * n / sampleRate + phase) % (2 * PI)

			(amplitude / PI) * (modValue - PI)
		}

		else -> FloatArray(signal.size) { 0f }
	}
}