package com.github.hummel.dsp.lab3

fun applyBoxBlur(imageArray: Array<Array<IntArray>>, boxSize: Int): Array<Array<IntArray>> {
	val (height, width) = extractDimensions(imageArray)
	val padSize = boxSize / 2

	val blurredImage = Array(height) { Array(width) { IntArray(channels) } }

	for (y in 0 until height) {
		for (x in 0 until width) {
			for (c in 0 until channels) {
				var sum = 0.0f
				var count = 0

				for (dy in -padSize..padSize) {
					for (dx in -padSize..padSize) {
						val newY = y + dy
						val newX = x + dx

						if (newY in 0 until height && newX in 0 until width) {
							sum += imageArray[newY][newX][c]
							count++
						}
					}
				}

				blurredImage[y][x][c] = if (count > 0) (sum / count).toInt() else 0
			}
		}
	}

	return blurredImage
}