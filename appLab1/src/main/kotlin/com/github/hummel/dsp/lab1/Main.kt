package com.github.hummel.dsp.lab1

import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart.XYChart
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.*

//https://www.desmos.com/calculator/vmgatudfmf?lang=ru

const val PI: Float = 3.141592653589793f

const val duration: Float = 5.0f //sec

const val sampleRate: Float = 44100.0f //N
const val dutyCycle: Float = 0.5f //d
const val phase: Float = 0.0f //ф

var defaultAmplitude: Float = 0.5f //A
var defaultFrequency: Float = 880.0f //f

var modulatorAmplitude: Float = 0.25f // A
var modulatorFrequency: Float = 1.0f // f

const val samples: Int = (sampleRate * duration).toInt()

fun main() {
	val origSoundsDir = mdIfNot("output/orig_sounds")
	val origGraphsDir = mdIfNot("output/orig_graphs")
	val modsDir = mdIfNot("output/mods")
	val moddedSoundsDir = mdIfNot("output/modded_sounds")
	val moddedGraphsDir = mdIfNot("output/modded_graphs")
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

	savePlot(origGraphsDir, "sine", sineWave, "Sine")
	savePlot(origGraphsDir, "pulse", pulseWave, "Pulse")
	savePlot(origGraphsDir, "triangle", triangleWave, "Triangle")
	savePlot(origGraphsDir, "sawtooth", sawtoothWave, "Sawtooth")
	savePlot(origGraphsDir, "noise", noise, "Noise")
	savePlot(origGraphsDir, "polyphonic", noise, "Polyphonic")

	savePlot(modsDir, "sine", sineModulator, "Sine")
	savePlot(modsDir, "pulse", pulseModulator, "Pulse")
	savePlot(modsDir, "triangle", triangleModulator, "Triangle")
	savePlot(modsDir, "sawtooth", sawtoothModulator, "Sawtooth")

	savePlot(moddedGraphsDir, "sine", moddedSineWave, "Sine")
	savePlot(moddedGraphsDir, "pulse", moddedPulseWave, "Pulse")
	savePlot(moddedGraphsDir, "triangle", moddedTriangleWave, "Triangle")
	savePlot(moddedGraphsDir, "sawtooth", moddedSawtoothWave, "Sawtooth")

	val cautionModulator1 = generatePulseModulator()
	val cautionModulator2 = generateTriangleModulator(modFrequency = modulatorFrequency * 3.0f)
	val cautionModulator3 = generateSawtoothModulator(modFrequency = modulatorFrequency * 1.5f)

	var moddedWave0 = modulateFrequencySineWave(modulator = cautionModulator2)
	var moddedWave1 = modulateFrequencySineWave(modulator = cautionModulator1)
	var moddedWave2 = modulateFrequencySineWave(modulator = cautionModulator3)

	saveWav(cautionDir, "caution", moddedWave0 + moddedWave1 + moddedWave2)
	savePlot(cautionDir, "caution", moddedWave0 + moddedWave1 + moddedWave2, "Caution")
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

private fun savePlot(dir: File, filename: String, signal: FloatArray, title: String, skip: Int = 100) {
	val xData = (0 until signal.size step skip).map { it.toDouble() / signal.size }.toDoubleArray()
	val yData = signal.filterIndexed { index, _ -> index % skip == 0 }.map { it.toDouble() }.toDoubleArray()

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