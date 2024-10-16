package com.github.hummel.dsp.lab2

import kotlin.math.cos
import kotlin.math.sin

fun dft(signal: FloatArray): Array<Complex> {
	val n = signal.size
	val spectrum = Array(n) { Complex.ZERO }

	for (i in 0 until n) {
		spectrum[i] = Complex.ZERO
		for (j in 0 until n) {
			val angle = 2 * PI * i * j / n
			val complex = Complex(cos(angle), -sin(angle))
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
		for (j in 0 until n) {
			val angle = 2 * PI * i * j / n
			val complex = Complex.fromPolarCoordinates(1.0f, angle)
			sum += spectrum[j] * complex
		}

		result[i] = sum.real / n
	}

	return result
}