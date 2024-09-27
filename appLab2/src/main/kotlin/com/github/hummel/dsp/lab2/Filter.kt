package com.github.hummel.dsp.lab2

fun lowPassFilter(fftResult: Array<Complex>, cutoff: Float): Array<Complex> {
	return fftResult.mapIndexed { index, complex ->
		val frequency = index * (sampleRate / fftResult.size)
		if (frequency > cutoff) Complex(0.0f, 0.0f) else complex
	}.toTypedArray()
}

fun highPassFilter(fftResult: Array<Complex>, cutoff: Float): Array<Complex> {
	return fftResult.mapIndexed { index, complex ->
		val frequency = index * (sampleRate / fftResult.size)
		if (frequency < cutoff) Complex(0.0f, 0.0f) else complex
	}.toTypedArray()
}

fun bandPassFilter(fftResult: Array<Complex>, cutoff: ClosedFloatingPointRange<Float>): Array<Complex> {
	return fftResult.mapIndexed { index, complex ->
		val frequency = index * (sampleRate / fftResult.size)
		if (frequency in cutoff) complex else Complex(0.0f, 0.0f)
	}.toTypedArray()
}