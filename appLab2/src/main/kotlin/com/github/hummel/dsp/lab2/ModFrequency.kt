package com.github.hummel.dsp.lab2

import kotlin.math.abs
import kotlin.math.sin

fun modulateFrequencySineWave(modulator: FloatArray): FloatArray {
	return FloatArray(samples) { n ->
		val modulatedFrequency = frequency * (1 + modulator[n])
		amplitude * sin(2 * PI * modulatedFrequency * n / sampleRate + phase)
	}
}

fun modulateFrequencyPulseWave(modulator: FloatArray): FloatArray {
	return FloatArray(samples) { n ->
		val modulatedFrequency = frequency * (1 + modulator[n])
		val modValue = (2 * PI * modulatedFrequency * n / sampleRate + phase) % (2 * PI)
		if (modValue / (2 * PI) <= dutyCycle) amplitude else -amplitude
	}
}

fun modulateFrequencyTriangleWave(modulator: FloatArray): FloatArray {
	return FloatArray(samples) { n ->
		val modulatedFrequency = frequency * (1 + modulator[n])
		val modValue = (2 * PI * modulatedFrequency * n / sampleRate + phase + 3 * PI / 2) % (2 * PI)
		(2 * amplitude / PI) * (abs(modValue - PI) - (PI / 2))
	}
}

fun modulateFrequencySawtoothWave(modulator: FloatArray): FloatArray {
	return FloatArray(samples) { n ->
		val modulatedFrequency = frequency * (1 + modulator[n])
		val modValue = (2 * PI * modulatedFrequency * n / sampleRate + phase + PI) % (2 * PI)
		(amplitude / PI) * (modValue - PI)
	}
}