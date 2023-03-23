package me.ste.stevesseries.base.api.map.color

import java.awt.Color

class MapColorShades(
    id: Byte,
    color: Color
) {
    val all = mutableMapOf<Byte, MapColor>()

    val darker = this.createColor(id, color, 0, 180)
    val dark = this.createColor(id, color, 1, 220)
    val normal = this.createColor(id, color, 2, 255)
    val darkest = this.createColor(id, color, 3, 135)

    private fun createColor(id: Byte, color: Color, shift: Byte, factor: Int): MapColor {
        val index = (id * 4 + shift).toByte()

        val color = MapColor(
            index,
            Color(
                color.red * factor / 255,
                color.green * factor / 255,
                color.blue * factor / 255,
                color.alpha
            )
        )

        this.all[index] = color

        return color
    }
}