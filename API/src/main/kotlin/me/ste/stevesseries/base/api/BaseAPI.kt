package me.ste.stevesseries.base.api

import me.ste.stevesseries.base.api.i18n.I18nManager
import me.ste.stevesseries.base.api.map.MapManager
import me.ste.stevesseries.base.api.storage.StorageManager
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.function.Consumer

interface BaseAPI {
    companion object {
        private var api: BaseAPI? = null

        fun getInstance() =
            api ?: throw IllegalStateException("The Base API has not yet been initialized!")

        fun setInstance(api: BaseAPI) {
            if (this.api != null) {
                throw IllegalStateException("A Base API instance has already been set!")
            }

            this.api = api
        }
    }

    fun getPlugin(): Plugin

    fun setChatInputListener(player: Player, listener: Consumer<String>?)
    fun getChatInputListener(player: Player): Consumer<String>?

    fun getStorageManager(): StorageManager
    fun getMapManager(): MapManager
    fun getI18nManager(): I18nManager
}