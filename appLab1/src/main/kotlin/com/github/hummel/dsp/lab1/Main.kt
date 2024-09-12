package com.github.hummel.dsp.lab1

import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sin
import kotlin.random.Random

fun main() {
	val duration = 10.0 // Duration in seconds
	val amplitude = 0.5 // Amplitude
	val samplingRate = 44100 // Sampling rate (Hz)
	val frequency = 1200.0 // Frequency of the signal (Hz)

	val t = DoubleArray((samplingRate * duration).toInt()) { (it / samplingRate).toDouble() }
	val sinWave = DoubleArray(t.size) { amplitude * sin(2 * Math.PI * frequency * t[it]) }
	val squareWave = DoubleArray(t.size) { amplitude * if (sin(2 * Math.PI * frequency * t[it]) >= 0) 1.0 else -1.0 }
	val triangleWave =
		DoubleArray(t.size) { amplitude * (2 * abs(2 * (t[it] * frequency - floor(t[it] * frequency + 0.5))) - 1) }
	val sawtoothWave = DoubleArray(t.size) { amplitude * (2 * (t[it] * frequency - floor(0.5 + t[it] * frequency))) }
	val noise = DoubleArray(t.size) { Random.nextDouble(-amplitude, amplitude) }

	writeWavFile("1/sin.wav", sinWave, samplingRate)
	writeWavFile("1/square.wav", squareWave, samplingRate)
	writeWavFile("1/triangle.wav", triangleWave, samplingRate)
	writeWavFile("1/sawtooth.wav", sawtoothWave, samplingRate)
	writeWavFile("1/noise.wav", noise, samplingRate)

	println("Files written")

	// Polyphonic signal
	val polyphonicSignal = DoubleArray(t.size) { sinWave[it] + squareWave[it] + triangleWave[it] + sawtoothWave[it] }
	normalize(polyphonicSignal)
	writeWavFile("2/polyphonic_signal.wav", polyphonicSignal, samplingRate)

	println("Polyphonic signal written")

	// Modulation
	val modulationFrequency = 0.2 // Modulation frequency (Hz)
	val modulatingSine = DoubleArray(t.size) { sin(2 * Math.PI * modulationFrequency * t[it]) }
	val modulatingSquare =
		DoubleArray(t.size) { if (sin(2 * Math.PI * modulationFrequency * t[it]) >= 0) 1.0 else -1.0 }
	val modulatingTriangle =
		DoubleArray(t.size) { 2 * abs(2 * (t[it] * modulationFrequency - floor(t[it] * modulationFrequency + 0.5))) - 1 }
	val modulatingSawtooth =
		DoubleArray(t.size) { 2 * (t[it] * modulationFrequency - floor(0.5 + t[it] * modulationFrequency)) }

	val signals = arrayOf(sinWave, squareWave, triangleWave, sawtoothWave)
	val signalsName = arrayOf("sin", "square", "triangle", "sawtooth")

	for (i in signals.indices) {
		val signal = signals[i]

		// Amplitude Modulation
		writeModulatedFiles(signal, modulatingSine, "3_${signalsName[i]}/am_sine.wav", samplingRate)
		writeModulatedFiles(signal, modulatingSquare, "3_${signalsName[i]}/am_square.wav", samplingRate)
		writeModulatedFiles(signal, modulatingTriangle, "3_${signalsName[i]}/am_triangle.wav", samplingRate)
		writeModulatedFiles(signal, modulatingSawtooth, "3_${signalsName[i]}/am_sawtooth.wav", samplingRate)

		// Frequency Modulation
		writeFMFiles(signal, modulatingSine, "3_${signalsName[i]}/fm_sine.wav", samplingRate)
		writeFMFiles(signal, modulatingSquare, "3_${signalsName[i]}/fm_square.wav", samplingRate)
		writeFMFiles(signal, modulatingTriangle, "3_${signalsName[i]}/fm_triangle.wav", samplingRate)
		writeFMFiles(signal, modulatingSawtooth, "3_${signalsName[i]}/fm_sawtooth.wav", samplingRate)
	}

	println("Modulated signals written.")
}

fun writeWavFile(filePath: String, signal: DoubleArray, samplingRate: Int) {
	val audioFormat = AudioFormat(samplingRate.toFloat(), 16, 1, true, false)
	val bytes = signal.flatMap { shortToBytes((it * 32767).toInt().toShort()).asIterable() }.toByteArray()
	val file = File(filePath)
	file.parentFile.mkdirs()

	val byteArrayInputStream = ByteArrayInputStream(bytes)

	val audioInputStream =
		AudioInputStream(byteArrayInputStream, audioFormat, bytes.size / audioFormat.frameSize.toLong())

	AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file)
}

fun shortToBytes(value: Short): ByteArray {
	return byteArrayOf(
		(value.toInt() and 0xFF).toByte(),
		(value.toInt() shr 8 and 0xFF).toByte()
	)
}

fun writeModulatedFiles(signal: DoubleArray, modulating: DoubleArray, filePath: String, samplingRate: Int) {
	val modulatedSignal = DoubleArray(signal.size) { (1 + modulating[it]) * signal[it] }
	writeWavFile(filePath, modulatedSignal, samplingRate)
}

fun writeFMFiles(signal: DoubleArray, modulating: DoubleArray, filePath: String, samplingRate: Int) {
	val fmSignal =
		DoubleArray(signal.size) { sin(2 * Math.PI * (1200 + 50 * modulating[it]) * (it / samplingRate.toDouble())) }
	writeWavFile(filePath, fmSignal, samplingRate)
}

fun normalize(signal: DoubleArray) {
	val maxVal = signal.maxOrNull() ?: 1.0
	for (i in signal.indices) {
		signal[i] /= maxVal
	}
}