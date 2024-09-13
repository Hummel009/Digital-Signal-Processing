package com.github.hummel.dsp.lab1

import kotlin.math.abs
import kotlin.math.sin

fun modulateAmplitude(signal: FloatArray, modulator: FloatArray): FloatArray {
	return FloatArray(signal.size) { n ->
		signal[n] * (1 + modulator[n]) // Модуляция амплитуды
	}
}

fun modulateFrequency(signal: FloatArray, modulator: FloatArray): FloatArray {
	return FloatArray(signal.size) { n ->
		val modulatedFrequency = frequency * (1 + modulator[n]) // Модуляция частоты
		amplitude * sin(2 * PI * modulatedFrequency * n / sampleRate)
	}
}

fun generateSineModulator(): FloatArray {
	val modulatorFrequency = 2.0f // Частота модулирующего сигнала
	return FloatArray(samples) { n ->
		0.5f * sin(2 * PI * modulatorFrequency * n / sampleRate) // Модулирующий сигнал
	}
}

fun generatePulseModulator(): FloatArray {
	val modulatorFrequency = 2.0f // Частота модулирующего сигнала
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * modulatorFrequency * n / sampleRate) % (2 * PI)
		if (modValue / (2 * PI) <= 0.5) 0.5f else -0.5f // Импульсный сигнал с 50% скважностью
	}
}

fun generateTriangleModulator(): FloatArray {
	val modulatorFrequency = 2.0f // Частота модулирующего сигнала
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * modulatorFrequency * n / sampleRate) % (2 * PI)
		(2 / PI) * (abs(modValue - PI) - (PI / 2)) // Треугольный сигнал
	}
}

fun generateSawtoothModulator(): FloatArray {
	val modulatorFrequency = 2.0f // Частота модулирующего сигнала
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * modulatorFrequency * n / sampleRate) % (2 * PI)
		(1 / PI) * (modValue - PI) // Пилообразный сигнал
	}
}