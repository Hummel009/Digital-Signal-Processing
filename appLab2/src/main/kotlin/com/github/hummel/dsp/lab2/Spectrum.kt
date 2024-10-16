package com.github.hummel.dsp.lab2

fun decomposeSignal(spectrum: Array<Complex>): Pair<FloatArray, FloatArray> {
	return spectrum.map {
		it.magnitude * 2 / spectrum.size
	}.toFloatArray() to spectrum.map {
		it.phase
	}.toFloatArray()
}