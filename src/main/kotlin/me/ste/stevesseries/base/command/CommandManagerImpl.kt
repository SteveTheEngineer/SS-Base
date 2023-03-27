package me.ste.stevesseries.base.command

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import me.ste.stevesseries.base.api.command.CommandManager
import me.ste.stevesseries.base.api.command.CommandSource
import me.ste.stevesseries.base.api.util.ReflectionUtil
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.SimpleCommandMap
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.help.HelpMap
import org.bukkit.help.HelpTopic
import org.bukkit.help.IndexHelpTopic
import org.bukkit.plugin.Plugin
import java.util.function.Consumer

class CommandManagerImpl : CommandManager {
    val registered: ListMultimap<String, LiteralCommandNode<Any>> = ArrayListMultimap.create()
    private val prefixed = mutableMapOf<LiteralCommandNode<Any>, LiteralCommandNode<Any>>()

    private val server = Bukkit.getServer()
    private val dedicatedServer = this.server::class.java.getDeclaredMethod("getServer").invoke(this.server)
    private val commandDispatcher = this.dedicatedServer::class.java.superclass.getDeclaredField("vanillaCommandDispatcher").get(this.dedicatedServer)
    private val brigadierDispatcher = this.commandDispatcher::class.java.getDeclaredMethod("a").invoke(this.commandDispatcher) as CommandDispatcher<Any>
    private val vanillaCommands = try {
        this.commandDispatcher::class.java.getDeclaredField("vanillaCommandNodes").get(this.commandDispatcher) as MutableCollection<CommandNode<Any>>
    } catch (_: Throwable) { null }

    private val commandMap = this.server::class.java.getDeclaredMethod("getCommandMap").invoke(this.server) as SimpleCommandMap
    private val knownCommands = this.commandMap::class.java.getDeclaredMethod("getKnownCommands").invoke(this.commandMap) as MutableMap<String, Command>

    private val helpMap = this.server::class.java.getDeclaredMethod("getHelpMap").invoke(this.server) as HelpMap
    private val helpTopics = ReflectionUtil.getField(this.helpMap::class.java, "helpTopics")!!.get(this.helpMap) as MutableMap<String, HelpTopic>

    private val vanillaCommandWrapperConstructor = Class.forName("${ReflectionUtil.CRAFT_BUKKIT_PACKAGE}.command.VanillaCommandWrapper").getConstructor(this.commandDispatcher::class.java, CommandNode::class.java)

    fun onDisable() {
        for (command in this.registered.entries().toList()) {
            this.unregister(command.key, command.value)
        }
    }

    override fun register(
        plugin: Plugin,
        builder: LiteralArgumentBuilder<CommandSource>,
        addPrefixedVariant: Boolean,
        modifier: Consumer<Command>
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

            val command = this.vanillaCommandWrapperConstructor.newInstance(this.commandDispatcher, aliasNode) as BukkitCommand
            this.applyDefaults(command)
            modifier.accept(command)
            this.commandMap.register("minecraft", command)
            this.knownCommands -= "minecraft:${aliasNode.literal}"
        }

        val command = this.vanillaCommandWrapperConstructor.newInstance(this.commandDispatcher, node) as BukkitCommand
        this.applyDefaults(command)
        modifier.accept(command)
        this.commandMap.register("minecraft", command)
        this.knownCommands -= "minecraft:${node.literal}"

        return node as LiteralCommandNode<CommandSource>
    }

     fun unregister(pluginName: String, anyNode: CommandNode<Any>) {
        // Skip unregistered nodes
        if (anyNode !in this.registered[pluginName]) {
            return
        }

        val children = this.brigadierDispatcher.root.children

        // Remove base node
        children.remove(anyNode)
        this.vanillaCommands?.remove(anyNode)
        this.registered.remove(pluginName, anyNode)

        // Remove prefixed node
        val prefixed = this.prefixed[anyNode] ?: return

        children.remove(prefixed)
        this.vanillaCommands?.remove(prefixed)
        this.prefixed -= prefixed
    }

    fun modifyHelpMap() {
        val minecraft = this.helpTopics["Minecraft"] as IndexHelpTopic
        val minecraftTopics = ReflectionUtil.getField(minecraft::class.java, "allTopics")!!.get(minecraft) as MutableCollection<HelpTopic>

        val indexes: ListMultimap<String, HelpTopic> = ArrayListMultimap.create()

        for ((pluginName, node) in this.registered.entries()) {
            val prefixedNode = this.prefixed[node]

            val nodeTopic = minecraftTopics.find { it.name == "/${node.literal}" }
            val prefixedNodeTopic = prefixedNode?.let { prefixedNodeNonNull -> minecraftTopics.find { it.name == "/${prefixedNodeNonNull.literal}" } }

            if (nodeTopic != null) {
                val mcTopic = "/minecraft:${node.literal}"

                indexes.put(pluginName, nodeTopic)

                this.helpTopics.remove(mcTopic)

                minecraftTopics.remove(nodeTopic)
                minecraftTopics.removeIf { it.name == mcTopic }
            }

            if (prefixedNodeTopic != null) {
                val mcTopic = "/minecraft:${prefixedNode.literal}"

                this.helpTopics.remove(prefixedNodeTopic.name)
                this.helpTopics.remove(mcTopic)

                minecraftTopics.remove(prefixedNodeTopic)
                minecraftTopics.removeIf { it.name == mcTopic }
            }
        }

        for (name in indexes.keySet()) {
            val topics = indexes.get(name)
            this.helpTopics[name] = IndexHelpTopic(name, "All commands for $name", null, topics, "Below is a list of all $name commands:")
        }
    }

    private fun applyDefaults(command: Command) {
        command.description = ""
    }

    fun removeRedundantCommands(commands: MutableCollection<String>) {
        for (node in this.registered.values()) {
            commands.remove("minecraft:${node.literal}")
        }

        for (node in this.prefixed.values) {
            commands.remove("minecraft:${node.literal}")
        }
    }

    fun isRedundantCommand(command: String): Boolean {
        for (node in this.registered.values()) {
            if (command == "minecraft:${node.literal}") {
                return true
            }
        }

        for (node in this.prefixed.values) {
            if (command == "minecraft:${node.literal}") {
                return true
            }
        }

        return false
    }
}
