package me.ste.stevesseries.base.api.util.vector

import kotlin.math.floor

data class Vector2D(
    val x: Double,
    val y: Double
) {
    fun mapToGrid(width: Int, height: Int) = Vector2I(floor(width * this.x).toInt(), floor(height * this.y).toInt())
}
