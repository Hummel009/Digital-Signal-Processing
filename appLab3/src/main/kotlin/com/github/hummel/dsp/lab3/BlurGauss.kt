package com.github.hummel.dsp.lab3

import kotlin.math.exp

fun applyGaussianBlur(imageArray: Array<Array<IntArray>>, boxSize: Int, sigma: Float): Array<Array<IntArray>> {
	val (height, width) = extractDimensions(imageArray)
	val padSize = boxSize / 2
	val kernel = gaussianKernel(boxSize, sigma)

	val blurredImage = Array(height) { Array(width) { IntArray(channels) } }

	for (y in 0 until height) {
		for (x in 0 until width) {
			for (c in 0 until channels) {
				var sum = 0.0f
				var weightSum = 0.0f

				for (dy in -padSize until padSize) {
					for (dx in -padSize until padSize) {
						val newY = y + dy
						val newX = x + dx

						if (newY in 0 until height && newX in 0 until width) {
							val kVal = kernel[dy + padSize][dx + padSize]

							sum += imageArray[newY][newX][c] * kVal
							weightSum += kVal
						}
					}
				}

				blurredImage[y][x][c] = (sum / weightSum).toInt().coerceIn(0, 255)
			}
		}
	}

	return blurredImage
}

fun gaussianKernel(kernelSize: Int, sigma: Float): Array<FloatArray> {
	val kernel = Array(kernelSize) { FloatArray(kernelSize) }
	var sumVal = 0.0f
	val center = kernelSize / 2

	for (i in 0 until kernelSize) {
		for (j in 0 until kernelSize) {
			val x = i - center
			val y = j - center
			kernel[i][j] = (1 / (2 * PI * sigma * sigma)) * exp(-(x * x + y * y) / (2 * sigma * sigma))
			sumVal += kernel[i][j]
		}
	}

	// Normalize the kernel
	for (i in 0 until kernelSize) {
		for (j in 0 until kernelSize) {
			kernel[i][j] /= sumVal
		}
	}

	return kernel
}