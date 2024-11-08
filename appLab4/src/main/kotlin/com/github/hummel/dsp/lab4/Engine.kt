package com.github.hummel.dsp.lab4

import java.awt.image.BufferedImage
import kotlin.math.pow
import kotlin.math.sqrt

fun getMagnitude(originalImage: BufferedImage, fragmentImage: BufferedImage, x: Int, y: Int): Double {
	var sumOriginalCrop = 0.0
	var sumOriginal = 0.0
	var sumCrop = 0.0
	var count = 0

	for (i in x until fragmentImage.width + x) {
		if (i >= originalImage.width) {
			continue
		}

		for (j in y until fragmentImage.height + y) {
			if (j >= originalImage.height) {
				continue
			}

			val (cropR, cropG, cropB) = fragmentImage.getPixel(i - x, j - y)
			val cropValue = (cropR + cropG + cropB) / 3
			sumCrop += cropValue.pow(2)

			val (originalR, originalG, originalB) = originalImage.getPixel(i, j)
			val originalValue = (originalR + originalG + originalB) / 3.0
			sumOriginal += originalValue.pow(2)

			sumOriginalCrop += originalValue * cropValue
			count++
		}
	}

	return sumOriginalCrop / (sqrt(sumCrop) * sqrt(sumOriginal))
}

fun processCorrelation(originalImage: BufferedImage, partOfImage: BufferedImage): Array<Any> {
	val width = originalImage.width
	val height = originalImage.height

	val resultImage = originalImage.copy()

	val function = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

	var maxCorrelation = Double.NEGATIVE_INFINITY
	var minCorrelation = Double.POSITIVE_INFINITY
	var maxX = 0
	var maxY = 0

	for (i in 0 until width) {
		for (j in 0 until height) {
			val correlationValue = getMagnitude(originalImage, partOfImage, i, j)

			function.setPixel(
				i, j, Triple(
					correlationValue.toFloat(), correlationValue.toFloat(), correlationValue.toFloat()
				)
			)

			if (correlationValue > maxCorrelation) {
				maxCorrelation = correlationValue
				maxX = i
				maxY = j
			}

			if (correlationValue < minCorrelation) {
				minCorrelation = correlationValue
			}
		}
	}

	resultImage.selectRectangle(maxX, maxY, maxX + partOfImage.width, maxY + partOfImage.height)

	return arrayOf(resultImage, function, maxX, maxY, maxCorrelation, minCorrelation)
}

fun processAutoCorrelation(originalImage: BufferedImage): BufferedImage {
	val width = originalImage.width
	val height = originalImage.height

	val resultImg = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

	val xC = width / 2
	val yC = height / 2

	val originalGrayArray = originalImage.getGrayArray()
	val resultImgArray = Array(width) { FloatArray(height) }

	for (x in 0 until width) {
		for (y in 0 until height) {
			var S = 0.0
			var F = 0.0 // dispersion for one
			var G = 0.0 // dispersion for last

			for (i in x - xC until x + xC) {
				for (j in y - yC until y + yC) {
					if (i < 0 || j < 0 || i >= width || j >= height) continue

					S += originalGrayArray[i][j] * originalGrayArray[i + xC - x][j + yC - y]
					F += originalGrayArray[i][j].toDouble().pow(2)
					G += originalGrayArray[i + xC - x][j + yC - y].toDouble().pow(2)
				}
			}

			resultImgArray[x][y] = maxOf(S / sqrt(F * G), 0.0).toFloat()
		}
	}

	for (i in resultImgArray.indices) {
		for (j in resultImgArray[i].indices) {
			resultImg.setPixel(i, j, Triple(resultImgArray[i][j], resultImgArray[i][j], resultImgArray[i][j]))
		}
	}

	return resultImg
}