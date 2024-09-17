package com.github.hummel.dsp.lab2

import org.jtransforms.fft.DoubleFFT_1D
import kotlin.math.atan2
import kotlin.math.sqrt

fun discreteFourierTransform(signal: FloatArray): DoubleArray {
	val n = signal.size
	val fft = DoubleFFT_1D(n.toLong())
	val complexSignal = DoubleArray(2 * n)

	for (i in signal.indices) {
		complexSignal[2 * i] = signal[i].toDouble() // реальная часть
		complexSignal[2 * i + 1] = 0.0 // мнимая часть
	}

	fft.realForwardFull(complexSignal)
	return complexSignal
}

fun inverseDiscreteFourierTransform(complexSignal: DoubleArray): FloatArray {
	val n = complexSignal.size / 2
	val fft = DoubleFFT_1D(n.toLong())
	val output = complexSignal.clone()

	fft.realInverseFull(output, true)
	return FloatArray(n) { output[2 * it].toFloat() }.map { it / n.toFloat() }.toFloatArray()
}

fun amplitudeSpectrum(complexSignal: DoubleArray): FloatArray {
	val n = complexSignal.size / 2
	return FloatArray(n) { i ->
		sqrt(complexSignal[2 * i] * complexSignal[2 * i] + complexSignal[2 * i + 1] * complexSignal[2 * i + 1]).toFloat()
	}
}

fun phaseSpectrum(complexSignal: DoubleArray): FloatArray {
	val n = complexSignal.size / 2
	return FloatArray(n) { i ->
		atan2(complexSignal[2 * i + 1], complexSignal[2 * i]).toFloat()
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