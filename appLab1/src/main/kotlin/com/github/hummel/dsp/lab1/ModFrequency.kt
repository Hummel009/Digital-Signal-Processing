package com.github.hummel.dsp.lab1

import kotlin.math.abs
import kotlin.math.sin

fun modulateFrequencySineWave(modulator: FloatArray): FloatArray {
	var value = 0.0f

	return FloatArray(samples) { n ->
		val res = defaultAmplitude * sin(value)
		value += 2 * PI * defaultFrequency * (1 + modulator[n]) / sampleRate + phase
		res
	}
}

fun modulateFrequencyPulseWave(modulator: FloatArray): FloatArray {
	var value = 0.0f

	return FloatArray(samples) { n ->
		val res = if (value % (2 * PI) / (2 * PI) <= dutyCycle) defaultAmplitude else -defaultAmplitude
		value += 2 * PI * defaultFrequency * (1 + modulator[n]) / sampleRate + phase
		res
	}
}

fun modulateFrequencyTriangleWave(modulator: FloatArray): FloatArray {
	var value = 0.0f

	return FloatArray(samples) { n ->
		val res = (2 * defaultAmplitude / PI) * (abs((value + 3 * PI / 2) % (2 * PI) - PI) - (PI / 2))
		value += 2 * PI * defaultFrequency * (1 + modulator[n]) / sampleRate + phase
		res
	}
}

fun modulateFrequencySawtoothWave(modulator: FloatArray): FloatArray {
	var value = 0.0f

	return FloatArray(samples) { n ->
		val res = (defaultAmplitude / PI) * ((value + PI) % (2 * PI) - PI)
		value += 2 * PI * defaultFrequency * (1 + modulator[n]) / sampleRate + phase
		res
	}
}