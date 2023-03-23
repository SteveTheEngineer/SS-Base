package me.ste.stevesseries.base.api.extensions

import me.ste.stevesseries.base.api.BaseAPI
import me.ste.stevesseries.base.api.map.MapHandler
import org.bukkit.map.MapView

fun MapView.attach(handler: MapHandler) {
    BaseAPI.getInstance().getMapManager().attach(this, handler)
}
