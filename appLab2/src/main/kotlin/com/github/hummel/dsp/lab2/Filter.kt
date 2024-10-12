package com.github.hummel.dsp.lab2

fun lowPassFilter(
	spectrum: Array<Complex>, passUntil: Float
): Array<Complex> {
	return spectrum.copyOf().mapIndexed { index, complex ->
		val frequency = index * sampleRate.toFloat() / spectrum.size
		if (frequency <= passUntil) complex else Complex(0.0f, 0.0f)
	}.toTypedArray()
}

fun highPassFilter(
	spectrum: Array<Complex>, passFrom: Float
): Array<Complex> {
	return spectrum.copyOf().mapIndexed { index, complex ->
		val frequency = index * sampleRate.toFloat() / spectrum.size
		if (frequency >= passFrom) complex else Complex(0.0f, 0.0f)
	}.toTypedArray()
}

fun bandPassFilter(
	spectrum: Array<Complex>, passIn: ClosedFloatingPointRange<Float>
): Array<Complex> {
	return spectrum.copyOf().mapIndexed { index, complex ->
		val frequency = index * sampleRate.toFloat() / spectrum.size
		if (frequency in passIn) complex else Complex(0.0f, 0.0f)
	}.toTypedArray()
}