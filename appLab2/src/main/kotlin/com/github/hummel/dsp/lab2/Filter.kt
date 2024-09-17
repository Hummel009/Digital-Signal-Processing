package com.github.hummel.dsp.lab2

fun lowPassFilter(spectrum: FloatArray, cutoffFrequency: Float): FloatArray {
	if (cutoffFrequency == 0.0f) return spectrum

	val n = spectrum.size
	val result = FloatArray(n)

	for (i in 0 until n) {
		val f = (i * sampleRate) / n
		result[i] = if (f > cutoffFrequency) 0.0f else spectrum[i]
	}

	return result
}

fun highPassFilter(spectrum: FloatArray, cutoffFrequency: Float): FloatArray {
	if (cutoffFrequency == 0.0f) return spectrum

	val n = spectrum.size
	val result = FloatArray(n)

	for (i in 0 until n) {
		val f = (i * sampleRate) / n
		result[i] = if (f < cutoffFrequency) 0.0f else spectrum[i]
	}

	return result
}

fun bandPassFilter(
	spectrum: FloatArray, cutoffFrequencyL: Float, cutoffFrequencyH: Float
): FloatArray {
	if (cutoffFrequencyL == 0.0f && cutoffFrequencyH == 0.0f) return spectrum

	val n = spectrum.size
	val result = FloatArray(n)

	for (i in 0 until n) {
		val f = (i * sampleRate) / n
		result[i] = if (f < cutoffFrequencyL || f > cutoffFrequencyH) 0.0f else spectrum[i]
	}

	return result
}

fun computeAmplitudeSpectrum(spectrum: Array<Complex>): FloatArray {
	val n = spectrum.size
	val amplitudeSpectrum = FloatArray(n)

	for (k in 0 until n) {
		amplitudeSpectrum[k] = 2 * spectrum[k].magnitude / n
	}

	return amplitudeSpectrum
}

fun computePhaseSpectrum(spectrum: Array<Complex>): FloatArray {
	val n = spectrum.size
	val phaseSpectrum = FloatArray(n)

	for (k in 0 until n) {
		phaseSpectrum[k] = spectrum[k].phase
	}

	return phaseSpectrum
}