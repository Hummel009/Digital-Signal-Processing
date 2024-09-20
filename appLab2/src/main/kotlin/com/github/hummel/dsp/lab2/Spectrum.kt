package com.github.hummel.dsp.lab2

import kotlin.math.atan2
import kotlin.math.sqrt

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