package me.ste.stevesseries.base.api.extensions

import com.mojang.brigadier.tree.LiteralCommandNode
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.Predicate

val Plugin.dataPath get() = this.dataFolder.toPath()
val Plugin.configPath get() = this.dataPath.resolve("config.yml")

val Plugin.namespace get() = NamespacedKey(this, "dummy").namespace
