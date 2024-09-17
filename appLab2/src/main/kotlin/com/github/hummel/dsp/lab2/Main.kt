package com.github.hummel.dsp.lab2

import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.*
import kotlin.math.abs

//https://www.desmos.com/calculator/vmgatudfmf?lang=ru

const val PI: Float = 3.141592653589793f

const val sampleRate: Float = 22050.0f //N
const val phase: Float = 0.0f //ф
const val dutyCycle: Float = 0.5f //d
const val duration: Float = 2.0f //sec

var modulatorAmplitude: Float = 0.25f // A
var modulatorFrequency: Float = 220.0f // f

var amplitude: Float = 0.5f //A
var frequency: Float = 880.0f //f

const val samples: Int = (sampleRate * duration).toInt()

fun main() {
	val soundsDir = mdIfNot("output/1_sounds_wave")

	var sineWave = generateSineWave()
	var pulseWave = generatePulseWave()
	var triangleWave = generateTriangleWave()
	var sawtoothWave = generateSawtoothWave()
	var noise = generateNoise()
	var polyphonic = noise.zip(sawtoothWave) { a, b -> a + b }.toFloatArray()

	saveWav(soundsDir, "sine_wave.wav", sineWave)
	saveWav(soundsDir, "pulse_wave.wav", pulseWave)
	saveWav(soundsDir, "triangle_wave.wav", triangleWave)
	saveWav(soundsDir, "sawtooth_wave.wav", sawtoothWave)
	saveWav(soundsDir, "noise.wav", noise)
	saveWav(soundsDir, "polyphonic.wav", polyphonic)

	val signal = floatArrayOf(1f, 0f, 0f, 0f)

	var transformed = discreteFourierTransform(signal)
	var reconstructedSignal = inverseDiscreteFourierTransform(transformed)

	println("BFT ORIG " + signal.joinToString(separator = ", "))
	println("BFT RECO " + reconstructedSignal.joinToString(separator = ", "))

	var error = signal.zip(reconstructedSignal) { a, b -> abs(a - b) }.average()
	println("Average BFT Reconstruction Error: $error")

	println("Which mode: «fortran» or «library»?")
	val fortan = readln()
	transformed = fastFourierTransform(signal, fortan == "fortran")
	reconstructedSignal = inverseFastFourierTransform(transformed, fortan == "fortran")

	println("FFT ORIG " + signal.joinToString(separator = ", "))
	println("FFT RECO " + reconstructedSignal.joinToString(separator = ", "))

	error = signal.zip(reconstructedSignal) { a, b -> abs(a - b) }.average()
	println("Average Reconstruction Error: $error")

	var amplitudeSpec = amplitudeSpectrum(transformed)
	var phaseSpec = phaseSpectrum(transformed)

	//println(amplitudeSpec.take(30).joinToString(separator = ", "))
	//println(phaseSpec.take(30).joinToString(separator = ", "))
}

private fun saveWav(dir: File, filename: String, signal: FloatArray) {
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
	AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, File(dir.path + "/" + filename))
}

private fun mdIfNot(path: String): File {
	val soundsDir = File(path)
	if (!soundsDir.exists()) {
		soundsDir.mkdirs()
	}
	return soundsDir
}