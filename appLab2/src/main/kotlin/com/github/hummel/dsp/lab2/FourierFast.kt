package com.github.hummel.dsp.lab2

import kotlin.math.cos
import kotlin.math.sin

fun fft(signal: FloatArray): Array<Complex> {
	val n = signal.size
	val real = FloatArray(n) { signal[it] }
	val imag = FloatArray(n) { 0.0f }

	transform(real, imag)

	return Array(n) { Complex(real[it], imag[it]) }
}

fun ifft(spectrum: Array<Complex>): FloatArray {
	val n = spectrum.size
	val real = FloatArray(n) { spectrum[it].real }
	val imag = FloatArray(n) { spectrum[it].imaginary }

	transform(real, imag)

	return real.map { it.toFloat() }.toFloatArray()
}

private fun transform(real: FloatArray, imag: FloatArray) {
	val inputRe = real.map { it.toDouble() }.toDoubleArray()
	val inputIm = imag.map { it.toDouble() }.toDoubleArray()

	val outputRe = DoubleArray(real.size)
	val outputIm = DoubleArray(real.size)

	fastFourier(inputRe, inputIm, outputRe, outputIm)

	inputRe.copyInto(outputRe)
	inputIm.copyInto(outputIm)
}

private fun fastFourier(inputRe: DoubleArray, inputIm: DoubleArray, outputRe: DoubleArray, outputIm: DoubleArray) {
	val n = inputRe.size
	val bits = get2Exponent(n)

	inputRe.copyInto(outputRe)
	inputIm.copyInto(outputIm)

	bitReverseVector(outputRe, outputIm, bits)

	var m = 2
	while (m <= n) {
		var i = 0
		while (i < n) {
			var k = 0
			while (k < m / 2) {
				val evenIndex = i + k
				val oddIndex = i + k + (m / 2)
				val evenRe = outputRe[evenIndex]
				val evenIm = outputIm[evenIndex]

				val oddRe = outputRe[oddIndex]
				val oddIm = outputIm[oddIndex]

				val x = -2.0 * PI * k / m.toDouble()
				val expXRe = cos(x)
				val expXIm = sin(x)

				val wmRe = expXRe * oddRe - expXIm * oddIm
				val wmIm = expXRe * oddIm + expXIm * oddRe

				outputRe[evenIndex] = evenRe + wmRe
				outputIm[evenIndex] = evenIm + wmIm

				outputRe[oddIndex] = evenRe - wmRe
				outputIm[oddIndex] = evenIm - wmIm

				k++
			}
			i += m
		}
		m *= 2
	}
}

private fun get2Exponent(x: Int): Int {
	require(x > 0) { "Size must be greater than 0" }

	var power = 0
	var number = 1

	while (number < x) {
		power++
		number *= 2
	}

	require(number == x) { "Size is not a power of 2" }

	return power
}

private fun bitReverseVector(re: DoubleArray, im: DoubleArray, bits: Int) {
	for (j in 1 until re.size / 2) {
		val swapPos = bitReversedIndex(j, bits)
		swap(re, im, j, swapPos)
	}
}

private fun bitReversedIndex(n: Int, bits: Int): Int {
	var reversedN = n
	var count = bits - 1

	var tempN = n shr 1
	while (tempN > 0) {
		reversedN = (reversedN shl 1) or (tempN and 1)
		count--
		tempN = tempN shr 1
	}

	return (reversedN shl count) and ((1 shl bits) - 1)
}

private fun swap(re: DoubleArray, im: DoubleArray, i: Int, j: Int) {
	re[i] = re[j].also { re[j] = re[i] }
	im[i] = im[j].also { im[j] = im[i] }
}