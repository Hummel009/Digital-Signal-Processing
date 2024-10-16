package com.github.hummel.dsp.lab2

import kotlin.math.atan2
import kotlin.math.sqrt

data class Complex(val real: Float, val imaginary: Float) {
	val magnitude: Float
		get() = sqrt(real * real + imaginary * imaginary)

	val phase: Float
		get() = atan2(imaginary, real)

	operator fun plus(other: Complex): Complex = Complex(
		real + other.real, imaginary + other.imaginary
	)

	operator fun minus(other: Complex): Complex = Complex(
		real - other.real, imaginary - other.imaginary
	)

	operator fun times(other: Complex): Complex = Complex(
		real * other.real - imaginary * other.imaginary, real * other.imaginary + imaginary * other.real
	)

	operator fun times(scalar: Float): Complex = Complex(
		real * scalar, imaginary * scalar
	)

	operator fun div(other: Complex): Complex {
		val denominator = other.real * other.real + other.imaginary * other.imaginary
		return Complex(
			(real * other.real + imaginary * other.imaginary) / denominator,
			(imaginary * other.real - real * other.imaginary) / denominator
		)
	}

	operator fun div(scalar: Float): Complex = Complex(
		real / scalar, imaginary / scalar
	)

	companion object {
		val ZERO = Complex(0.0f, 0.0f)
	}
}