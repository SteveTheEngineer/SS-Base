package me.ste.stevesseries.base.api.util

import org.bukkit.plugin.Plugin
import java.util.function.Consumer

object PluginBootstrap {
    fun checkPlugin(plugin: Plugin, pluginName: String, consumer: Consumer<Plugin>) {
        val target = plugin.server.pluginManager.getPlugin(pluginName) ?: return
        consumer.accept(target)
    }
}