package com.github.hummel.dsp.lab2

import kotlin.math.cos
import kotlin.math.sin

fun dft(signal: FloatArray): Array<Complex> {
	val n = signal.size
	val real = FloatArray(n) { signal[it] }
	val imag = FloatArray(n) { 0.0f }

	transform(real, imag, false)

	return Array(n) { Complex(real[it], imag[it]) }
}

fun idft(spectrum: Array<Complex>): FloatArray {
	val n = spectrum.size
	val real = FloatArray(n) { spectrum[it].real }
	val imag = FloatArray(n) { spectrum[it].imaginary }

	transform(real, imag, true)

	return real.map { it.toFloat() }.toFloatArray()
}

private fun transform(real: FloatArray, imag: FloatArray, inv: Boolean) {
	val inputRe = real.map { it.toDouble() }.toDoubleArray()
	val inputIm = imag.map { it.toDouble() }.toDoubleArray()

	val outputRe = DoubleArray(real.size)
	val outputIm = DoubleArray(real.size)

	if (inv) {
		discreteFourierInv(inputRe, inputIm, outputRe, outputIm)
	} else {
		discreteFourier(inputRe, inputIm, outputRe, outputIm)
	}

	inputRe.copyInto(outputRe)
	inputIm.copyInto(outputIm)
}

fun discreteFourier(inputRe: DoubleArray, inputIm: DoubleArray, outputRe: DoubleArray, outputIm: DoubleArray) {
	val size = inputRe.size
	val n = size.toDouble()
	val temp = DoubleArray(2) { 0.0 }
	val temp2 = DoubleArray(2) { 0.0 }

	for (i in 0 until size) {
		outputRe[i] = 0.0
		outputIm[i] = 0.0
		for (j in 0 until size) {
			temp[0] = inputRe[j]
			temp[1] = inputIm[j]

			val x = -2.0 * PI * i * j / n
			temp2[0] = cos(x)
			temp2[1] = sin(x)

			outputRe[i] += temp[0] * temp2[0] - temp[1] * temp2[1]
			outputIm[i] += temp[0] * temp2[1] + temp[1] * temp2[0]
		}
	}
}

fun discreteFourierInv(inputRe: DoubleArray, inputIm: DoubleArray, outputRe: DoubleArray, outputIm: DoubleArray) {
	val size = inputRe.size
	val n = size.toDouble()
	val temp = DoubleArray(2) { 0.0 }
	val temp2 = DoubleArray(2) { 0.0 }

	for (i in 0 until size) {
		outputRe[i] = 0.0
		outputIm[i] = 0.0
		for (j in 0 until size) {
			temp[0] = inputRe[j]
			temp[1] = inputIm[j]

			val x = 2.0 * PI * i * j / n
			temp2[0] = cos(x)
			temp2[1] = sin(x)

			outputRe[i] += temp[0] * temp2[0] - temp[1] * temp2[1]
			outputIm[i] += temp[0] * temp2[1] + temp[1] * temp2[0]
		}

		outputRe[i] /= n
		outputIm[i] /= n
	}
}