package me.ste.stevesseries.base

import me.ste.stevesseries.base.chatinput.ChatInputListener
import org.bukkit.plugin.java.JavaPlugin

class Base : JavaPlugin() {
    override fun onEnable() {
        this.server.pluginManager.registerEvents(ChatInputListener, this)
    }
}