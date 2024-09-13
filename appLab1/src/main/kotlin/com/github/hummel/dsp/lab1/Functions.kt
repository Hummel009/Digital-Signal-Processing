package com.github.hummel.dsp.lab1

import kotlin.math.abs
import kotlin.math.sin

const val amplitude: Float = 0.5f //A
var frequency: Float = 880.0f //f

fun generateSineWave(): FloatArray {
	return FloatArray(samples) { n ->
		amplitude * sin(2 * PI * frequency * n / sampleRate + phase)
	}
}

fun generatePulseWave(): FloatArray {
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * frequency * n / sampleRate + phase) % (2 * PI)
		if (modValue / (2 * PI) <= dutyCycle) amplitude else -amplitude
	}
}

fun generateTriangleWave(): FloatArray {
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * frequency * n / sampleRate + phase + 3 * PI / 2) % (2 * PI)
		(2 * amplitude / PI) * (abs(modValue - PI) - (PI / 2))
	}
}

fun generateSawtoothWave(): FloatArray {
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * frequency * n / sampleRate + phase + PI) % (2 * PI)
		(amplitude / PI) * (modValue - PI)
	}
}

fun generateNoise(): FloatArray {
	return FloatArray(samples) {
		amplitude * (Math.random().toFloat() * 2 - 1)
	}
}