package com.github.hummel.dsp.lab2

fun computeAmplitudeSpectrum(spectrum: Array<Complex>): FloatArray {
	return FloatArray(spectrum.size) { k ->
		2 * spectrum[k].magnitude / spectrum.size
	}
}

fun computePhaseSpectrum(spectrum: Array<Complex>): FloatArray {
	return FloatArray(spectrum.size) { k ->
		spectrum[k].phase
	}
}