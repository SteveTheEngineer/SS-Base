package me.ste.stevesseries.base.listener

import me.ste.stevesseries.base.api.event.EventManager
import me.ste.stevesseries.base.map.MapManagerImpl
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class MapPlayerListener(
    private val maps: MapManagerImpl
) : Listener {
    fun onPlayerJoin(event: PlayerJoinEvent) {
        for ((_, map) in this.maps.maps) {
            if (map.isContextual()) {
                map.update(event.player)
            } else {
                if (map.renderedImages.isEmpty()) {
                    map.update(event.player)
                    return
                }

                map.renderedImages[event.player.uniqueId] = map.renderedImages.values.first()
            }
        }
    }

    fun onPlayerQuit(event: PlayerQuitEvent) {
        for ((_, map) in this.maps.maps) {
            map.renderedImages -= event.player.uniqueId
        }
    }

    fun register(manager: EventManager) {
        manager.listen(listener = this::onPlayerJoin)
        manager.listen(listener = this::onPlayerQuit)
    }
}