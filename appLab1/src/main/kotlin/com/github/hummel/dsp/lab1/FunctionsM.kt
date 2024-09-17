package com.github.hummel.dsp.lab1

import kotlin.math.abs
import kotlin.math.sin

fun generateSineModulator(): FloatArray {
	return FloatArray(samples) { n ->
		val value = 2 * PI * modulatorFrequency + phase * n / sampleRate
		modulatorAmplitude * sin(value)
	}
}

fun generatePulseModulator(): FloatArray {
	return FloatArray(samples) { n ->
		val value = 2 * PI * modulatorFrequency * n / sampleRate + phase
		if (value % (2 * PI) / (2 * PI) <= dutyCycle) modulatorAmplitude else -modulatorAmplitude
	}
}

fun generateTriangleModulator(): FloatArray {
	return FloatArray(samples) { n ->
		val value = 2 * PI * modulatorFrequency * n / sampleRate + phase
		(2 * modulatorAmplitude / PI) * (abs((value + 3 * PI / 2) % (2 * PI) - PI) - (PI / 2))
	}
}

fun generateSawtoothModulator(): FloatArray {
	return FloatArray(samples) { n ->
		val value = 2 * PI * modulatorFrequency * n / sampleRate + phase
		(modulatorAmplitude / PI) * ((value + PI) % (2 * PI) - PI)
	}
}