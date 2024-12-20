package com.github.hummel.dsp.lab3

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

const val PI: Float = 3.1415927f

const val channels: Int = 3

fun main() {
	mdIfNot("output")

	val imagePath = "output/basic.jpg"
	val boxOutput = "output/blur_box.jpg"
	val gausOutput = "output/blur_gauss.jpg"
	val medianOutput = "output/blur_median.jpg"
	val sobelOutput = "output/sobel.jpg"

	val imageArray = loadImageAsArray(imagePath)

	val boxBlurredImage = applyBoxBlur(imageArray, 7)
	saveImageFromArray(boxBlurredImage, boxOutput)

	val gaussBlurredImage = applyGaussianBlur(imageArray, 14, 3.0f)
	saveImageFromArray(gaussBlurredImage, gausOutput)

	val medianBlurredImage = applyMedianBlur(imageArray, 5)
	saveImageFromArray(medianBlurredImage, medianOutput)

	val sobelImage = sobelOperator(imageArray)
	saveImageFromArray(sobelImage, sobelOutput)
}

private fun loadImageAsArray(imagePath: String): Array<Array<IntArray>> {
	val img: BufferedImage = ImageIO.read(File(imagePath))
	val width = img.width
	val height = img.height
	val imageArray = Array(height) { Array(width) { IntArray(3) } }

	for (y in 0 until height) {
		for (x in 0 until width) {
			val rgb = img.getRGB(x, y)
			imageArray[y][x][0] = (rgb shr 16) and 0xff // Red
			imageArray[y][x][1] = (rgb shr 8) and 0xff // Green
			imageArray[y][x][2] = rgb and 0xff // Blue
		}
	}

	return imageArray
}

private fun saveImageFromArray(imageArray: Array<Array<IntArray>>, outputPath: String) {
	val (height, width) = extractDimensions(imageArray)
	val img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

	for (y in 0 until height) {
		for (x in 0 until width) {
			val r = imageArray[y][x][0]
			val g = imageArray[y][x][1]
			val b = imageArray[y][x][2]
			img.setRGB(x, y, (r shl 16) or (g shl 8) or b)
		}
	}

	ImageIO.write(img, "jpg", File(outputPath))
}

private fun mdIfNot(path: String): File {
	val soundsDir = File(path)
	if (!soundsDir.exists()) {
		soundsDir.mkdirs()
	}
	return soundsDir
}

fun extractDimensions(imageArray: Array<Array<IntArray>>): Pair<Int, Int> {
	val height = imageArray.size
	val width = imageArray[0].size
	return height to width
}