package com.github.hummel.dsp.lab3

fun applyBoxBlur(imageArray: Array<Array<IntArray>>, boxSize: Int): Array<Array<IntArray>> {
	val (height, width) = extractDimensions(imageArray)
	val padSize = boxSize / 2

	val blurredImage = Array(height) { Array(width) { IntArray(channels) } }

	for (y in padSize until height - padSize) {
		for (x in padSize until width - padSize) {
			for (c in 0 until channels) {
				var sum = 0.0f
				for (dy in -padSize..padSize) {
					for (dx in -padSize..padSize) {
						val newY = y + dy
						val newX = x + dx

						if (newY in 0 until height && newX in 0 until width) {
							sum += imageArray[newY][newX][c]
						}
					}
				}
				val area = boxSize * boxSize
				blurredImage[y][x][c] = (sum / area).toInt()
			}
		}
	}

	return blurredImage
}