package com.github.hummel.dsp.lab1

import kotlin.math.abs
import kotlin.math.sin

fun generateSineModulator(): FloatArray {
	return FloatArray(samples) { n ->
		modulatorAmplitude * sin(2 * PI * modulatorFrequency * n / sampleRate + phase)
	}
}

fun generatePulseModulator(): FloatArray {
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * modulatorFrequency * n / sampleRate + phase) % (2 * PI)
		if (modValue / (2 * PI) <= dutyCycle) modulatorAmplitude else -modulatorAmplitude
	}
}

fun generateTriangleModulator(): FloatArray {
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * modulatorFrequency * n / sampleRate + phase + 3 * PI / 2) % (2 * PI)
		(2 * modulatorAmplitude / PI) * (abs(modValue - PI) - (PI / 2))
	}
}

fun generateSawtoothModulator(): FloatArray {
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * modulatorFrequency * n / sampleRate + phase + PI) % (2 * PI)
		(modulatorAmplitude / PI) * (modValue - PI)
	}
}