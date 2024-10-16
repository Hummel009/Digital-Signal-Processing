package com.github.hummel.dsp.lab2

import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart.XYChart
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.*
import kotlin.math.abs
import kotlin.math.ceil

//https://www.desmos.com/calculator/vmgatudfmf?lang=ru

const val PI: Float = 3.141592653589793f

const val sampleRate: Int = 44100 //N
const val dutyCycle: Float = 0.5f //d
const val phase: Float = 0.0f //ф

const val defaultAmplitude: Float = 0.2f //A
const val defaultDuration: Int = 2 //sec

val skip: Int = ceil(sampleRate / 2048.0f).coerceAtLeast(1.0f).toInt()

fun main() {
	val origDir = mdIfNot("output/orig")
	val recoDir = mdIfNot("output/reco")
	val spectrumDir = mdIfNot("output/spectrum")
	val filterDir = mdIfNot("output/filter")

	val signal1 = generateSineWave(frequency = 1000.0f)
	val signal2 = generateSineWave(frequency = 2000.0f)
	val signal3 = generateSineWave(frequency = 3000.0f)

	val signal = signal1.zip(signal2) { a, b ->
		a + b
	}.toFloatArray().zip(signal3) { a, b ->
		a + b
	}.toFloatArray()

	saveWav(origDir, "orig1", signal1)
	saveWav(origDir, "orig2", signal2)
	saveWav(origDir, "orig3", signal3)

	saveWav(origDir, "orig", signal)

	saveTimePlot(origDir, "orig", signal, "ORIG")

	println("Which mode: «dft» or «fft»?")
	val input = readln()

	val spectrum = if (input.lowercase() == "dft") {
		require(signal.size <= 4096) { "Too large sample rate for this non-optimized method." }

		val deconstructedSignal = dft(signal)
		val reconstructedSignal = idft(deconstructedSignal)

		saveWav(recoDir, "reco_dft", reconstructedSignal)
		saveTimePlot(recoDir, "reco_dft", reconstructedSignal, "RECO DFT")

		val error = signal.zip(reconstructedSignal) { a, b -> abs(a - b) }.average()
		println("Average BFT Reconstruction Error: ${"%.8f".format(error)}")

		deconstructedSignal
	} else {
		val deconstructedSignal = fft(signal)
		val reconstructedSignal = ifft(deconstructedSignal)

		saveWav(recoDir, "reco_fft", reconstructedSignal)
		saveTimePlot(recoDir, "reco_fft", reconstructedSignal, "RECO FFT")

		val error = signal.zip(reconstructedSignal) { a, b -> abs(a - b) }.average()
		println("Average FFT Reconstruction Error: ${"%.8f".format(error)}")

		deconstructedSignal
	}

	val (amplitudeSpectrum, phaseSpectrum) = decomposeSignal(spectrum)

	saveFreqPlot(spectrumDir, "amplitude", amplitudeSpectrum, "Amplitude")
	saveFreqPlot(spectrumDir, "phase", phaseSpectrum, "Phase")

	val lowPassFiltered = lowPassFilter(
		spectrum, passUntil = 1500.0f
	)
	val highPassFiltered = highPassFilter(
		spectrum, passFrom = 2500.0f
	)
	val bandPassFiltered = bandPassFilter(
		spectrum, passIn = 1500.0f..2500.0f
	)

	val lowPassSignal = normalizeAmplitudes(ifft(lowPassFiltered))
	val highPassSignal = normalizeAmplitudes(ifft(highPassFiltered))
	val bandPassSignal = normalizeAmplitudes(ifft(bandPassFiltered))

	saveTimePlot(filterDir, "sound_low_pass", lowPassSignal, "Low Pass")
	saveTimePlot(filterDir, "sound_high_pass", highPassSignal, "High Pass")
	saveTimePlot(filterDir, "sound_band_pass", bandPassSignal, "Band Pass")

	saveWav(filterDir, "low_pass", lowPassSignal)
	saveWav(filterDir, "high_pass", highPassSignal)
	saveWav(filterDir, "band_pass", bandPassSignal)
}

private fun saveWav(dir: File, filename: String, signal: FloatArray) {
	val audioFormat = AudioFormat(sampleRate.toFloat(), 16, 1, true, false)
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

private fun saveFreqPlot(dir: File, filename: String, signal: FloatArray, title: String) {
	val xData = (0 until signal.size step skip).map {
		it.toDouble() * sampleRate / signal.size
	}
	val yData = signal.filterIndexed { index, _ ->
		index % skip == 0
	}

	val chart = XYChart(1600, 900)
	chart.title = title
	chart.xAxisTitle = "Frequency (Hz)"
	chart.yAxisTitle = title
	chart.addSeries(title, xData, yData)
	BitmapEncoder.saveBitmap(chart, dir.path + "/" + filename, BitmapFormat.JPG)
}

private fun saveTimePlot(dir: File, filename: String, signal: FloatArray, title: String) {
	val xData = (0 until signal.size step skip).map {
		it.toDouble() / sampleRate
	}
	val yData = signal.filterIndexed { index, _ ->
		index % skip == 0
	}

	val chart = XYChart(1600, 900)
	chart.title = title
	chart.xAxisTitle = "Time (s)"
	chart.yAxisTitle = title
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