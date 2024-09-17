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

const val sampleRate: Float = 22050.0f //N
const val phase: Float = 0.0f //ф
const val dutyCycle: Float = 0.5f //d
const val duration: Float = 2.0f //sec

var amplitude: Float = 0.5f //A
var frequency: Float = 880.0f //f

const val samples: Int = (sampleRate * duration).toInt()

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

	val signal = FloatArray(100) {
		Math.random().toFloat()
	}

	saveWav(soundsDir, "signal_orig.wav", signal)
	savePlot(graphsDir, "signal_orig.png", sineWave, "Signal Orig")

	var transformed = discreteFourierTransform(signal)
	var reconstructedSignal = inverseDiscreteFourierTransform(transformed)

	saveWav(soundsDir, "signal_reconstr.wav", reconstructedSignal)
	savePlot(graphsDir, "signal_reconstr.png", pulseWave, "Signal Reconstr")

	println("BFT ORIG " + signal.take(10).joinToString(separator = "; ") { String.format("%.2f", it) })
	println("BFT RECO " + reconstructedSignal.take(10).joinToString(separator = "; ") { String.format("%.2f", it) })

	var error = signal.zip(reconstructedSignal) { a, b -> abs(a - b) }.average()
	println("Average BFT Reconstruction Error: ${String.format("%.2f", error)}")

	println("Which mode: «fortran» or «library»?")
	val fortan = readln()

	transformed = fastFourierTransform(signal, fortan == "fortran")
	reconstructedSignal = inverseFastFourierTransform(transformed, fortan == "fortran")

	saveWav(soundsDir, "signal_reconstr.wav", reconstructedSignal)
	savePlot(graphsDir, "signal_reconstr.png", reconstructedSignal, "Reconstructed Signal")

	println("FFT ORIG " + signal.take(10).joinToString(separator = "; ") { String.format("%.2f", it) })
	println("FFT RECO " + reconstructedSignal.take(10).joinToString(separator = "; ") { String.format("%.2f", it) })

	error = signal.zip(reconstructedSignal) { a, b -> abs(a - b) }.average()
	println("Average Reconstruction Error: ${String.format("%.2f", error)}")

	var amplitudeSpec = amplitudeSpectrum(transformed)
	var phaseSpec = phaseSpectrum(transformed)

	println()

	println("AMPL SPECTRE " + amplitudeSpec.take(10).joinToString(separator = "; ") { String.format("%.2f", it) })
	println("PHASE SPECTRE " + phaseSpec.take(10).joinToString(separator = "; ") { String.format("%.2f", it) })
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

private fun savePlot(dir: File, filename: String, signal: FloatArray, title: String, skip: Int = 100) {
	val sampleRate = 100 // Define a sample rate if not defined elsewhere
	val xData = (0 until signal.size step skip).map { it.toDouble() / sampleRate }
	val yData = signal.filterIndexed { index, _ -> index % skip == 0 }.map { it.toDouble() }

	val chart = XYChart(1600, 900)
	chart.title = title
	chart.xAxisTitle = "Time (s)"
	chart.yAxisTitle = "Amplitude"
	chart.addSeries(title, xData.toDoubleArray(), yData.toDoubleArray())

	// Ensure the directory exists before saving the plot
	if (!dir.exists()) {
		dir.mkdirs()
	}

	BitmapEncoder.saveBitmap(chart, dir.path + "/" + filename, BitmapFormat.PNG)
}

private fun mdIfNot(path: String): File {
	val soundsDir = File(path)
	if (!soundsDir.exists()) {
		soundsDir.mkdirs()
	}
	return soundsDir
}