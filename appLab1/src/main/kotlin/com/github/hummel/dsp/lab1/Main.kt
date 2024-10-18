package com.github.hummel.dsp.lab1

import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart.XYChart
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.*
import kotlin.math.ceil

//https://www.desmos.com/calculator/vmgatudfmf?lang=ru

const val PI: Float = 3.141592653589793f

const val sampleRate: Int = 2048 //N
const val dutyCycle: Float = 0.5f //d
const val phase: Float = 0.0f //ф

const val defaultAmplitude: Float = 0.5f //A
const val defaultFrequency: Float = 880.0f //f

const val modulatorAmplitude: Float = 0.25f // A
const val modulatorFrequency: Float = 1.0f // f

const val duration: Int = 2 //sec

val skip: Int = ceil(sampleRate / 2048.0f).coerceAtLeast(1.0f).toInt()

fun main() {
	val origSoundsDir = mdIfNot("output/orig_sounds")
	val origGraphsDir = mdIfNot("output/orig_graphs")
	val moddedSoundsDir = mdIfNot("output/modded_sounds")
	val cautionDir = mdIfNot("output/caution")

	println("Which mode: «amplitude» or «frequency»")

	val input = readln()

	var sineWave = generateSineWave()
	var pulseWave = generatePulseWave()
	var triangleWave = generateTriangleWave()
	var sawtoothWave = generateSawtoothWave()
	var noise = generateNoise()
	var polyphonic = noise.zip(sawtoothWave) { a, b -> a + b }.toFloatArray()

	var sineModulator = generateSineModulator()
	var pulseModulator = generatePulseModulator()
	var triangleModulator = generateTriangleModulator()
	var sawtoothModulator = generateSawtoothModulator()

	var moddedSineWave: FloatArray
	var moddedPulseWave: FloatArray
	var moddedTriangleWave: FloatArray
	var moddedSawtoothWave: FloatArray

	if (input.lowercase() == "amplitude") {
		moddedSineWave = modulateAmplitude(sineWave, sineModulator)
		moddedPulseWave = modulateAmplitude(pulseWave, pulseModulator)
		moddedTriangleWave = modulateAmplitude(triangleWave, triangleModulator)
		moddedSawtoothWave = modulateAmplitude(sawtoothWave, sawtoothModulator)
	} else {
		moddedSineWave = modulateFrequencySineWave(modulator = sineModulator)
		moddedPulseWave = modulateFrequencyPulseWave(modulator = pulseModulator)
		moddedTriangleWave = modulateFrequencyTriangleWave(modulator = triangleModulator)
		moddedSawtoothWave = modulateFrequencySawtoothWave(modulator = sawtoothModulator)
	}

	saveWav(origSoundsDir, "sine", sineWave)
	saveWav(origSoundsDir, "pulse", pulseWave)
	saveWav(origSoundsDir, "triangle", triangleWave)
	saveWav(origSoundsDir, "sawtooth", sawtoothWave)
	saveWav(origSoundsDir, "noise", noise)
	saveWav(origSoundsDir, "polyphonic", polyphonic)

	saveWav(moddedSoundsDir, "sine", moddedSineWave)
	saveWav(moddedSoundsDir, "pulse", moddedPulseWave)
	saveWav(moddedSoundsDir, "triangle", moddedTriangleWave)
	saveWav(moddedSoundsDir, "sawtooth", moddedSawtoothWave)

	saveTimePlot(origGraphsDir, "sine", sineWave, "Sine")
	saveTimePlot(origGraphsDir, "pulse", pulseWave, "Pulse")
	saveTimePlot(origGraphsDir, "triangle", triangleWave, "Triangle")
	saveTimePlot(origGraphsDir, "sawtooth", sawtoothWave, "Sawtooth")
	saveTimePlot(origGraphsDir, "noise", noise, "Noise")
	saveTimePlot(origGraphsDir, "polyphonic", noise, "Polyphonic")

	val cautionModulator1 = generatePulseModulator()
	val cautionModulator2 = generateTriangleModulator(frequency = modulatorFrequency * 3.0f)
	val cautionModulator3 = generateSawtoothModulator(frequency = modulatorFrequency * 1.5f)

	var moddedWave0 = modulateFrequencySineWave(modulator = cautionModulator2)
	var moddedWave1 = modulateFrequencySineWave(modulator = cautionModulator1)
	var moddedWave2 = modulateFrequencySineWave(modulator = cautionModulator3)

	saveWav(cautionDir, "caution", moddedWave0 + moddedWave1 + moddedWave2)
	saveTimePlot(cautionDir, "caution", moddedWave0 + moddedWave1 + moddedWave2, "Caution")
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