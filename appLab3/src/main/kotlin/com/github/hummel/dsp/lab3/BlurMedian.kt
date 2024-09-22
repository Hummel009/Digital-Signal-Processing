package com.github.hummel.dsp.lab3

fun applyMedianBlur(imageArray: Array<Array<IntArray>>, kernelSize: Int): Array<Array<IntArray>> {
	val height = imageArray.size
	val width = imageArray[0].size
	val padSize = kernelSize / 2
	val filteredImage = Array(height) { Array(width) { IntArray(3) } }

	for (y in padSize until height - padSize) {
		for (x in padSize until width - padSize) {
			for (c in 0 until channels) {
				val pixelValues = mutableListOf<Int>()
				for (dy in -padSize..padSize) {
					for (dx in -padSize..padSize) {
						pixelValues.add(imageArray[y + dy][x + dx][c])
					}
				}
				pixelValues.sort()
				filteredImage[y][x][c] = pixelValues[pixelValues.size / 2] // Get median value.
			}
		}
	}

	return filteredImage
}