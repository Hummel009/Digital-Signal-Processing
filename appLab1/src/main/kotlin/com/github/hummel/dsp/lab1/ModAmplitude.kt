package com.github.hummel.dsp.lab1

fun modulateAmplitude(signal: FloatArray, modulator: FloatArray): FloatArray {
	require(signal.size == modulator.size) { "Arrays must be of the same length" }

	return FloatArray(signal.size) { n ->
		signal[n] * (1 + modulator[n])
	}
}