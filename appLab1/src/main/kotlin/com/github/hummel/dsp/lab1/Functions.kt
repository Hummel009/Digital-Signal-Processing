package com.github.hummel.dsp.lab1

import kotlin.math.abs
import kotlin.math.sin

fun generateSineWave(): FloatArray {
	return FloatArray(samples) { n ->
		val value = 2 * PI * frequency + phase * n / sampleRate
		amplitude * sin(value)
	}
}

fun generatePulseWave(): FloatArray {
	return FloatArray(samples) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		if (value % (2 * PI) / (2 * PI) <= dutyCycle) amplitude else -amplitude
	}
}

fun generateTriangleWave(): FloatArray {
	return FloatArray(samples) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		(2 * amplitude / PI) * (abs((value + 3 * PI / 2) % (2 * PI) - PI) - (PI / 2))
	}
}

fun generateSawtoothWave(): FloatArray {
	return FloatArray(samples) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		(amplitude / PI) * ((value + PI) % (2 * PI) - PI)
	}
}

fun generateNoise(): FloatArray {
	return FloatArray(samples) {
		amplitude * (Math.random().toFloat() * 2 - 1)
	}
}