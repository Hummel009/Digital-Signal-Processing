package com.github.hummel.dsp.lab2

fun lowPassFilter(transformed: Array<Complex>, cutoffFrequency: Float): Array<Complex> {
	val n = transformed.size
	val result = Array<Complex>(n) { Complex(0.0f, 0.0f) }

	for (i in 0 until n) {
		val f = (i * defaultFrequency) / n
		result[i] = if (f > cutoffFrequency) Complex(0.0f, 0.0f) else transformed[i]
	}

	return result
}

fun highPassFilter(transformed: Array<Complex>, cutoffFrequency: Float): Array<Complex> {
	val n = transformed.size
	val result = Array<Complex>(n) { Complex(0.0f, 0.0f) }

	for (i in 0 until n) {
		val f = (i * defaultFrequency) / n
		result[i] = if (f < cutoffFrequency) Complex(0.0f, 0.0f) else transformed[i]
	}

	return result
}

fun bandPassFilter(transformed: Array<Complex>, cutoffFrequencyL: Float, cutoffFrequencyH: Float): Array<Complex> {
	val n = transformed.size
	val result = Array<Complex>(n) { Complex(0.0f, 0.0f) }

	for (i in 0 until n) {
		val f = (i * defaultFrequency) / n
		result[i] = if (f < cutoffFrequencyL || f > cutoffFrequencyH) Complex(0.0f, 0.0f) else transformed[i]
	}

	return result
}