package me.ste.stevesseries.base.api.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import org.bukkit.plugin.Plugin

interface CommandManager {
    fun register(plugin: Plugin, builder: LiteralArgumentBuilder<CommandSource>, addPrefixedVariant: Boolean = true): LiteralCommandNode<CommandSource>
    fun unregister(plugin: Plugin, node: CommandNode<CommandSource>)
}