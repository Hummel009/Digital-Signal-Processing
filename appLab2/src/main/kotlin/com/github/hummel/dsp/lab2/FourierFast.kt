package com.github.hummel.dsp.lab2

import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin

fun fft(signal: FloatArray): Array<Complex> {
	val n = signal.size
	val real = FloatArray(n) { signal[it] }
	val imag = FloatArray(n) { 0.0f }

	fft(real, imag)

	return Array(n) { i ->
		Complex(real[i], imag[i])
	}
}

fun ifft(complexSignal: Array<Complex>): FloatArray {
	val n = complexSignal.size
	val real = FloatArray(n) { i ->
		complexSignal[i].real
	}
	val imag = FloatArray(n) { i ->
		complexSignal[i].imaginary
	}

	ifft(real, imag)

	return real.map { it / n }.toFloatArray()
}

fun fft(x: FloatArray, y: FloatArray) {
	var j: Int
	var k: Int
	var n1: Int
	var n2: Int
	var a: Int
	var c: Float
	var s: Float
	var t1: Float
	var t2: Float
	var n = x.size
	var m = (ln(n.toFloat()) / ln(2.0)).toInt()

	j = 0
	n2 = n / 2
	var i = 1
	while (i < n - 1) {
		n1 = n2
		while (j >= n1) {
			j = j - n1
			n1 = n1 / 2
		}
		j = j + n1

		if (i < j) {
			t1 = x[i]
			x[i] = x[j]
			x[j] = t1
			t1 = y[i]
			y[i] = y[j]
			y[j] = t1
		}
		i++
	}

	n1 = 0
	n2 = 1

	i = 0
	while (i < m) {
		n1 = n2
		n2 = n2 + n2
		a = 0

		j = 0
		while (j < n1) {
			c = cos(a.toFloat())
			s = sin(a.toFloat())
			a += 1 shl m - i - 1

			k = j
			while (k < n) {
				t1 = c * x[k + n1] - s * y[k + n1]
				t2 = s * x[k + n1] + c * y[k + n1]
				x[k + n1] = x[k] - t1
				y[k + n1] = y[k] - t2
				x[k] = x[k] + t1
				y[k] = y[k] + t2
				k = k + n2
			}
			++j
		}
		++i
	}
}

fun ifft(real: FloatArray, imag: FloatArray) {
	imag.forEachIndexed { i, v -> imag[i] = -v }
	fft(real, imag)
	imag.forEachIndexed { i, v -> imag[i] = -v }
}