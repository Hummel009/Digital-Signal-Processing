package com.github.hummel.dsp.lab1

import kotlin.math.abs
import kotlin.math.sin

fun generateSineModulator(
	duration: Int = defaultDuration, amplitude: Float = modulatorAmplitude, frequency: Float = modulatorFrequency
): FloatArray {
	return FloatArray(duration * sampleRate) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		amplitude * sin(value)
	}
}

fun generatePulseModulator(
	duration: Int = defaultDuration, amplitude: Float = modulatorAmplitude, frequency: Float = modulatorFrequency
): FloatArray {
	return FloatArray(duration * sampleRate) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		if (value % (2 * PI) / (2 * PI) <= dutyCycle) amplitude else -amplitude
	}
}

fun generateTriangleModulator(
	duration: Int = defaultDuration, amplitude: Float = modulatorAmplitude, frequency: Float = modulatorFrequency
): FloatArray {
	return FloatArray(duration * sampleRate) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		(2 * amplitude / PI) * (abs((value + 3 * PI / 2) % (2 * PI) - PI) - (PI / 2))
	}
}

fun generateSawtoothModulator(
	duration: Int = defaultDuration, amplitude: Float = modulatorAmplitude, frequency: Float = modulatorFrequency
): FloatArray {
	return FloatArray(duration * sampleRate) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		(amplitude / PI) * ((value + PI) % (2 * PI) - PI)
	}
}