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

const val duration: Int = 2 //sec

val skip: Int = ceil(sampleRate / 2048.0f).coerceAtLeast(1.0f).toInt()

fun main() {
	val origDir = mdIfNot("output/orig")
	val spectrumDir = mdIfNot("output/spectrum")
	val filterDir = mdIfNot("output/filter")

	val signal1 = generateSineWave(frequency = 300.0f)
	val signal2 = generateSineWave(frequency = 400.0f)
	val signal3 = generateSineWave(frequency = 500.0f)

	val signal = normalizeAmplitudes(signal1.zip(signal2) { a, b ->
		a + b
	}.toFloatArray().zip(signal3) { a, b ->
		a + b
	}.toFloatArray())

	saveWav(origDir, "orig1", signal1)
	saveWav(origDir, "orig2", signal2)
	saveWav(origDir, "orig3", signal3)

	saveWav(origDir, "orig", signal)

	saveTimePlot(origDir, "orig", signal, "ORIG")

	println("Which mode: «dft» or «fft»?")
	val input = readln()

	if (input.lowercase() == "dft") {
		require(signal.size <= 4096) { "Too large sample rate for this non-optimized method." }
	}

	val transform = if (input.lowercase() == "dft") ::dft else ::fft
	val transformInverse = if (input.lowercase() == "dft") ::idft else ::ifft

	val deconstructedSignal = transform(signal)
	val reconstructedSignal = transformInverse(deconstructedSignal)

	saveWav(origDir, "reco", reconstructedSignal)
	saveTimePlot(origDir, "reco", reconstructedSignal, "RECO")

	val error = signal.zip(reconstructedSignal) { a, b -> abs(a - b) }.average()
	println("Average Reconstruction Error: ${"%.8f".format(error)}")

	val spectrum = deconstructedSignal

	val (amplitudeSpectrum, phaseSpectrum) = decomposeSignal(spectrum)

	saveFreqPlot(spectrumDir, "amplitude", normalizeAmplitudes(amplitudeSpectrum), "Amplitude")
	saveFreqPlot(spectrumDir, "phase", phaseSpectrum, "Phase")

	val lowPassFiltered = lowPassFilter(
		spectrum, passUntil = 350.0f
	)
	val highPassFiltered = highPassFilter(
		spectrum, passFrom = 450.0f
	)
	val bandPassFiltered = bandPassFilter(
		spectrum, passIn = 350.0f..450.0f
	)

	val lowPassSignal = normalizeAmplitudes(transformInverse(lowPassFiltered))
	val highPassSignal = normalizeAmplitudes(transformInverse(highPassFiltered))
	val bandPassSignal = normalizeAmplitudes(transformInverse(bandPassFiltered))

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