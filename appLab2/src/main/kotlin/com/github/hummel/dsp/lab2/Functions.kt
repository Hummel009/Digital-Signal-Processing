@file:Suppress("unused")

package com.github.hummel.dsp.lab2

import kotlin.math.sin

fun generateSineWave(
	duration: Int, frequency: Float
): FloatArray {
	return FloatArray(sampleRate * duration) { n ->
		val value = 2 * PI * frequency * n / sampleRate + phase
		amplitude * sin(value)
	}
}

fun generateNoise(
	duration: Int
): FloatArray {
	return FloatArray(sampleRate * duration) {
		amplitude * (Math.random().toFloat() * 2 - 1)
	}
}