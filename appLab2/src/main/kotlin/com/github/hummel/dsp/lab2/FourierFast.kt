package com.github.hummel.dsp.lab2

import kotlin.math.cos
import kotlin.math.sin

fun fft(signalValues: FloatArray): Array<Complex> = iteration(adaptLength(signalValues), false)

fun ifft(spectrum: Array<Complex>): FloatArray = iteration(spectrum, true).map { it.real }.toFloatArray()

private fun iteration(data: Array<Complex>, invert: Boolean): Array<Complex> {
	val N = data.size
	if (N == 1) return data

	val result = Array(N) { Complex.ZERO }
	val even = Array(N / 2) { Complex.ZERO }
	val odd = Array(N / 2) { Complex.ZERO }

	for (i in 0 until N step 2) {
		even[i / 2] = data[i]
		odd[i / 2] = data[i + 1]
	}

	val evenRes = iteration(even, invert)
	val oddRes = iteration(odd, invert)

	val ang = 2 * PI / N * if (invert) -1 else 1
	var w = Complex(1.0f, 0.0f)
	val wn = Complex(cos(ang), sin(ang))

	for (i in 0 until N / 2) {
		result[i] = evenRes[i] + w * oddRes[i]
		result[i + N / 2] = evenRes[i] - w * oddRes[i]
		if (invert) {
			result[i] /= 2.0f
			result[i + N / 2] /= 2.0f
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