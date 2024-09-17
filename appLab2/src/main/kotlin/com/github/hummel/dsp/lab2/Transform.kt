@file:Suppress("unused")

package com.github.hummel.dsp.lab2

import com.tambapps.fft4j.FastFouriers
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class Complex(val real: Float, val imaginary: Float) {
	val magnitude: Float
		get() = sqrt(real * real + imaginary * imaginary)

	val phase: Float
		get() = atan2(imaginary, real)

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
		fortranFourierTransform(n, real, imag)
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
		fortranFourierTransform(n, real, imag)
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

@Suppress("NAME_SHADOWING", "serial")
private fun fortranFourierTransform(n: Int, rex: DoubleArray, imx: DoubleArray) {
	var k: Int
	var tr: Double
	var ti: Double
	var le: Int
	var le2: Int
	var ur: Double
	var ui: Double
	var sr: Double
	var si: Double
	var jm1: Int
	var ip: Int

	//Set constants
	val pi = Math.PI //1050
	val nm1 = n - 1 //1060
	val nd2 = n / 2 //1070
	val m = log2(n.toDouble()).toInt() //1080
	var j = nd2 //1090

	//Bit reversal sorting
	class GoTo1190 : Exception()
	nextI@ for (i in 1 until n - 1) { //1110
		try {
			if (i >= j) throw GoTo1190() //1120
			tr = rex[j] //1130
			ti = imx[j] //1140
			rex[j] = rex[i] //1150
			imx[j] = imx[i] //1160
			rex[i] = tr //1170
			imx[i] = ti //1180
			throw GoTo1190()
		} catch (_: GoTo1190) {
			k = nd2 //1190
			while (k <= j) { //1220
				j -= k //1210
				k /= 2 //1220
				if (k == 0) {
					return
				}
			}
			j += k //1240
			continue@nextI //1250
		}
	}

	//Loop for each stage
	for (l in 1..m) { //1270
		le = (2.0.pow(l)).toInt() //1280
		le2 = le / 2 //1290
		ur = 1.0 //1300
		ui = 0.0 //1310

		//Calculate sine & cosine values
		sr = cos(pi / le2) //1320
		si = -sin(pi / le2) //1330

		//Loop for each sub DFT
		for (j in 1..le2) { //1340
			jm1 = j - 1 //1350

			//Loop for each butterfly
			for (i in jm1..nm1 step le) { //1360
				ip = i + le2 //1370

				//Butterfly calculation
				tr = rex[ip] * ur - imx[ip] * ui //1380
				ti = rex[ip] * ui + imx[ip] * ur //1390
				rex[ip] = rex[i] - tr //1400
				imx[ip] = imx[i] - ti //1410
				rex[i] = rex[i] + tr //1420
				imx[i] = imx[i] + ti //1430
			}
			tr = ur //1450
			ur = tr * sr - ui * si //1460
			ui = tr * si + ui * sr //1470
		}
	}
}