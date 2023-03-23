package me.ste.stevesseries.base.api.extensions

import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin

val Plugin.dataPath get() = this.dataFolder.toPath()
val Plugin.configPath get() = this.dataPath.resolve("config.yml")

val Plugin.namespace get() = NamespacedKey(this, "dummy").namespace
