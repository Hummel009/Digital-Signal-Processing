package com.github.hummel.dsp.lab1

import kotlin.math.abs
import kotlin.math.sin

fun modulateFrequencySineWave(modulator: FloatArray): FloatArray {
	return FloatArray(samples) { n ->
		val modulatedFrequency = frequency * (1 + modulator[n])
		amplitude * sin(2 * PI * modulatedFrequency * n / sampleRate + phase)
	}
}

fun modulateFrequencyPulseWave(): FloatArray {
	val res = FloatArray(samples) { 0.0f }

	var f = phase

	var i = 0;
	for (n in 0..sampleRate.toInt()) {
		res[i] = if (f % (2 * PI) / (2 * PI) <= dutyCycle) amplitude else -amplitude
		val sawtooth = (amplitude / PI) * ((2 * PI * modulatorFrequency * n / sampleRate + phase + PI) % (2 * PI) - PI)
		f += 1 / sampleRate * (2 * PI * frequency) * (1 + sawtooth)
		i++
	}

	return res
}

fun modulateFrequencyTriangleWave(modulator: FloatArray): FloatArray {
	return FloatArray(samples) { n ->
		val modulatedFrequency = frequency * (1 + modulator[n])
		val modValue = (2 * PI * modulatedFrequency * n / sampleRate + phase + 3 * PI / 2) % (2 * PI)
		(2 * amplitude / PI) * (abs(modValue - PI) - (PI / 2))
	}
}

fun modulateFrequencySawtoothWave(modulator: FloatArray): FloatArray {
	return FloatArray(samples) { 0.0f }
}