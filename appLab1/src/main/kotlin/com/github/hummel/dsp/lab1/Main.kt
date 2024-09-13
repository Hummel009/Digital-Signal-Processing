package com.github.hummel.dsp.lab1

import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.*
import kotlin.math.*

private const val amplitude: Float = 0.5f //A
private const val frequency: Float = 440.0f //f
private const val phase: Float = 0.0f //ф
private const val sampleRate: Float = 44100.0f //N
private const val dutyCycle: Float = 0.5f //d

private const val duration: Float = 5.0f //sec

private const val PI: Float = 3.141592653589793f

const val samples: Int = (sampleRate * duration).toInt()

fun main() {
	val sineWave = generateSineWave()
	val pulseWave = generatePulseWave()
	val triangleWave = generateTriangleWave()
	val sawtoothWave = generateSawtoothWave()
	val noise = generateNoise()

	val soundsDir = File("sounds")
	if (!soundsDir.exists()) {
		soundsDir.mkdirs()
	}

	saveWav("sounds/sine_wave.wav", sineWave)
	saveWav("sounds/pulse_wave.wav", pulseWave)
	saveWav("sounds/triangle_wave.wav", triangleWave)
	saveWav("sounds/sawtooth_wave.wav", sawtoothWave)
	saveWav("sounds/noise.wav", noise)

	val polyphonicSignal = sineWave.zip(pulseWave) { a, b -> a + b }
	saveWav("sounds/polyphonic.wav", polyphonicSignal.toFloatArray())
}

fun generateSineWave(): FloatArray {
	return FloatArray(samples) { n ->
		amplitude * sin(2 * PI * frequency * n / sampleRate + phase)
	}
}

fun generatePulseWave(): FloatArray {
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * frequency * n / sampleRate + phase) % (2 * PI)
		if (modValue / (2 * PI) <= dutyCycle) amplitude else -amplitude
	}
}

fun generateTriangleWave(): FloatArray {
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * frequency * n / sampleRate + phase + 3 * PI / 2) % (2 * PI)
		((2 * amplitude / PI) * (abs(modValue - PI) - (PI / 2)))
	}
}

fun generateSawtoothWave(): FloatArray {
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * frequency * n / sampleRate + phase + PI) % (2 * PI)
		((amplitude / PI) * (modValue - PI))
	}
}

fun generateNoise(): FloatArray {
	return FloatArray(samples) {
		amplitude * (Math.random().toFloat() * 2 - 1)
	}
}

fun saveWav(filename: String, signal: FloatArray) {
	val audioFormat = AudioFormat(sampleRate, 16, 1, true, false)
	val data = ByteArray(signal.size * 2)
	for (i in signal.indices) {
		val value = (signal[i] * Short.MAX_VALUE).toInt().toShort()
		data[i * 2] = (value.toInt() and 0x00FF).toByte()
		data[i * 2 + 1] = (value.toInt() shr 8 and 0x00FF).toByte()
	}
	val audioInputStream = AudioInputStream(
		ByteArrayInputStream(data), audioFormat, signal.size.toLong()
	)
	AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, File(filename))
}