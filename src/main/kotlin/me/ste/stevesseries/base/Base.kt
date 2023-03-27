package me.ste.stevesseries.base

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import me.ste.stevesseries.base.api.BaseAPI
import me.ste.stevesseries.base.api.command.CommandArguments
import me.ste.stevesseries.base.api.command.CommandSource
import me.ste.stevesseries.base.api.config.storage.YamlConfigStorage
import me.ste.stevesseries.base.api.event.EventManager
import me.ste.stevesseries.base.api.extensions.*
import me.ste.stevesseries.base.api.map.BaseMapCanvas
import me.ste.stevesseries.base.api.map.MapClickType
import me.ste.stevesseries.base.api.map.MapHandler
import me.ste.stevesseries.base.api.map.color.MapColors
import me.ste.stevesseries.base.api.util.vector.Vector2D
import me.ste.stevesseries.base.api.util.vector.Vector2I
import me.ste.stevesseries.base.map.listener.MapInteractListener
import me.ste.stevesseries.base.map.listener.MapPlayerListener
import me.ste.stevesseries.base.command.CommandManagerListener
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.entity.Damageable
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
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
        CommandManagerListener(this, this.impl).register(events)

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
