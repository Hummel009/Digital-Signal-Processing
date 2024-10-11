package com.github.hummel.dsp.lab2

fun lowPassFilter(
	fftResult: Array<Complex>, passUntil: Float
): Array<Complex> {
	return fftResult.copyOf().mapIndexed { index, complex ->
		val frequency = (index * sampleRate) / fftResult.size
		if (frequency <= passUntil) complex else Complex(0.0f, complex.imaginary)
	}.toTypedArray()
}

fun highPassFilter(
	fftResult: Array<Complex>, passFrom: Float
): Array<Complex> {
	return fftResult.copyOf().mapIndexed { index, complex ->
		val frequency = (index * sampleRate) / fftResult.size
		if (frequency >= passFrom) complex else Complex(0.0f, complex.imaginary)
	}.toTypedArray()
}

fun bandPassFilter(
	fftResult: Array<Complex>, passIn: ClosedFloatingPointRange<Float>
): Array<Complex> {
	return fftResult.copyOf().mapIndexed { index, complex ->
		val frequency = (index * sampleRate) / fftResult.size
		if (frequency in passIn) complex else Complex(0.0f, complex.imaginary)
	}.toTypedArray()
}