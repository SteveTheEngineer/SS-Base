package me.ste.stevesseries.base

import me.ste.stevesseries.base.api.BaseAPI
import me.ste.stevesseries.base.api.config.storage.YamlConfigStorage
import me.ste.stevesseries.base.api.events.EventManager
import me.ste.stevesseries.base.api.extensions.*
import me.ste.stevesseries.base.api.map.color.MapColors
import me.ste.stevesseries.base.api.storage.key.StorageKey
import me.ste.stevesseries.base.listener.ChatInputListener
import me.ste.stevesseries.base.listener.MapInteractListener
import me.ste.stevesseries.base.listener.MapPlayerListener
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

class Base : JavaPlugin() {
    companion object {
        lateinit var INSTANCE: Base
    }

    init {
        INSTANCE = this
    }

    private val impl = BaseAPIImpl(this)

    override fun onLoad() {
        BaseAPI.setInstance(this.impl)
    }

    override fun onEnable() {
        this.impl.onEnable()

        val events = EventManager()

        ChatInputListener(this.impl).register(events)
        MapInteractListener(this.impl).register(events)
        MapPlayerListener(this.impl.getMapManager()).register(events)

        events.register(this)

//        MapColors // init

        // Config
        val config = BaseConfiguration(this.impl)
        config.sync(YamlConfigStorage(this.configPath))

        this.impl.getStorageManager().setStorageProvider(NamespacedKey.fromString(config.storageProvider)!!)
        this.impl.getI18nManager().setLocaleProviders(config.localeProviders.map { NamespacedKey.fromString(it)!! }.toTypedArray())
    }

    override fun onDisable() {
        this.impl.onDisable()
    }
}
