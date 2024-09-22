package com.github.hummel.dsp.lab3

import kotlin.math.exp


fun gaussianKernel(kernelSize: Int, sigma: Double): Array<DoubleArray> {
	val kernel = Array(kernelSize) { DoubleArray(kernelSize) }
	var sumVal = 0.0
	val center = kernelSize / 2

	for (i in 0 until kernelSize) {
		for (j in 0 until kernelSize) {
			val x = i - center
			val y = j - center
			kernel[i][j] = (1 / (2 * Math.PI * sigma * sigma)) * exp(-(x * x + y * y) / (2 * sigma * sigma))
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

fun applyGaussianBlur(imageArray: Array<Array<IntArray>>, kernelSize: Int, sigma: Double): Array<Array<IntArray>> {
	val height = imageArray.size
	val width = imageArray[0].size
	val channels = imageArray[0][0].size
	val padSize = kernelSize / 2
	val kernel = gaussianKernel(kernelSize, sigma)

	val blurredImage = Array(height) { Array(width) { IntArray(3) } }

	for (i in 0 until height) {
		for (j in 0 until width) {
			for (c in 0 until channels) {
				var sumColor = 0.0
				var weightSum = 0.0 // To normalize the result

				for (k in -padSize until padSize) {
					for (m in -padSize until padSize) {
						val newY = i + k
						val newX = j + m

						// Check if newY and newX are within bounds
						if (newY in 0 until height && newX in 0 until width) {
							sumColor += imageArray[newY][newX][c] * kernel[k + padSize][m + padSize]
							weightSum += kernel[k + padSize][m + padSize]
						}
					}
				}

				// Normalize color value by total weight
				blurredImage[i][j][c] = if (weightSum > 0) (sumColor / weightSum).toInt().coerceIn(0, 255) else 0
			}
		}
	}

	return blurredImage
}