package me.ste.stevesseries.base.api.map.color

import org.bukkit.map.MapPalette
import org.bukkit.util.Vector
import java.awt.Color
import java.awt.color.ColorSpace

object MapColors {
    private val COLOR_MATCH_MAP = Array<Byte>(256 * 256 * 256) { 0 }

    val ALL_SHADES = mutableMapOf<Byte, MapColorShades>()
    val ALL_COLORS = mutableMapOf<Byte, MapColor>()

    private fun createColor(id: Byte, color: Color): MapColorShades {
        val color = MapColorShades(id, color)

        ALL_SHADES[id] = color
        ALL_COLORS += color.all

        return color
    }

    val NONE = createColor(0, Color(0, 0, 0, 0))
    val GRASS = createColor(1, Color(127, 178, 56))
    val SAND = createColor(2, Color(247, 233, 163))
    val WOOL = createColor(3, Color(199, 199, 199))
    val FIRE = createColor(4, Color(255, 0, 0))
    val ICE = createColor(5, Color(160, 160, 255))
    val METAL = createColor(6, Color(167, 167, 167))
    val PLANT = createColor(7, Color(0, 124, 0))
    val SNOW = createColor(8, Color(255, 255, 255))
    val CLAY = createColor(9, Color(164, 168, 184))
    val DIRT = createColor(10, Color(151, 109, 77))
    val STONE = createColor(11, Color(112, 112, 112))
    val WATER = createColor(12, Color(64, 64, 255))
    val WOOD = createColor(13, Color(143, 119, 72))
    val QUARTZ = createColor(14, Color(255, 252, 245))
    val COLOR_ORANGE = createColor(15, Color(216, 127, 51))
    val COLOR_MAGENTA = createColor(16, Color(178, 76, 216))
    val COLOR_LIGHT_BLUE = createColor(17, Color(102, 153, 216))
    val COLOR_YELLOW = createColor(18, Color(229, 229, 51))
    val COLOR_LIGHT_GREEN = createColor(19, Color(127, 204, 25))
    val COLOR_PINK = createColor(20, Color(242, 127, 165))
    val COLOR_GRAY = createColor(21, Color(76, 76, 76))
    val COLOR_LIGHT_GRAY = createColor(22, Color(153, 153, 153))
    val COLOR_CYAN = createColor(23, Color(76, 127, 153))
    val COLOR_PURPLE = createColor(24, Color(127, 63, 178))
    val COLOR_BLUE = createColor(25, Color(51, 76, 178))
    val COLOR_BROWN = createColor(26, Color(102, 76, 51))
    val COLOR_GREEN = createColor(27, Color(102, 127, 51))
    val COLOR_RED = createColor(28, Color(153, 51, 51))
    val COLOR_BLACK = createColor(29, Color(25, 25, 25))
    val GOLD = createColor(30, Color(250, 238, 77))
    val DIAMOND = createColor(31, Color(92, 219, 213))
    val LAPIS = createColor(32, Color(74, 128, 255))
    val EMERALD = createColor(33, Color(0, 217, 58))
    val PODZOL = createColor(34, Color(129, 86, 49))
    val NETHER = createColor(35, Color(112, 2, 0))
    val TERRACOTTA_WHITE = createColor(36, Color(209, 177, 161))
    val TERRACOTTA_ORANGE = createColor(37, Color(159, 82, 36))
    val TERRACOTTA_MAGENTA = createColor(38, Color(149, 87, 108))
    val TERRACOTTA_LIGHT_BLUE = createColor(39, Color(112, 108, 138))
    val TERRACOTTA_YELLOW = createColor(40, Color(186, 133, 36))
    val TERRACOTTA_LIGHT_GREEN = createColor(41, Color(103, 117, 53))
    val TERRACOTTA_PINK = createColor(42, Color(160, 77, 78))
    val TERRACOTTA_GRAY = createColor(43, Color(57, 41, 35))
    val TERRACOTTA_LIGHT_GRAY = createColor(44, Color(135, 107, 98))
    val TERRACOTTA_CYAN = createColor(45, Color(87, 92, 92))
    val TERRACOTTA_PURPLE = createColor(46, Color(122, 73, 88))
    val TERRACOTTA_BLUE = createColor(47, Color(76, 62, 92))
    val TERRACOTTA_BROWN = createColor(48, Color(76, 50, 35))
    val TERRACOTTA_GREEN = createColor(49, Color(76, 82, 42))
    val TERRACOTTA_RED = createColor(50, Color(142, 60, 46))
    val TERRACOTTA_BLACK = createColor(51, Color(37, 22, 16))
    val CRIMSON_NYLIUM = createColor(52, Color(189, 48, 49))
    val CRIMSON_STEM = createColor(53, Color(148, 63, 97))
    val CRIMSON_HYPHAE = createColor(54, Color(92, 25, 29))
    val WARPED_NYLIUM = createColor(55, Color(22, 126, 134))
    val WARPED_STEM = createColor(56, Color(58, 142, 140))
    val WARPED_HYPHAE = createColor(57, Color(86, 44, 62))
    val WARPED_WART_BLOCK = createColor(58, Color(20, 180, 133))
    val DEEPSLATE = createColor(59, Color(100, 100, 100))
    val RAW_IRON = createColor(60, Color(216, 175, 147))
    val GLOW_LICHEN = createColor(61, Color(127, 167, 150))

    fun getShade(id: Byte) = ALL_SHADES[id]
    fun getColor(index: Byte) = ALL_COLORS[index]

    fun matchColor(color: Color) = getColor(COLOR_MATCH_MAP[getRGB(color)])

    private fun getRGB(color: Color) = ((color.red and 255) shl 16) or ((color.green and 255) shl 8) or ((color.blue and 255) shl 0)

    init {
        for (red in 0..255) {
            for (green in 0..255) {
                for (blue in 0..255) { // I don't know of any ways to remove this nesting
                    val awtColor = Color(red, green, blue)

                    val closestColor = MapPalette.matchColor(awtColor)

                    val rgb = getRGB(awtColor)
                    COLOR_MATCH_MAP[rgb] = closestColor
                }
            }
        }
    }
}