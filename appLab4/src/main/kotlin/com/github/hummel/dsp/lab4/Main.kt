package com.github.hummel.dsp.lab4

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.random.Random

fun main() {
	mdIfNot("output")

	val originalImage = ImageIO.read(File("output/basic_image.png"))
	val random = Random

	while (true) {
		val xStart = random.nextInt(originalImage.width * 1 / 8, originalImage.width * 3 / 8)
		val yStart = random.nextInt(originalImage.height * 1 / 8, originalImage.height * 3 / 8)
		val xEnd = random.nextInt(originalImage.width * 5 / 8, originalImage.width * 7 / 8)
		val yEnd = random.nextInt(originalImage.height * 5 / 8, originalImage.height * 7 / 8)

		val fragmentImage = originalImage.getSubimage(xStart, yStart, xEnd - xStart, yEnd - yStart)

		val (resultImage, function, maxX, maxY, maxCorrelation, minCorrelation) = processCorrelation(
			originalImage, fragmentImage
		)

		if (maxX != originalImage.width - 1 && maxY != originalImage.height - 1) {
			println("Max Correlation: $maxCorrelation at ($maxX, $maxY)")
			println("Min Correlation: $minCorrelation")

			ImageIO.write(fragmentImage, "png", File("output/fragment_image.png"))
			ImageIO.write(resultImage as BufferedImage, "png", File("output/highlight_image.png"))
			ImageIO.write(function as BufferedImage, "png", File("output/cfunction_image.png"))

			val res2img = processAutoCorrelation(originalImage)

			ImageIO.write(res2img, "png", File("output/acfunction_image.png"))

			break
		}
	}
}

operator fun <T> Array<T>.component6(): T = get(5)

fun mdIfNot(path: String): File {
	val soundsDir = File(path)
	if (!soundsDir.exists()) {
		soundsDir.mkdirs()
	}
	return soundsDir
}