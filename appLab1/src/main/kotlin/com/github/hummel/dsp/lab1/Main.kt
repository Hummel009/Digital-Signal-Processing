package com.github.hummel.dsp.lab1

import javax.sound.sampled.*
import java.io.File
import kotlin.math.*

fun main() {
    val sampleRate = 44100
    val durationSeconds = 5
    val amplitude = 0.5f

    val sineWave = generateSineWave(sampleRate, durationSeconds, amplitude)
    val squareWave = generateSquareWave(sampleRate, durationSeconds, amplitude, 0.5)
    val triangleWave = generateTriangleWave(sampleRate, durationSeconds, amplitude)
    val sawtoothWave = generateSawtoothWave(sampleRate, durationSeconds, amplitude)
    val noise = generateNoise(sampleRate, durationSeconds, amplitude)

    saveWav("sine_wave.wav", sineWave)
    saveWav("square_wave.wav", squareWave)
    saveWav("triangle_wave.wav", triangleWave)
    saveWav("sawtooth_wave.wav", sawtoothWave)
    saveWav("noise.wav", noise)

    val polyphonicSignal = sineWave.zip(squareWave) { a, b -> a + b }
    saveWav("polyphonic_signal.wav", polyphonicSignal.toFloatArray())
}

fun generateSineWave(sampleRate: Int, duration: Int, amplitude: Float): List<Float> {
    val samples = (sampleRate * duration)
    return List(samples) { i ->
        amplitude * sin(2 * PI * 440 * i / sampleRate).toFloat() // 440Hz
    }
}

fun generateSquareWave(sampleRate: Int, duration: Int, amplitude: Float, dutyCycle: Double): List<Float> {
    val samples = (sampleRate * duration)
    return List(samples) { i ->
        if (i % (sampleRate / 440) < (sampleRate / 440 * dutyCycle)) amplitude else -amplitude
    }
}

fun generateTriangleWave(sampleRate: Int, duration: Int, amplitude: Float): List<Float> {
    val samples = (sampleRate * duration)
    return List(samples) { i ->
        amplitude * (1 - abs(2 * (i % (sampleRate / 440)) / (sampleRate / 440) - 1)).toFloat()
    }
}

fun generateSawtoothWave(sampleRate: Int, duration: Int, amplitude: Float): List<Float> {
    val samples = (sampleRate * duration)
    return List(samples) { i ->
        amplitude * (2 * (i % (sampleRate / 440)).toFloat() / (sampleRate / 440) - 1)
    }
}

fun generateNoise(sampleRate: Int, duration: Int, amplitude: Float): List<Float> {
    val samples = (sampleRate * duration)
    return List(samples) { amplitude * (Math.random().toFloat() * 2 - 1) }
}

fun saveWav(filename: String, signal: FloatArray) {
    val audioFormat = AudioFormat(44100f, 16, 1, true, false)
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