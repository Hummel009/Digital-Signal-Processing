package com.github.hummel.dsp.lab1

import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart.XYChart
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.*

const val PI: Float = 3.141592653589793f

const val sampleRate: Float = 44100.0f //N
const val phase: Float = 0.0f //ф
const val dutyCycle: Float = 0.5f //d
const val duration: Float = 5.0f //sec

var modulatorAmplitude: Float = 0.25f // A
var modulatorFrequency: Float = 220.0f // f

var amplitude: Float = 0.5f //A
var frequency: Float = 880.0f //f

const val samples: Int = (sampleRate * duration).toInt()

fun main() {
	val soundsDir = mdIfNot("appLab1/1_sounds_wave")
	val soundsModWaveDir = mdIfNot("appLab1/3_sounds_mod_wave")
	val graphsDir = mdIfNot("appLab1/1_graphs_wave")
	val graphsModWaveDir = mdIfNot("appLab1/3_graphs_mod_wave")
	val graphsModDir = mdIfNot("appLab1/2_graphs_mod")

	var sineWave = generateSineWave()
	var pulseWave = generatePulseWave()
	var triangleWave = generateTriangleWave()
	var sawtoothWave = generateSawtoothWave()
	var noise = generateNoise()
	var polyphonicSignal = noise.zip(sawtoothWave) { a, b -> a + b }.toFloatArray()

	var sineModulator = generateSineModulator()
	var pulseModulator = generatePulseModulator()
	var triangleModulator = generateTriangleModulator()
	var sawtoothModulator = generateSawtoothModulator()

	var modulatedSineWave = modulateAmplitude(sineWave, sineModulator)
	var modulatedPulseWave = modulateAmplitude(pulseWave, pulseModulator)
	var modulatedTriangleWave = modulateAmplitude(triangleWave, triangleModulator)
	var modulatedSawtoothWave = modulateAmplitude(sawtoothWave, sawtoothModulator)

	saveWav(soundsDir, "sine_wave.wav", sineWave)
	saveWav(soundsDir, "pulse_wave.wav", pulseWave)
	saveWav(soundsDir, "triangle_wave.wav", triangleWave)
	saveWav(soundsDir, "sawtooth_wave.wav", sawtoothWave)
	saveWav(soundsDir, "noise.wav", noise)
	saveWav(soundsDir, "polyphonic.wav", polyphonicSignal)

	saveWav(soundsModWaveDir, "sine_mod_wave.wav", modulatedSineWave)
	saveWav(soundsModWaveDir, "pulse_mod_wave.wav", modulatedPulseWave)
	saveWav(soundsModWaveDir, "triangle_mod_wave.wav", modulatedTriangleWave)
	saveWav(soundsModWaveDir, "sawtooth_mod_wave.wav", modulatedSawtoothWave)

	frequency /= 440.0f
	modulatorFrequency /= 440.0f

	sineWave = generateSineWave()
	pulseWave = generatePulseWave()
	triangleWave = generateTriangleWave()
	sawtoothWave = generateSawtoothWave()
	noise = generateNoise()
	polyphonicSignal = noise.zip(sawtoothWave) { a, b -> a + b }.toFloatArray()

	sineModulator = generateSineModulator()
	pulseModulator = generatePulseModulator()
	triangleModulator = generateTriangleModulator()
	sawtoothModulator = generateSawtoothModulator()

	println("Enter mode: amplitude or frequency")

	val input = readln()

	if (input.lowercase() == "amplitude") {
		modulatedSineWave = modulateAmplitude(sineWave, sineModulator)
		modulatedPulseWave = modulateAmplitude(pulseWave, pulseModulator)
		modulatedTriangleWave = modulateAmplitude(triangleWave, triangleModulator)
		modulatedSawtoothWave = modulateAmplitude(sawtoothWave, sawtoothModulator)
	} else {
		modulatedSineWave = modulateFrequencySineWave(sineModulator)
		modulatedPulseWave = modulateFrequencyPulseWave(pulseModulator)
		modulatedTriangleWave = modulateFrequencyTriangleWave(triangleModulator)
		modulatedSawtoothWave = modulateFrequencySawtoothWave(sawtoothModulator)
	}

	savePlot(graphsDir, "sine_wave.png", sineWave, "Sine Wave")
	savePlot(graphsDir, "pulse_wave.png", pulseWave, "Pulse Wave")
	savePlot(graphsDir, "triangle_wave.png", triangleWave, "Triangle Wave")
	savePlot(graphsDir, "sawtooth_wave.png", sawtoothWave, "Sawtooth Wave")
	savePlot(graphsDir, "noise.png", noise, "Noise")
	savePlot(graphsDir, "polyphonic.png", noise, "Polyphonic")

	savePlot(graphsModDir, "sine_mod.png", sineModulator, "Sine Mod")
	savePlot(graphsModDir, "pulse_mod.png", pulseModulator, "Pulse Mod")
	savePlot(graphsModDir, "triangle_mod.png", triangleModulator, "Triangle Mod")
	savePlot(graphsModDir, "sawtooth_mod.png", sawtoothModulator, "Sawtooth Mod")

	savePlot(graphsModWaveDir, "sine_mod_wave.png", modulatedSineWave, "Sine Mod Wave")
	savePlot(graphsModWaveDir, "pulse_mod_wave.png", modulatedPulseWave, "Pulse Mod Wave")
	savePlot(graphsModWaveDir, "triangle_mod_wave.png", modulatedTriangleWave, "Triangle Mod Wave")
	savePlot(graphsModWaveDir, "sawtooth_mod_wave.png", modulatedSawtoothWave, "Sawtooth Mod Wave")

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
	val xData = (0 until samples step skip).map { it.toDouble() / sampleRate }
	val yData = signal.filterIndexed { index, _ -> index % skip == 0 }.map { it.toDouble() }

	val chart = XYChart(1600, 900)
	chart.title = title
	chart.xAxisTitle = "Time (s)"
	chart.yAxisTitle = "Amplitude"
	chart.addSeries(title, xData.toDoubleArray(), yData.toDoubleArray())
	BitmapEncoder.saveBitmap(chart, dir.path + "/" + filename, BitmapFormat.PNG)
}

private fun mdIfNot(path: String): File {
	val soundsDir = File(path)
	if (!soundsDir.exists()) {
		soundsDir.mkdirs()
	}
	return soundsDir
}