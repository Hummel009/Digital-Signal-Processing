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

const val sampleRate: Float = 44100.0f //N
const val dutyCycle: Float = 0.5f //d
const val phase: Float = 0.0f //ф

var defaultAmplitude: Float = 0.5f //A
var defaultFrequency: Float = 880.0f //f

const val samples: Int = (sampleRate * duration).toInt()

fun main() {
	val soundsDir = mdIfNot("output/sounds")
	val graphsDir = mdIfNot("output/graphs")
	val spectrumDir = mdIfNot("output/spectrum")
	val filterDir = mdIfNot("output/filter")

	var signal = generateSineWave(frequency = 500.0f).zip(generateSineWave(frequency = 1000.0f)) { a, b ->
		a + b
	}.toFloatArray().zip(generateSineWave(frequency = 1500.0f)) { a, b ->
		a + b
	}.toFloatArray()

	val originalSize = signal.size
	val nearestPowerOfTwo = 1 shl (32 - Integer.numberOfLeadingZeros(originalSize - 1))
	val paddedSignal = FloatArray(nearestPowerOfTwo)
	signal.copyInto(paddedSignal)

	saveWav(soundsDir, "orig", paddedSignal)
	saveTimePlot(graphsDir, "orig", paddedSignal, "ORIG")

	println("Which mode: «dft» or «fft»?")
	val input = readln()

	val transformed = if (input.lowercase() == "dft") {
		require(paddedSignal.size < 1000) { "Too large sample rate for this non-optimized method." }

		val deconstructedSignal = dft(paddedSignal)
		val reconstructedSignal = idft(deconstructedSignal)

		saveWav(soundsDir, "reco_dft", reconstructedSignal)
		saveTimePlot(graphsDir, "reco_dft", reconstructedSignal, "RECO DFT")

		val error = paddedSignal.zip(reconstructedSignal) { a, b -> abs(a - b) }.average()
		println("Average BFT Reconstruction Error: ${String.format("%.8f", error)}")

		deconstructedSignal
	} else {
		val deconstructedSignal = fft(paddedSignal)
		val reconstructedSignal = ifft(deconstructedSignal)

		saveWav(soundsDir, "reco_fft", reconstructedSignal)
		saveTimePlot(graphsDir, "reco_fft", reconstructedSignal, "RECO FFT")

		val error = paddedSignal.zip(reconstructedSignal) { a, b -> abs(a - b) }.average()
		println("Average FFT Reconstruction Error: ${String.format("%.8f", error)}")

		deconstructedSignal
	}

	val (amplitudeSpectrum, phaseSpectrum) = decomposeSignal(transformed.copyOf())

	saveTimePlot(spectrumDir, "amplitude", amplitudeSpectrum, "Amplitude")
	saveTimePlot(spectrumDir, "phase", phaseSpectrum, "Phase")

	val lowPassFiltered = lowPassFilter(
		transformed, passUntil = 550.0f
	)
	val highPassFiltered = highPassFilter(
		transformed, passFrom = 1450.0f
	)
	val bandPassFiltered = bandPassFilter(
		transformed, passIn = 950.0f..1050.0f
	)

	saveFrequencyPlot(
		filterDir, "ampl_low_pass", lowPassFiltered.map { it.real }.toFloatArray(), "Low Pass"
	)
	saveFrequencyPlot(
		filterDir, "ampl_high_pass", highPassFiltered.map { it.real }.toFloatArray(), "High Pass"
	)
	saveFrequencyPlot(
		filterDir, "ampl_band_pass", bandPassFiltered.map { it.real }.toFloatArray(), "Band Pass"
	)

	val lowPassSignal = ifft(lowPassFiltered)
	val highPassSignal = ifft(highPassFiltered)
	val bandPassSignal = ifft(bandPassFiltered)

	saveTimePlot(filterDir, "sound_low_pass", lowPassSignal, "Low Pass")
	saveTimePlot(filterDir, "sound_high_pass", highPassSignal, "High Pass")
	saveTimePlot(filterDir, "sound_band_pass", bandPassSignal, "Band Pass")

	saveWav(soundsDir, "low_pass", lowPassSignal)
	saveWav(soundsDir, "high_pass", highPassSignal)
	saveWav(soundsDir, "band_pass", bandPassSignal)
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
	AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, File(dir.path + "/" + filename + ".wav"))
}

private fun saveFrequencyPlot(dir: File, filename: String, signal: FloatArray, title: String, skip: Int = 100) {
	val xData = (0 until signal.size step skip).map { it.toDouble() / (signal.size / sampleRate) / 1000 }
	val yData = signal.filterIndexed { index, _ -> index % skip == 0 }.map { it.toDouble() }

	val chart = XYChart(1600, 900)
	chart.title = title
	chart.xAxisTitle = "Frequency (kHz)"
	chart.yAxisTitle = "Amplitude"
	chart.addSeries(title, xData, yData)
	BitmapEncoder.saveBitmap(chart, dir.path + "/" + filename, BitmapFormat.JPG)
}

private fun saveTimePlot(dir: File, filename: String, signal: FloatArray, title: String, skip: Int = 100) {
	val xData = (0 until signal.size step skip).map { it.toDouble() / signal.size * duration }
	val yData = signal.filterIndexed { index, _ -> index % skip == 0 }.map { it.toDouble() }

	val chart = XYChart(1600, 900)
	chart.title = title
	chart.xAxisTitle = "Time (s)"
	chart.yAxisTitle = "Amplitude"
	chart.addSeries(title, xData, yData)
	BitmapEncoder.saveBitmap(chart, dir.path + "/" + filename, BitmapFormat.JPG)
}

private fun mdIfNot(path: String): File {
	val soundsDir = File(path)
	if (!soundsDir.exists()) {
		soundsDir.mkdirs()
	}
	return soundsDir
}