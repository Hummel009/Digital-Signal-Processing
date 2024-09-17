@file:Suppress("unused")

package com.github.hummel.dsp.lab2

import com.tambapps.fft4j.FastFouriers
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Complex(val real: Float, val imaginary: Float) {
	operator fun plus(other: Complex) = Complex(real + other.real, imaginary + other.imaginary)

	operator fun times(scalar: Float) = Complex(real * scalar, imaginary * scalar)

	operator fun times(other: Complex) = Complex(
		real * other.real - imaginary * other.imaginary,
		real * other.imaginary + imaginary * other.real
	)
}

fun discreteFourierTransform(signalValues: FloatArray): Array<Complex> {
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

fun inverseDiscreteFourierTransform(spectrum: Array<Complex>): FloatArray {
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

fun fastFourierTransform(signal: FloatArray, fortran: Boolean): Array<Complex> {
	val n = signal.size
	val real = DoubleArray(n) { signal[it].toDouble() }
	val imag = DoubleArray(n) { 0.0 }

	if (fortran) {
		fortranFourierTransform(real, imag)
	} else {
		FastFouriers.BEST.transform(real, imag)
	}

	return Array(n) { i -> Complex(real[i].toFloat(), imag[i].toFloat()) }
}

fun inverseFastFourierTransform(complexSignal: Array<Complex>, fortran: Boolean): FloatArray {
	val n = complexSignal.size
	var real = DoubleArray(n) { 0.0 }
	var imag = DoubleArray(n) { 0.0 }

	for (i in 0 until n) {
		real[i] = complexSignal[i].real.toDouble()
		imag[i] = complexSignal[i].imaginary.toDouble()
	}

	if (fortran) {
		fortranFourierTransform(real, imag)
	} else {
		FastFouriers.BEST.transform(real, imag)
		real = real.map { it * -1 }.toDoubleArray()
	}

	return FloatArray(n).apply {
		for (i in indices) {
			this[i] = (real[i] / n).toFloat()
		}
	}
}

fun amplitudeSpectrum(complexSignal: FloatArray): FloatArray {
	val n = complexSignal.size / 2
	return FloatArray(n) { i ->
		sqrt(complexSignal[2 * i] * complexSignal[2 * i] + complexSignal[2 * i + 1] * complexSignal[2 * i + 1])
	}
}

fun phaseSpectrum(complexSignal: FloatArray): FloatArray {
	val n = complexSignal.size / 2
	return FloatArray(n) { i ->
		atan2(complexSignal[2 * i + 1], complexSignal[2 * i])
	}
}

fun lowPassFilter(signal: FloatArray, cutoffFreq: Float): FloatArray {
	val result = FloatArray(signal.size)
	val rc = 1.0f / (cutoffFreq * 2 * PI)
	val alpha = 1.0f / (rc + 1)

	result[0] = signal[0]
	for (i in 1 until signal.size) {
		result[i] = result[i - 1] + alpha * (signal[i] - result[i - 1])
	}
	return result
}

private fun fortranFourierTransform(re: DoubleArray, im: DoubleArray) {
	val n = re.size

	// Bit-reversal permutation
	val j = IntArray(n)
	var k = 0
	for (i in 1 until n) {
		val bit = n shr 1
		while (k >= bit) {
			k -= bit
		}
		j[i] = k
		k += bit
	}

	var m = 1
	while (m < n) {
		val theta = -2.0 * Math.PI / (m * 2)
		val wpr = cos(theta).toDouble()
		val wpi = sin(theta).toDouble()
		var wR = 1.0
		var wI = 0.0
		for (k in 0 until m) {
			for (i in k until n step m * 2) {
				val j = i + m
				val tempr = wR * re[j] - wI * im[j]
				val tempi = wR * im[j] + wI * re[j]
				re[j] = re[i] - tempr
				im[j] = im[i] - tempi
				re[i] += tempr
				im[i] += tempi
			}
			val temp = wR
			wR = temp * wpr - wI * wpi
			wI = temp * wpi + wI * wpr
		}
		m *= 2
	}

	// Normalization
	for (i in 0 until n) {
		re[i] /= n
		im[i] /= n
	}
}