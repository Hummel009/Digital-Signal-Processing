@file:Suppress("unused")

package com.github.hummel.dsp.lab2

import kotlin.math.abs
import kotlin.math.sin

fun generateSineWave(
	duration: Int = defaultDuration, amplitude: Float = defaultAmplitude, frequency: Float
): FloatArray {
	return FloatArray(duration * sampleRate) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		amplitude * sin(value)
	}
}

fun generatePulseWave(
	duration: Int = defaultDuration, amplitude: Float = defaultAmplitude, frequency: Float
): FloatArray {
	return FloatArray(duration * sampleRate) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		if (value % (2 * PI) / (2 * PI) <= dutyCycle) amplitude else -amplitude
	}
}

fun generateTriangleWave(
	duration: Int = defaultDuration, amplitude: Float = defaultAmplitude, frequency: Float
): FloatArray {
	return FloatArray(duration * sampleRate) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		(2 * amplitude / PI) * (abs((value + 3 * PI / 2) % (2 * PI) - PI) - (PI / 2))
	}
}

fun generateSawtoothWave(
	duration: Int = defaultDuration, amplitude: Float = defaultAmplitude, frequency: Float
): FloatArray {
	return FloatArray(duration * sampleRate) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		(amplitude / PI) * ((value + PI) % (2 * PI) - PI)
	}
}

fun generateNoise(
	duration: Int = defaultDuration, amplitude: Float = defaultAmplitude
): FloatArray {
	return FloatArray(duration * sampleRate) {
		amplitude * (Math.random().toFloat() * 2 - 1)
	}
}