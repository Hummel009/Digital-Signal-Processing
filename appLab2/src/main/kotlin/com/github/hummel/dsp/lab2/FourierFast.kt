package com.github.hummel.dsp.lab2

import kotlin.math.cos
import kotlin.math.sin

fun fft(signal: FloatArray): Array<Complex> {
	val squareLength = 1 shl (32 - Integer.numberOfLeadingZeros(sampleRate * duration - 1))

	val paddedSignal = FloatArray(squareLength) { 0.0f }
	signal.copyInto(paddedSignal)

	val spectrumView = paddedSignal.map { Complex(it, 0.0f) }.toTypedArray()

	val spectrum = fftRecursion(spectrumView, false)

	return spectrum
}

fun ifft(spectrum: Array<Complex>): FloatArray {
	val spectrumView = fftRecursion(spectrum, true)

	val paddedSignal = spectrumView.map { it.real }.toFloatArray()

	val signal = paddedSignal.take(duration * sampleRate).toFloatArray()

	return signal
}

private fun fftRecursion(spectrum: Array<Complex>, invert: Boolean): Array<Complex> {
	val n = spectrum.size
	if (n == 1) return spectrum

	val result = Array(n) { Complex.ZERO }
	val even = Array(n / 2) { Complex.ZERO }
	val odd = Array(n / 2) { Complex.ZERO }

	for (i in 0 until n step 2) {
		even[i / 2] = spectrum[i]
		odd[i / 2] = spectrum[i + 1]
	}

	val evenRes = fftRecursion(even, invert)
	val oddRes = fftRecursion(odd, invert)

	val angle = 2 * PI / n * if (invert) -1 else 1
	var w = Complex(1.0f, 0.0f)
	val wn = Complex(cos(angle), sin(angle))

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