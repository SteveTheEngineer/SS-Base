package me.ste.stevesseries.base.api.map

import me.ste.stevesseries.base.api.extensions.isTracking
import me.ste.stevesseries.base.api.extensions.requireItemMeta
import me.ste.stevesseries.base.api.util.vector.Vector2D
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapView
import java.util.*

abstract class MapHandler {
    lateinit var view: MapView

    val renderedImages: MutableMap<UUID, BaseMapCanvas> = mutableMapOf()

    open fun init() {}

    open fun isContextual() = false

    protected abstract fun BaseMapCanvas.render(player: Player)
    fun doRender(canvas: BaseMapCanvas, player: Player) {
        canvas.render(player)
    }

    open fun itemFrameClickUnsure(frame: ItemFrame, player: Player, click: MapClickType) = true
    open fun itemFrameClick(frame: ItemFrame, player: Player, position: Vector2D, click: MapClickType) = false

    fun update(player: Player) {
        this.renderedImages.clear()

        val canvas = if (player.uniqueId in this.renderedImages) this.renderedImages[player.uniqueId]!! else {
            val canvas = BaseMapCanvas(128, 128)
            this.renderedImages[player.uniqueId] = canvas
            canvas
        }
        canvas.clear()

        this.doRender(canvas, player)

        if (!this.isContextual()) {
            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                if (this.isTrackedBy(onlinePlayer)) {
                    onlinePlayer.sendMap(this.view)
                }
            }
        } else {
            if (this.isTrackedBy(player)) {
                player.sendMap(this.view)
            }
        }
    }

    fun isTrackedBy(player: Player): Boolean {
        for (stack in arrayOf(player.inventory.itemInMainHand, player.inventory.itemInOffHand)) {
            if (stack.type != Material.FILLED_MAP) {
                continue
            }

            val meta = stack.requireItemMeta() as MapMeta
            if (meta.mapView?.id != this.view.id) {
                continue
            }

            return true
        }

        for (entity in player.world.getEntitiesByClass(ItemFrame::class.java)) {
            val stack = entity.item
            if (stack.type != Material.FILLED_MAP) {
                continue
            }

            val meta = stack.requireItemMeta() as MapMeta
            if (meta.mapView?.id != this.view.id) {
                continue
            }

            if (!player.isTracking(entity)) {
                continue
            }

            return true
        }

        return false
    }
}