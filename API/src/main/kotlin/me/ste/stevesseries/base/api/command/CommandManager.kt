package me.ste.stevesseries.base.api.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import org.bukkit.command.Command
import org.bukkit.plugin.Plugin
import java.util.function.Consumer

interface CommandManager {
    fun register(plugin: Plugin, builder: LiteralArgumentBuilder<CommandSource>, addPrefixedVariant: Boolean = true, modifier: Consumer<Command> = Consumer {}): LiteralCommandNode<CommandSource>
}