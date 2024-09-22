package com.github.hummel.dsp.lab3

fun applyMedianBlur(imageArray: Array<Array<IntArray>>, boxSize: Int): Array<Array<IntArray>> {
	val (height, width) = extractDimensions(imageArray)
	val padSize = boxSize / 2

	val blurredImage = Array(height) { Array(width) { IntArray(channels) } }

	for (y in padSize until height - padSize) {
		for (x in padSize until width - padSize) {
			for (c in 0 until channels) {
				val pixelValues = mutableListOf<Int>()
				for (dy in -padSize..padSize) {
					for (dx in -padSize..padSize) {
						val newY = y + dy
						val newX = x + dx

						if (newY in 0 until height && newX in 0 until width) {
							pixelValues.add(imageArray[newY][newX][c])
						}
					}
				}
				pixelValues.sort()
				blurredImage[y][x][c] = pixelValues[pixelValues.size / 2] // Get median value.
			}
		}
	}

	return blurredImage
}