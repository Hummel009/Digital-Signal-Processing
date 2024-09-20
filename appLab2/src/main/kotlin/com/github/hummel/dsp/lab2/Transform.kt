package com.github.hummel.dsp.lab2

import com.tambapps.fft4j.FastFouriers
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Complex(val real: Float, val imaginary: Float) {
	val magnitude: Float
		get() = sqrt(real * real + imaginary * imaginary)

	val phase: Float
		get() = atan2(imaginary, real)

	operator fun plus(other: Complex) = Complex(real + other.real, imaginary + other.imaginary)

	operator fun minus(other: Complex) = Complex(real - other.real, imaginary - other.imaginary)

	operator fun times(scalar: Float) = Complex(real * scalar, imaginary * scalar)

	operator fun times(other: Complex) = Complex(
		real * other.real - imaginary * other.imaginary, real * other.imaginary + imaginary * other.real
	)
}

fun fft(signal: FloatArray): Array<Complex> {
	val n = signal.size
	val realIn = DoubleArray(n) { signal[it].toDouble() }
	val imagIn = DoubleArray(n) { 0.0 }
	val realOut = DoubleArray(n) { 0.0 }
	val imagOut = DoubleArray(n) { 0.0 }

	FastFouriers.RECURSIVE_COOLEY_TUKEY.transform(realIn, imagIn, realOut, imagOut)

	return Array(n) { i ->
		Complex(realOut[i].toFloat(), imagOut[i].toFloat())
	}
}

fun ifft(complexSignal: Array<Complex>): FloatArray {
	val n = complexSignal.size
	val realIn = DoubleArray(n) { 0.0 }
	val imagIn = DoubleArray(n) { 0.0 }
	val realOut = DoubleArray(n) { 0.0 }
	val imagOut = DoubleArray(n) { 0.0 }

	for (i in 0 until n) {
		realIn[i] = complexSignal[i].real.toDouble()
		imagIn[i] = complexSignal[i].imaginary.toDouble()
	}

	FastFouriers.RECURSIVE_COOLEY_TUKEY.transform(realIn, imagIn, realOut, imagOut)

	return realOut.map { it / n }.map { it.toFloat() }.toFloatArray().reversedArray()
}

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