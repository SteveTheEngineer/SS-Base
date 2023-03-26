package me.ste.stevesseries.base.api.event

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.util.function.Consumer

class EventManager {
    private val listener = object : Listener {}

    private val queue = mutableListOf<Consumer<Plugin>>()
    private var registered = false

    fun <T : Event> listen(
        type: Class<T>,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        listener: Consumer<T>
    ) {
        this.queue += Consumer {
            it.server.pluginManager.registerEvent(type, this.listener, priority, { _, event ->
                if (!type.isInstance(event)) {
                    return@registerEvent
                }

                listener.accept(event as T)
            }, it, ignoreCancelled)
        }
    }

    inline fun <reified T : Event> listen(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        listener: Consumer<T>
    ) {
        this.listen(T::class.java, priority, ignoreCancelled, listener)
    }

    fun register(plugin: Plugin) {
        if (this.registered) {
            throw IllegalStateException("Attempted to register duplicate listeners.")
        }
        this.registered = true

        for (element in this.queue) {
            element.accept(plugin)
        }

        this.queue.clear()
    }
}