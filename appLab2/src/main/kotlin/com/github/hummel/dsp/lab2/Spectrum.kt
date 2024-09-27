package com.github.hummel.dsp.lab2

fun decomposeSignal(fftResult: Array<Complex>): Pair<FloatArray, FloatArray> {
	val amplitude = FloatArray(fftResult.size)
	val phase = FloatArray(fftResult.size)

	for (i in fftResult.indices) {
		amplitude[i] = fftResult[i].magnitude
		phase[i] = fftResult[i].phase
	}
	return Pair(amplitude, phase)
}