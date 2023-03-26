package me.ste.stevesseries.base.listener

import com.mojang.brigadier.tree.CommandNode
import me.ste.stevesseries.base.BaseAPIImpl
import me.ste.stevesseries.base.api.command.CommandSource
import me.ste.stevesseries.base.api.event.EventManager
import org.bukkit.command.CommandSender
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.event.server.ServerLoadEvent

class PluginListener(
    private val impl: BaseAPIImpl
) {
    fun onPluginDisable(event: PluginDisableEvent) {
        for (command in this.impl.getCommandManager().registered.get(event.plugin.name)?.toList() ?: emptyList()) {
            this.impl.getCommandManager().unregister(event.plugin, command as CommandNode<CommandSource>)
        }
    }

    fun register(manager: EventManager) {
        manager.listen(listener = this::onPluginDisable)
    }
}