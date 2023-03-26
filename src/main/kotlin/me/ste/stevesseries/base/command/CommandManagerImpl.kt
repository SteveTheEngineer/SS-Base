package me.ste.stevesseries.base.command

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import me.ste.stevesseries.base.api.command.CommandArguments
import me.ste.stevesseries.base.api.command.CommandManager
import me.ste.stevesseries.base.api.command.CommandSource
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.function.Predicate

class CommandManagerImpl : CommandManager {
    val registered: ListMultimap<String, CommandNode<Any>> = ArrayListMultimap.create()
    private val prefixed = mutableMapOf<CommandNode<Any>, CommandNode<Any>>()

    private val server = Bukkit.getServer()
    private val dedicatedServer = this.server::class.java.getDeclaredMethod("getServer").invoke(this.server)
    private val commandDispatcher = this.dedicatedServer::class.java.superclass.getDeclaredField("vanillaCommandDispatcher").get(this.dedicatedServer)
    private val brigadierDispatcher = this.commandDispatcher::class.java.getDeclaredMethod("a").invoke(this.commandDispatcher) as CommandDispatcher<Any>
    private val vanillaCommands = try {
        this.commandDispatcher::class.java.getDeclaredField("vanillaCommandNodes").get(this.commandDispatcher) as MutableCollection<CommandNode<Any>>
    } catch (_: Throwable) { null }

    override fun register(
        plugin: Plugin,
        builder: LiteralArgumentBuilder<CommandSource>,
        addPrefixedVariant: Boolean
    ): LiteralCommandNode<CommandSource> {
        // Register base node
        val node = this.brigadierDispatcher.register(CommandNodeConversions.convertNode(builder))
        this.vanillaCommands?.add(node)
        this.registered.put(plugin.name, node)

        // Figure out the prefixed variant
        if (addPrefixedVariant) {
            // If the node is a redirect node, then take all the parameters from it. Otherwise, it won't work
            val redirect = node.redirect ?: node as CommandNode<Any>
            val redirectModifier = node.redirectModifier as? RedirectModifier<Any>
            val fork = node.isFork

            // Register the alias
            val aliasName = "${plugin.name.lowercase()}:${node.literal}"

            val aliasBuilder =
                if (node.children.isNotEmpty()) {
                    LiteralArgumentBuilder.literal<Any>(aliasName)
                        .forward(redirect as CommandNode<Any>, redirectModifier, fork)
                } else {
                    val builder = node.createBuilder()

                    val field = builder::class.java.getDeclaredField("literal")
                    field.isAccessible = true
                    field.set(builder, aliasName)

                    builder
                }

            val aliasNode = this.brigadierDispatcher.register(aliasBuilder)
            this.vanillaCommands?.add(aliasNode)
            this.prefixed[node] = aliasNode
        }

        return node as LiteralCommandNode<CommandSource>
    }

    override fun unregister(plugin: Plugin, node: CommandNode<CommandSource>) {
        val anyNode = node as CommandNode<Any>

        // Skip unregistered nodes
        if (anyNode !in this.registered[plugin.name]) {
            return
        }

        val children = this.brigadierDispatcher.root.children

        // Remove base node
        children.remove(anyNode)
        this.vanillaCommands?.remove(anyNode)
        this.registered.remove(plugin.name, anyNode)

        // Remove prefixed node
        val prefixed = this.prefixed[anyNode] ?: return

        children.remove(prefixed)
        this.vanillaCommands?.remove(prefixed)
        this.prefixed -= prefixed
    }
}
