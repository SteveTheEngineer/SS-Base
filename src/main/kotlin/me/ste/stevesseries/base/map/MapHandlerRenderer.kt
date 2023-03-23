package me.ste.stevesseries.base.map

import me.ste.stevesseries.base.api.map.MapHandler
import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView

class MapHandlerRenderer(
    private val handler: MapHandler
) : MapRenderer(handler.isContextual()) {
    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        this.handler.renderedImages[player.uniqueId]?.copyTo(canvas)
    }
}