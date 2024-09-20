package com.github.hummel.dsp.lab2

fun lowPassFilter(spectrum: FloatArray, cutoffFrequency: Float): FloatArray {
	if (cutoffFrequency == 0.0f) return spectrum

	val n = spectrum.size
	val result = FloatArray(n)

	for (i in 0 until n) {
		val f = (i * defaultFrequency) / n
		result[i] = if (f > cutoffFrequency) 0.0f else spectrum[i]
	}

	return result
}

fun highPassFilter(spectrum: FloatArray, cutoffFrequency: Float): FloatArray {
	if (cutoffFrequency == 0.0f) return spectrum

	val n = spectrum.size
	val result = FloatArray(n)

	for (i in 0 until n) {
		val f = (i * defaultFrequency) / n
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
		val f = (i * defaultFrequency) / n
		result[i] = if (f < cutoffFrequencyL || f > cutoffFrequencyH) 0.0f else spectrum[i]
	}

	return result
}