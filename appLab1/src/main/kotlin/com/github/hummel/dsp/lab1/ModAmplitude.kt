package com.github.hummel.dsp.lab1

fun modulateAmplitude(signal: FloatArray, modulator: FloatArray): FloatArray {
	return FloatArray(signal.size) { n ->
		signal[n] * (1 + modulator[n])
	}
}