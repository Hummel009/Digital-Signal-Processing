package com.github.hummel.dsp.lab2

fun lowPassFilter(
	spectrum: Array<Complex>, passUntil: Float
): Array<Complex> {
	val halfSize = spectrum.size / 2
	return spectrum.copyOf().mapIndexed { index, complex ->
		val frequency = index * sampleRate.toFloat() / spectrum.size
		if (index < halfSize) {
			if (frequency <= passUntil) complex else Complex.ZERO
		} else {
			Complex.ZERO
		}
	}.toTypedArray()
}

fun highPassFilter(
	spectrum: Array<Complex>, passFrom: Float
): Array<Complex> {
	val halfSize = spectrum.size / 2
	return spectrum.copyOf().mapIndexed { index, complex ->
		val frequency = index * sampleRate.toFloat() / spectrum.size
		if (index < halfSize) {
			if (frequency >= passFrom) complex else Complex.ZERO
		} else {
			Complex.ZERO
		}
	}.toTypedArray()
}

fun bandPassFilter(
	spectrum: Array<Complex>, passIn: ClosedFloatingPointRange<Float>
): Array<Complex> {
	val halfSize = spectrum.size / 2
	return spectrum.copyOf().mapIndexed { index, complex ->
		val frequency = index * sampleRate.toFloat() / spectrum.size
		if (index < halfSize) {
			if (frequency in passIn) complex else Complex.ZERO
		} else {
			Complex.ZERO
		}
	}.toTypedArray()
}

fun normalizeAmplitudes(signal: FloatArray): FloatArray {
	val maxAmplitude = signal.maxOf { it }

	return signal.map { it / maxAmplitude * defaultAmplitude }.toFloatArray()
}