package com.github.hummel.dsp.lab1

import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart.XYChart
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.*
import kotlin.math.*

const val sampleRate: Float = 44100.0f //N
const val amplitude: Float = 0.5f //A
const val phase: Float = 0.0f //ф
const val dutyCycle: Float = 0.5f //d

var frequency: Float = 440.0f //f

const val duration: Float = 5.0f //sec

const val samples: Int = (sampleRate * duration).toInt()

const val PI: Float = 3.141592653589793f

fun main() {
	val soundsDir = mdIfNot("appLab1/sounds")
	val soundsModDir = mdIfNot("appLab1/sounds_mod")
	val graphsDir = mdIfNot("appLab1/graphs")
	val graphsModDir = mdIfNot("appLab1/graphs_mod")

	var sineWave = generateSineWave()
	var pulseWave = generatePulseWave()
	var triangleWave = generateTriangleWave()
	var sawtoothWave = generateSawtoothWave()
	var noise = generateNoise()

	var polyphonicSignal = noise.zip(sawtoothWave) { a, b -> a + b }.toFloatArray()

	saveWav(soundsDir, "sine_wave.wav", sineWave)
	saveWav(soundsDir, "pulse_wave.wav", pulseWave)
	saveWav(soundsDir, "triangle_wave.wav", triangleWave)
	saveWav(soundsDir, "sawtooth_wave.wav", sawtoothWave)

	saveWav(soundsDir, "noise.wav", noise)
	saveWav(soundsDir, "polyphonic.wav", polyphonicSignal)

	println("Sounds are ready!")

	val modulatedSineWave = modulateAmplitude(sineWave, generateSineModulator())
	val modulatedPulseWave = modulateAmplitude(pulseWave, generatePulseModulator())
	val modulatedTriangleWave = modulateAmplitude(triangleWave, generateTriangleModulator())
	val modulatedSawtoothWave = modulateAmplitude(sawtoothWave, generateSawtoothModulator())

	saveWav(soundsModDir, "sine_wave.wav", modulatedSineWave)
	saveWav(soundsModDir, "pulse_wave.wav", modulatedPulseWave)
	saveWav(soundsModDir, "triangle_wave.wav", modulatedTriangleWave)
	saveWav(soundsModDir, "sawtooth_wave.wav", modulatedSawtoothWave)

	println("Modulated sounds are ready!")

	frequency = 1.0f

	sineWave = generateSineWave()
	pulseWave = generatePulseWave()
	triangleWave = generateTriangleWave()
	sawtoothWave = generateSawtoothWave()
	noise = generateNoise()

	polyphonicSignal = noise.zip(sawtoothWave) { a, b -> a + b }.toFloatArray()

	savePlot(graphsDir, "sine_wave.png", sineWave, "Sine Wave")
	savePlot(graphsDir, "pulse_wave.png", pulseWave, "Pulse Wave")
	savePlot(graphsDir, "triangle_wave.png", triangleWave, "Triangle Wave")
	savePlot(graphsDir, "sawtooth_wave.png", sawtoothWave, "Sawtooth Wave")

	savePlot(graphsDir, "noise.png", noise, "Noise")
	savePlot(graphsDir, "polyphonic.png", noise, "Polyphonic")

	println("Graphs are ready!")

	savePlot(graphsModDir, "sine_wave.png", modulatedSineWave, "Sine Wave")
	savePlot(graphsModDir, "pulse_wave.png", modulatedPulseWave, "Pulse Wave")
	savePlot(graphsModDir, "triangle_wave.png", modulatedTriangleWave, "Triangle Wave")
	savePlot(graphsModDir, "sawtooth_wave.png", modulatedSawtoothWave, "Sawtooth Wave")

	println("Modulated graphs are ready!")

}

private fun generateSineWave(): FloatArray {
	return FloatArray(samples) { n ->
		amplitude * sin(2 * PI * frequency * n / sampleRate + phase)
	}
}

private fun generatePulseWave(): FloatArray {
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * frequency * n / sampleRate + phase) % (2 * PI)
		if (modValue / (2 * PI) <= dutyCycle) amplitude else -amplitude
	}
}

private fun generateTriangleWave(): FloatArray {
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * frequency * n / sampleRate + phase + 3 * PI / 2) % (2 * PI)
		((2 * amplitude / PI) * (abs(modValue - PI) - (PI / 2)))
	}
}

private fun generateSawtoothWave(): FloatArray {
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * frequency * n / sampleRate + phase + PI) % (2 * PI)
		((amplitude / PI) * (modValue - PI))
	}
}

private fun generateNoise(): FloatArray {
	return FloatArray(samples) {
		amplitude * (Math.random().toFloat() * 2 - 1)
	}
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
	AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, File(dir, filename))
}

private fun savePlot(dir: File, filename: String, signal: FloatArray, title: String, skip: Int = 100) {
	val xData = (0 until samples step skip).map { it.toDouble() / sampleRate }
	val yData = signal.filterIndexed { index, _ -> index % skip == 0 }.map { it.toDouble() }

	val chart = XYChart(800, 400)
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