package me.ste.stevesseries.base.chatinput

import org.bukkit.entity.Player
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap

object PlayerChatInput {
    val INPUT_LISTENERS: MutableMap<UUID, Consumer<String>> = HashMap()

    var Player.chatInputListener: Consumer<String>?
        get() = INPUT_LISTENERS[this.uniqueId]
        set(value) {
            if (value != null) {
                INPUT_LISTENERS[this.uniqueId] = value
            } else {
                INPUT_LISTENERS -= this.uniqueId
            }
        }
}