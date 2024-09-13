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
	val soundsDir = File("sounds")
	if (!soundsDir.exists()) {
		soundsDir.mkdirs()
	}

	val soundsModDir = File("sounds_mod")
	if (!soundsModDir.exists()) {
		soundsModDir.mkdirs()
	}

	val graphsDir = File("graphs")
	if (!graphsDir.exists()) {
		graphsDir.mkdirs()
	}

	val graphsModDir = File("graphs_mod")
	if (!graphsModDir.exists()) {
		graphsModDir.mkdirs()
	}
	var sineWave = generateSineWave()
	var pulseWave = generatePulseWave()
	var triangleWave = generateTriangleWave()
	var sawtoothWave = generateSawtoothWave()
	var noise = generateNoise()

	var polyphonicSignal = noise.zip(sawtoothWave) { a, b -> a + b }.toFloatArray()

	saveWav("sounds/sine_wave.wav", sineWave)
	saveWav("sounds/pulse_wave.wav", pulseWave)
	saveWav("sounds/triangle_wave.wav", triangleWave)
	saveWav("sounds/sawtooth_wave.wav", sawtoothWave)

	saveWav("sounds/noise.wav", noise)
	saveWav("sounds/polyphonic.wav", polyphonicSignal)

	println("Sounds are ready!")

	val modulatedSineWave = modulateAmplitude(sineWave, generateSineModulator())
	val modulatedPulseWave = modulateAmplitude(pulseWave, generatePulseModulator())
	val modulatedTriangleWave = modulateAmplitude(triangleWave, generateTriangleModulator())
	val modulatedSawtoothWave = modulateAmplitude(sawtoothWave, generateSawtoothModulator())

	saveWav("sounds_mod/sine_wave.wav", modulatedSineWave)
	saveWav("sounds_mod/pulse_wave.wav", modulatedPulseWave)
	saveWav("sounds_mod/triangle_wave.wav", modulatedTriangleWave)
	saveWav("sounds_mod/sawtooth_wave.wav", modulatedSawtoothWave)

	println("Modulated sounds are ready!")

	frequency = 1.0f

	sineWave = generateSineWave()
	pulseWave = generatePulseWave()
	triangleWave = generateTriangleWave()
	sawtoothWave = generateSawtoothWave()
	noise = generateNoise()

	polyphonicSignal = noise.zip(sawtoothWave) { a, b -> a + b }.toFloatArray()

	savePlot("graphs/sine_wave.png", sineWave, "Sine Wave")
	savePlot("graphs/pulse_wave.png", pulseWave, "Pulse Wave")
	savePlot("graphs/triangle_wave.png", triangleWave, "Triangle Wave")
	savePlot("graphs/sawtooth_wave.png", sawtoothWave, "Sawtooth Wave")

	savePlot("graphs/noise.png", noise, "Noise")
	savePlot("graphs/polyphonic.png", noise, "Polyphonic")

	println("Graphs are ready!")

	savePlot("graphs_mod/sine_wave.png", modulatedSineWave, "Sine Wave")
	savePlot("graphs_mod/pulse_wave.png", modulatedPulseWave, "Pulse Wave")
	savePlot("graphs_mod/triangle_wave.png", modulatedTriangleWave, "Triangle Wave")
	savePlot("graphs_mod/sawtooth_wave.png", modulatedSawtoothWave, "Sawtooth Wave")

	println("Modulated graphs are ready!")

}

fun generateSineWave(): FloatArray {
	return FloatArray(samples) { n ->
		amplitude * sin(2 * PI * frequency * n / sampleRate + phase)
	}
}

fun generatePulseWave(): FloatArray {
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * frequency * n / sampleRate + phase) % (2 * PI)
		if (modValue / (2 * PI) <= dutyCycle) amplitude else -amplitude
	}
}

fun generateTriangleWave(): FloatArray {
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * frequency * n / sampleRate + phase + 3 * PI / 2) % (2 * PI)
		((2 * amplitude / PI) * (abs(modValue - PI) - (PI / 2)))
	}
}

fun generateSawtoothWave(): FloatArray {
	return FloatArray(samples) { n ->
		val modValue = (2 * PI * frequency * n / sampleRate + phase + PI) % (2 * PI)
		((amplitude / PI) * (modValue - PI))
	}
}

fun generateNoise(): FloatArray {
	return FloatArray(samples) {
		amplitude * (Math.random().toFloat() * 2 - 1)
	}
}

fun saveWav(filename: String, signal: FloatArray) {
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
	AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, File(filename))
}

fun savePlot(filename: String, signal: FloatArray, title: String, skip: Int = 100) {
	val xData = (0 until samples step skip).map { it.toDouble() / sampleRate }
	val yData = signal.filterIndexed { index, _ -> index % skip == 0 }.map { it.toDouble() }

	val chart = XYChart(800, 400)
	chart.title = title
	chart.xAxisTitle = "Time (s)"
	chart.yAxisTitle = "Amplitude"
	chart.addSeries(title, xData.toDoubleArray(), yData.toDoubleArray())
	BitmapEncoder.saveBitmap(chart, filename, BitmapFormat.PNG)
}