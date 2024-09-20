package com.github.hummel.dsp.lab2

import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart.XYChart
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.*
import kotlin.math.abs

//https://www.desmos.com/calculator/vmgatudfmf?lang=ru

const val PI: Float = 3.141592653589793f

const val duration: Float = 5.0f //sec

const val phase: Float = 0.0f //ф
const val dutyCycle: Float = 0.5f //d

var amplitude: Float = 0.5f //A
var frequency: Float = 1.0f //f

val sampleRate: Float = frequency * 100.0f //N
val samples: Int = (sampleRate * duration).toInt()

fun main() {
	val soundsDir = mdIfNot("output/sounds_wave")
	val graphsDir = mdIfNot("output/graphs_wave")

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

	var signal = sineWave

	saveWav(soundsDir, "signal_orig.wav", signal)
	savePlot(graphsDir, "signal_orig.png", signal, "Signal ORG")

	var transformed = discreteFourierTransform(signal)
	var reconstructedSignal = inverseDiscreteFourierTransform(transformed)

	saveWav(soundsDir, "signal_reconstr_disc.wav", reconstructedSignal)
	savePlot(graphsDir, "signal_reconstr_disc.png", reconstructedSignal, "Signal DFT")

	var error = signal.zip(reconstructedSignal) { a, b -> abs(a - b) }.average()
	println("Average BFT Reconstruction Error: ${String.format("%.8f", error)}")

	println("Which mode: «fortran» or «library»?")
	val input = readln()

	transformed = fastFourierTransform(signal, input == "fortran")
	reconstructedSignal = inverseFastFourierTransform(transformed, input == "fortran")

	saveWav(soundsDir, "signal_reconstr_fast.wav", reconstructedSignal)
	savePlot(graphsDir, "signal_reconstr_fast.png", reconstructedSignal, "Signal FFT")

	error = signal.zip(reconstructedSignal) { a, b -> abs(a - b) }.average()
	println("Average FFT Reconstruction Error: ${String.format("%.8f", error)}")

	signal = noise

	transformed = fastFourierTransform(signal, input == "fortran")
	reconstructedSignal = inverseFastFourierTransform(transformed, input == "fortran")

	val amplitudeSpectrum = computeAmplitudeSpectrum(transformed)
	val phaseSpectrum = computePhaseSpectrum(transformed)

	savePlot(graphsDir, "amplitude_spectrum.png", amplitudeSpectrum, "Amplitude Spectrum")
	savePlot(graphsDir, "phase_spectrum.png", phaseSpectrum, "Phase Spectrum")

	val lowPassFiltered = lowPassFilter(amplitudeSpectrum, cutoffFrequency = 50.0f)
	val highPassFiltered = highPassFilter(amplitudeSpectrum, cutoffFrequency = 50.0f)
	val bandPassFiltered = bandPassFilter(amplitudeSpectrum, cutoffFrequencyL = 25.0f, cutoffFrequencyH = 75.0f)

	savePlot(graphsDir, "signal_low_pass.png", lowPassFiltered, "Low Pass Filtered")
	savePlot(graphsDir, "signal_high_pass.png", highPassFiltered, "High Pass Filtered")
	savePlot(graphsDir, "signal_band_pass.png", bandPassFiltered, "Band Pass Filtered")
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

private fun savePlot(dir: File, filename: String, signal: FloatArray, title: String, skip: Int = 1) {
	val frequencyData = (0 until signal.size).map { it * (sampleRate / signal.size) }

	val chart = XYChart(1600, 900)
	chart.title = title
	chart.xAxisTitle = "Frequency (Hz)"
	chart.yAxisTitle = "Amplitude"
	chart.addSeries(
		title,
		frequencyData.map { it.toDouble() }.toDoubleArray(),
		signal.map { it.toDouble() }.toDoubleArray()
	)
	BitmapEncoder.saveBitmap(chart, dir.path + "/" + filename, BitmapFormat.PNG)
}

private fun mdIfNot(path: String): File {
	val soundsDir = File(path)
	if (!soundsDir.exists()) {
		soundsDir.mkdirs()
	}
	return soundsDir
}