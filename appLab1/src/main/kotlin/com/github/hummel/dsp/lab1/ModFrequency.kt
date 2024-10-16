package com.github.hummel.dsp.lab1

import kotlin.math.abs
import kotlin.math.sin

fun modulateFrequencySineWave(
	duration: Int = defaultDuration,
	amplitude: Float = defaultAmplitude,
	frequency: Float = defaultFrequency,
	modulator: FloatArray
): FloatArray {
	var value = 0.0f

	return FloatArray(duration * sampleRate) { n ->
		val res = amplitude * sin(value)
		value += 2 * PI * frequency * (1 + modulator[n]) / sampleRate + phase
		res
	}
}

fun modulateFrequencyPulseWave(
	duration: Int = defaultDuration,
	amplitude: Float = defaultAmplitude,
	frequency: Float = defaultFrequency,
	modulator: FloatArray
): FloatArray {
	var value = 0.0f

	return FloatArray(duration * sampleRate) { n ->
		val res = if (value % (2 * PI) / (2 * PI) <= dutyCycle) amplitude else -amplitude
		value += 2 * PI * frequency * (1 + modulator[n]) / sampleRate + phase
		res
	}
}

fun modulateFrequencyTriangleWave(
	duration: Int = defaultDuration,
	amplitude: Float = defaultAmplitude,
	frequency: Float = defaultFrequency,
	modulator: FloatArray
): FloatArray {
	var value = 0.0f

	return FloatArray(duration * sampleRate) { n ->
		val res = (2 * amplitude / PI) * (abs((value + 3 * PI / 2) % (2 * PI) - PI) - (PI / 2))
		value += 2 * PI * frequency * (1 + modulator[n]) / sampleRate + phase
		res
	}
}

fun modulateFrequencySawtoothWave(
	duration: Int = defaultDuration,
	amplitude: Float = defaultAmplitude,
	frequency: Float = defaultFrequency,
	modulator: FloatArray
): FloatArray {
	var value = 0.0f

	return FloatArray(duration * sampleRate) { n ->
		val res = (amplitude / PI) * ((value + PI) % (2 * PI) - PI)
		value += 2 * PI * frequency * (1 + modulator[n]) / sampleRate + phase
		res
	}
}