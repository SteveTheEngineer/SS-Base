package me.ste.stevesseries.base.api.extensions

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.ste.stevesseries.base.api.BaseAPI
import me.ste.stevesseries.base.api.command.CommandSource
import org.bukkit.command.Command
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.Consumer

fun JavaPlugin.registerCommand(builder: LiteralArgumentBuilder<CommandSource>, addPrefixedVariant: Boolean = true, modifier: Consumer<Command> = Consumer {}) =
    BaseAPI.getInstance().getCommandManager().register(this, builder, addPrefixedVariant, modifier)
