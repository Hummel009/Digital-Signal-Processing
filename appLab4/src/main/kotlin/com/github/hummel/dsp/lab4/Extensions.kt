package com.github.hummel.dsp.lab4

import java.awt.Color
import java.awt.image.BufferedImage

fun BufferedImage.getPixel(x: Int, y: Int): Triple<Float, Float, Float> {
	val pixel = getRGB(x, y)

	//val alpha = (pixel shr 24 and 0xff) / 255.0f
	val red = (pixel shr 16 and 0xff) / 255.0f
	val green = (pixel shr 8 and 0xff) / 255.0f
	val blue = (pixel and 0xff) / 255.0f

	return Triple(red, green, blue)
}

fun BufferedImage.setPixel(x: Int, y: Int, color: Triple<Float, Float, Float>) {
	val red = (color.first.coerceIn(0.0f, 1.0f) * 255).toInt() and 0xff
	val green = (color.second.coerceIn(0.0f, 1.0f) * 255).toInt() and 0xff
	val blue = (color.third.coerceIn(0.0f, 1.0f) * 255).toInt() and 0xff

	val alpha = 255

	val pixel = (alpha shl 24) or (red shl 16) or (green shl 8) or blue

	setRGB(x, y, pixel)
}

fun BufferedImage.getGrayArray(): Array<FloatArray> {
	val result = Array(width) { FloatArray(height) }

	// Заполняем массив значениями яркости
	for (x in 0 until width) {
		for (y in 0 until height) {
			val (r, g, b) = getPixel(x, y)
			result[x][y] = (r + g + b) / 3
		}
	}

	// Вычисляем среднее значение яркости
	val avg = result.flatMap { it.asIterable() }.average().toFloat()

	// Корректируем значения яркости
	result.forEachIndexed { x, row ->
		row.forEachIndexed { y, value ->
			result[x][y] = value - avg
		}
	}

	return result
}

fun BufferedImage.selectRectangle(xStart: Int, yStart: Int, xEnd: Int, yEnd: Int) {
	val g2d = createGraphics()
	g2d.color = Color.RED
	g2d.drawRect(xStart, yStart, xEnd - xStart, yEnd - yStart)
	g2d.dispose()
}

fun BufferedImage.copy(): BufferedImage {
	val newImage = BufferedImage(width, height, type)

	copyData(newImage.raster)

	return newImage
}