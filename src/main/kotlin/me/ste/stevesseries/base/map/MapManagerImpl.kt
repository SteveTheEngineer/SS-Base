package me.ste.stevesseries.base.map

import me.ste.stevesseries.base.api.map.MapHandler
import me.ste.stevesseries.base.api.map.MapManager
import org.bukkit.Bukkit
import org.bukkit.map.MapView

class MapManagerImpl : MapManager {
    val maps = mutableMapOf<Int, MapHandler>()

    override fun attach(view: MapView, handler: MapHandler) {
        if (view.id in this.maps) {
            throw IllegalArgumentException("Map ID ${view.id} already has a handler attached.")
        }

        handler.view = view
        view.addRenderer(MapHandlerRenderer(handler))
        this.maps[view.id] = handler

        if (handler.isContextual()) {
            for (player in Bukkit.getOnlinePlayers()) {
                handler.update(player)
            }
        } else {
            val player = Bukkit.getOnlinePlayers().firstOrNull() ?: return
            handler.update(player)
        }
    }

    override fun getHandler(view: MapView) = this.maps[view.id]
}