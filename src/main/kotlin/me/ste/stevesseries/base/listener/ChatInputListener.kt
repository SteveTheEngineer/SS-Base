package me.ste.stevesseries.base.listener

import me.ste.stevesseries.base.BaseAPIImpl
import me.ste.stevesseries.base.api.events.EventManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.plugin.Plugin

class ChatInputListener(
    private val impl: BaseAPIImpl
) : Listener {
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        if (this.impl.consumeChatInput(event.player, event.message)) {
            event.isCancelled = true
        }
    }

    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        if (this.impl.consumeChatInput(event.player, event.message)) {
            event.isCancelled = true
        }
    }

    fun register(manager: EventManager) {
        manager.listen(listener = this::onPlayerChat)
        manager.listen(listener = this::onPlayerCommand)
    }
}