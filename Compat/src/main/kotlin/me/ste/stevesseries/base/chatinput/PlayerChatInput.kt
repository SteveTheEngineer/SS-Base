package me.ste.stevesseries.base.chatinput

import me.ste.stevesseries.base.api.BaseAPI
import me.ste.stevesseries.base.api.extensions.acceptChatInput
import me.ste.stevesseries.base.api.extensions.cancelChatInput
import me.ste.stevesseries.base.chatinput.PlayerChatInput.chatInputListener
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Consumer

@Deprecated("For backwards compatibility only.")
object PlayerChatInput {
    @Deprecated("Removed, always throws an error. Should never have been used by plugins.", level = DeprecationLevel.ERROR)
    val INPUT_LISTENERS: MutableMap<UUID, Consumer<String>> = throw UnsupportedOperationException("INPUT_LISTENERS is not available in the Compat module of Base. If you were using this feature, please create an issue on GitHub.")

    var Player.chatInputListener: Consumer<String>?
        @Deprecated("Use BaseAPI.getInstance().getChatInputListener(player) instead.")
        get() = BaseAPI.getInstance().getChatInputListener(this)

        @Deprecated("Use acceptChatInput or cancelChatInput instead.")
        set(value) {
            if (value != null) {
                this.acceptChatInput(value)
            } else {
                this.cancelChatInput()
            }
        }
}
