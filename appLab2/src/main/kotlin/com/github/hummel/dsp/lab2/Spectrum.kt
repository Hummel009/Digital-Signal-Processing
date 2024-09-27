package com.github.hummel.dsp.lab2

fun decomposeSignal(fftResult: Array<Complex>): Pair<FloatArray, FloatArray> {
	return fftResult.map {
		it.magnitude
	}.toFloatArray() to fftResult.map {
		it.phase
	}.toFloatArray()
}