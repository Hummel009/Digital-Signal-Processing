package com.github.hummel.dsp.lab2

import kotlin.math.atan2
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