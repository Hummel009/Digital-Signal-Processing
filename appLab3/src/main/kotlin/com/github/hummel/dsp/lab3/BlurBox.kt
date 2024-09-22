package com.github.hummel.dsp.lab3

fun applyBoxBlur(imageArray: Array<Array<IntArray>>, kernelSize: Int): Array<Array<IntArray>> {
	val height = imageArray.size
	val width = imageArray[0].size
	val padSize = kernelSize / 2
	val filteredImage = Array(height) { Array(width) { IntArray(3) } }

	for (y in padSize until height - padSize) {
		for (x in padSize until width - padSize) {
			for (c in 0 until channels) {
				var sum = 0.0
				for (dy in -padSize..padSize) {
					for (dx in -padSize..padSize) {
						sum += imageArray[y + dy][x + dx][c]
					}
				}
				filteredImage[y][x][c] = (sum / (kernelSize * kernelSize)).toInt()
			}
		}
	}

	return filteredImage
}