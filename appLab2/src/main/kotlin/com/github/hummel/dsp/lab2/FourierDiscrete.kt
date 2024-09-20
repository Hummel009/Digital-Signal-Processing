package com.github.hummel.dsp.lab2

import kotlin.math.cos
import kotlin.math.sin

fun dft(signalValues: FloatArray): Array<Complex> {
	val n = signalValues.size
	val spectrum = Array(n) { Complex(0f, 0f) }

	for (i in 0 until n) {
		val angle = 2 * PI.toFloat() * i / n
		for (j in 0 until n) {
			val complex = Complex(cos(angle * j).toFloat(), -sin(angle * j).toFloat())
			spectrum[i] += complex * signalValues[j]
		}
	}

	return spectrum
}

fun idft(spectrum: Array<Complex>): FloatArray {
	val n = spectrum.size
	val result = FloatArray(n)

	for (i in 0 until n) {
		var sum = Complex(0f, 0f)
		val angle = 2 * PI.toFloat() * i / n

		for (j in 0 until n) {
			val complex = Complex(cos(angle * j).toFloat(), sin(angle * j).toFloat())
			sum += spectrum[j] * complex
		}

		result[i] = sum.real / n
	}

	return result
}