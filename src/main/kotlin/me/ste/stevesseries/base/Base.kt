package me.ste.stevesseries.base

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import me.ste.stevesseries.base.api.BaseAPI
import me.ste.stevesseries.base.api.command.CommandArguments
import me.ste.stevesseries.base.api.command.CommandSource
import me.ste.stevesseries.base.api.config.storage.YamlConfigStorage
import me.ste.stevesseries.base.api.event.EventManager
import me.ste.stevesseries.base.api.extensions.*
import me.ste.stevesseries.base.listener.ChatInputListener
import me.ste.stevesseries.base.listener.MapInteractListener
import me.ste.stevesseries.base.listener.MapPlayerListener
import me.ste.stevesseries.base.listener.PluginListener
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandSender
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
        PluginListener(this.impl).register(events)

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
