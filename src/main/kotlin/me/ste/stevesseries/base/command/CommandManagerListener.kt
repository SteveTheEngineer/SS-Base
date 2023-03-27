package me.ste.stevesseries.base.command

import me.ste.stevesseries.base.Base
import me.ste.stevesseries.base.BaseAPIImpl
import me.ste.stevesseries.base.api.event.EventManager
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerCommandSendEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.event.server.PluginEnableEvent
import org.bukkit.event.server.ServerCommandEvent

class CommandManagerListener(
    private val plugin: Base,
    private val impl: BaseAPIImpl
) {
    private val dummyCommand = "thisisahackywaytotriggertheunknowncommandmessagepleaseignore"

    fun onPluginDisable(event: PluginDisableEvent) {
        for (command in this.impl.getCommandManager().registered.get(event.plugin.name)?.toList() ?: emptyList()) {
            this.impl.getCommandManager().unregister(event.plugin.name, command)
        }
    }

    fun onPluginEnable(event: PluginEnableEvent) {
        this.plugin.server.scheduler.runTask(this.plugin, Runnable {
            this.impl.getCommandManager().modifyHelpMap()
        })
    }

    fun onPlayerCommandSend(event: PlayerCommandSendEvent) {
        this.impl.getCommandManager().removeRedundantCommands(event.commands)
    }

    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val command = event.message.substring(1)

        if (!this.isRedundantCommand(command)) {
            return
        }

        event.isCancelled = true
        event.player.performCommand(this.dummyCommand)
    }

    fun onServerCommand(event: ServerCommandEvent) {
        if (!this.isRedundantCommand(event.command)) {
            return
        }

        event.isCancelled = true
        this.plugin.server.dispatchCommand(event.sender, this.dummyCommand)
    }

    private fun isRedundantCommand(message: String) =
        this.impl.getCommandManager().isRedundantCommand(message.split(' ').first())

    fun register(manager: EventManager) {
        manager.listen(listener = this::onPluginDisable)
        manager.listen(listener = this::onPluginEnable)
        manager.listen(listener = this::onPlayerCommandSend)
        manager.listen(listener = this::onPlayerCommandPreprocess)
        manager.listen(listener = this::onServerCommand)
    }
}