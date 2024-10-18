package com.github.hummel.dsp.lab1

import kotlin.math.abs
import kotlin.math.sin

fun generateSineModulator(
	amplitude: Float = modulatorAmplitude, frequency: Float = modulatorFrequency
): FloatArray {
	return FloatArray(duration * sampleRate) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		amplitude * sin(value)
	}
}

fun generatePulseModulator(
	amplitude: Float = modulatorAmplitude, frequency: Float = modulatorFrequency
): FloatArray {
	return FloatArray(duration * sampleRate) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		if (value % (2 * PI) / (2 * PI) <= dutyCycle) amplitude else -amplitude
	}
}

fun generateTriangleModulator(
	amplitude: Float = modulatorAmplitude, frequency: Float = modulatorFrequency
): FloatArray {
	return FloatArray(duration * sampleRate) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		(2 * amplitude / PI) * (abs((value + 3 * PI / 2) % (2 * PI) - PI) - (PI / 2))
	}
}

fun generateSawtoothModulator(
	amplitude: Float = modulatorAmplitude, frequency: Float = modulatorFrequency
): FloatArray {
	return FloatArray(duration * sampleRate) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		(amplitude / PI) * ((value + PI) % (2 * PI) - PI)
	}
}