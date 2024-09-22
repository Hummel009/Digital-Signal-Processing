package com.github.hummel.dsp.lab3

import kotlin.math.sqrt

fun sobelOperator(imageArray: Array<Array<IntArray>>): Array<Array<IntArray>> {
	// Define Sobel kernels.
	val sobelX = arrayOf(
		intArrayOf(-1, 0, 1), intArrayOf(-2, 0, 2), intArrayOf(-1, 0, 1)
	)

	val sobelY = arrayOf(
		intArrayOf(1, 2, 1), intArrayOf(0, 0, 0), intArrayOf(-1, -2, -1)
	)

	val (height, width) = extractDimensions(imageArray)

	val gradientMagnitudeRGB = Array(height) { Array(width) { IntArray(channels) } }

	for (y in 1 until height - 1) {
		for (x in 1 until width - 1) {
			var gxR = 0.0
			var gxG = 0.0
			var gxB = 0.0
			var gyR = 0.0
			var gyG = 0.0
			var gyB = 0.0

			// Calculate gradients
			for (i in -1..1) {
				for (j in -1..1) {
					gxR += imageArray[y + i][x + j][0] * sobelX[i + 1][j + 1]
					gxG += imageArray[y + i][x + j][1] * sobelX[i + 1][j + 1]
					gxB += imageArray[y + i][x + j][2] * sobelX[i + 1][j + 1]

					gyR += imageArray[y + i][x + j][0] * sobelY[i + 1][j + 1]
					gyG += imageArray[y + i][x + j][1] * sobelY[i + 1][j + 1]
					gyB += imageArray[y + i][x + j][2] * sobelY[i + 1][j + 1]
				}
			}

			// Compute gradient magnitude
			gradientMagnitudeRGB[y][x][0] = sqrt(gxR * gxR + gyR * gyR).toInt().coerceIn(0..255)
			gradientMagnitudeRGB[y][x][1] = sqrt(gxG * gxG + gyG * gyG).toInt().coerceIn(0..255)
			gradientMagnitudeRGB[y][x][2] = sqrt(gxB * gxB + gyB * gyB).toInt().coerceIn(0..255)
		}
	}

	return gradientMagnitudeRGB
}