package com.github.hummel.dsp.lab2

import kotlin.math.cos
import kotlin.math.sin

fun dft(signal: FloatArray): Array<Complex> {
	val n = signal.size
	val spectrum = Array(n) { Complex.ZERO }

	for (i in 0 until n) {
		spectrum[i] = Complex.ZERO

		val angle = 2 * PI * i / n

		for (j in 0 until n) {
			val complex = Complex(cos(angle * j), -sin(angle * j))
			spectrum[i] += complex * signal[j]
		}
	}

	return spectrum
}

fun idft(spectrum: Array<Complex>): FloatArray {
	val n = spectrum.size
	val result = FloatArray(n)

	for (i in 0 until n) {
		var sum = Complex.ZERO

		val angle = 2 * PI * i / n

		for (j in 0 until n) {
			val complex = Complex(cos(angle * j), sin(angle * j))
			sum += complex * spectrum[j]
		}

		result[i] = sum.real / n
	}

	return result
}