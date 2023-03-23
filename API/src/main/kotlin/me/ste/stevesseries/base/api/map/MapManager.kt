package me.ste.stevesseries.base.api.map

import org.bukkit.map.MapView

interface MapManager {
    fun attach(view: MapView, handler: MapHandler)
    fun getHandler(view: MapView): MapHandler?
}