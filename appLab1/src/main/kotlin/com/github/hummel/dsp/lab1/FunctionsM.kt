package com.github.hummel.dsp.lab1

import kotlin.math.abs
import kotlin.math.sin

fun generateSineModulator(
	modAmplitude: Float = modulatorAmplitude, modFrequency: Float = modulatorFrequency
): FloatArray {
	return FloatArray(samples) { n ->
		val value = 2 * PI * modFrequency + phase * n / sampleRate
		modAmplitude * sin(value)
	}
}

fun generatePulseModulator(
	modAmplitude: Float = modulatorAmplitude, modFrequency: Float = modulatorFrequency
): FloatArray {
	return FloatArray(samples) { n ->
		val value = 2 * PI * modFrequency * n / sampleRate + phase
		if (value % (2 * PI) / (2 * PI) <= dutyCycle) modAmplitude else -modAmplitude
	}
}

fun generateTriangleModulator(
	modAmplitude: Float = modulatorAmplitude, modFrequency: Float = modulatorFrequency
): FloatArray {
	return FloatArray(samples) { n ->
		val value = 2 * PI * modFrequency * n / sampleRate + phase
		(2 * modAmplitude / PI) * (abs((value + 3 * PI / 2) % (2 * PI) - PI) - (PI / 2))
	}
}

fun generateSawtoothModulator(
	modAmplitude: Float = modulatorAmplitude, modFrequency: Float = modulatorFrequency
): FloatArray {
	return FloatArray(samples) { n ->
		val value = 2 * PI * modFrequency * n / sampleRate + phase
		(modAmplitude / PI) * ((value + PI) % (2 * PI) - PI)
	}
}