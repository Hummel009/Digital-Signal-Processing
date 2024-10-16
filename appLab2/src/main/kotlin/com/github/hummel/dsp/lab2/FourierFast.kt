package com.github.hummel.dsp.lab2

import kotlin.math.cos
import kotlin.math.sin

fun fft(signalValues: FloatArray): Array<Complex> = fftRecursion(adaptLength(signalValues), false)

fun ifft(spectrum: Array<Complex>): FloatArray = fftRecursion(spectrum, true).map { it.real }.toFloatArray()

private fun fftRecursion(data: Array<Complex>, invert: Boolean): Array<Complex> {
	val n = data.size
	if (n == 1) return data

	val result = Array(n) { Complex.ZERO }
	val even = Array(n / 2) { Complex.ZERO }
	val odd = Array(n / 2) { Complex.ZERO }

	for (i in 0 until n step 2) {
		even[i / 2] = data[i]
		odd[i / 2] = data[i + 1]
	}

	val evenRes = fftRecursion(even, invert)
	val oddRes = fftRecursion(odd, invert)

	val ang = 2 * PI / n * if (invert) -1 else 1
	var w = Complex(1.0f, 0.0f)
	val wn = Complex(cos(ang), sin(ang))

	for (i in 0 until n / 2) {
		result[i] = evenRes[i] + w * oddRes[i]
		result[i + n / 2] = evenRes[i] - w * oddRes[i]
		if (invert) {
			result[i] /= 2.0f
			result[i + n / 2] /= 2.0f
		}
		w *= wn
	}

	return result
}

private fun adaptLength(data: FloatArray): Array<Complex> {
	var lengthNew = 1
	while (lengthNew < data.size) lengthNew *= 2
	val result = Array(lengthNew) { Complex.ZERO }

	for (i in result.indices) {
		if (i < data.size) {
			result[i] = Complex(data[i], 0.0f)
		} else {
			result[i] = Complex.ZERO
		}
	}

	return result
}