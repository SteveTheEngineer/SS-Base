package me.ste.stevesseries.base.chatinput

import me.ste.stevesseries.base.chatinput.PlayerChatInput.chatInputListener
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object ChatInputListener : Listener {
    private fun handleMessage(player: Player, message: String): Boolean {
        val listener = player.chatInputListener
        if (listener != null) {
            listener.accept(message)
            player.chatInputListener = null
            return true
        }
        return false
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        event.isCancelled = this.handleMessage(event.player, event.message) || event.isCancelled
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        event.isCancelled = this.handleMessage(event.player, event.message) || event.isCancelled
    }
}