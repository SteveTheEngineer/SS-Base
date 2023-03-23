package me.ste.stevesseries.base.api.map

import me.ste.stevesseries.base.api.map.color.MapColor
import me.ste.stevesseries.base.api.map.color.MapColors
import org.bukkit.map.MapCanvas

class BaseMapCanvas(
    val width: Int,
    val height: Int
) {
    private val pixels = Array<Byte>(width * height) { 0 }

    private fun getBufferIndex(x: Int, y: Int) = y * this.width + x

    fun isInBounds(x: Int, y: Int) = x >= 0 && y >= 0 && x < this.width && y < this.height
    fun requireInBounds(x: Int, y: Int) {
        if (!this.isInBounds(x, y)) {
            throw IllegalArgumentException("Position out of bounds: $x, $y.")
        }
    }

    fun pixel(x: Int, y: Int, color: MapColor) {
        this.requireInBounds(x, y)
        this.pixels[this.getBufferIndex(x, y)] = color.index
    }

    fun pixel(x: Int, y: Int): MapColor {
        return MapColors.getColor(this.pixels[this.getBufferIndex(x, y)])!!
    }

    fun fill(x: Int, y: Int, width: Int, height: Int, color: MapColor) {
        if (width == 0 || height == 0) {
            return
        }

        if (width < 0 || height < 0) {
            throw IllegalArgumentException("Width ($width) and height ($height) can not be negative.")
        }

        this.requireInBounds(x, y)

        val endX = x + width - 1
        val endY = y + height - 1

        this.requireInBounds(endX, endY)

        for (cX in x..endX) {
            for (cY in y..endY) {
                this.pixels[this.getBufferIndex(cX, cY)] = color.index
            }
        }
    }

    fun copyTo(canvas: MapCanvas) {
        if (this.width != 128 || this.height != 128) {
            throw IllegalArgumentException("Copying to a Bukkit MapCanvas requires a 128x128 canvas.")
        }

        for ((index, pixel) in this.pixels.withIndex()) {
            canvas.setPixel(index % this.width, index / this.width, pixel)
        }
    }

    fun clear() {
        this.pixels.fill(0)
    }
}