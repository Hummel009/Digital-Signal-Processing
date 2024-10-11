@file:Suppress("unused")

package com.github.hummel.dsp.lab2

import kotlin.math.abs
import kotlin.math.sin

fun generateSineWave(
	s: Int = samples, amplitude: Float = defaultAmplitude, frequency: Float = defaultFrequency
): FloatArray {
	return FloatArray(s) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		amplitude * sin(value)
	}
}

fun generatePulseWave(
	amplitude: Float = defaultAmplitude, frequency: Float = defaultFrequency
): FloatArray {
	return FloatArray(samples) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		if (value % (2 * PI) / (2 * PI) <= dutyCycle) amplitude else -amplitude
	}
}

fun generateTriangleWave(
	amplitude: Float = defaultAmplitude, frequency: Float = defaultFrequency
): FloatArray {
	return FloatArray(samples) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		(2 * amplitude / PI) * (abs((value + 3 * PI / 2) % (2 * PI) - PI) - (PI / 2))
	}
}

fun generateSawtoothWave(
	amplitude: Float = defaultAmplitude, frequency: Float = defaultFrequency
): FloatArray {
	return FloatArray(samples) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		(amplitude / PI) * ((value + PI) % (2 * PI) - PI)
	}
}

fun generateNoise(
	amplitude: Float = defaultAmplitude
): FloatArray {
	return FloatArray(samples) {
		amplitude * (Math.random().toFloat() * 2 - 1)
	}
}