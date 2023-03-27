package me.ste.stevesseries.base

import me.ste.stevesseries.base.api.BaseAPI
import me.ste.stevesseries.base.api.command.CommandManager
import me.ste.stevesseries.base.api.extensions.dataPath
import me.ste.stevesseries.base.api.i18n.I18nManager
import me.ste.stevesseries.base.api.map.MapManager
import me.ste.stevesseries.base.command.CommandManagerImpl
import me.ste.stevesseries.base.i18n.I18nManagerImpl
import me.ste.stevesseries.base.map.MapManagerImpl
import me.ste.stevesseries.base.storage.FileStorageProvider
import me.ste.stevesseries.base.storage.StorageManagerImpl
import me.ste.stevesseries.base.storage.StorageSaveTask
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.function.Consumer
import kotlin.io.path.createDirectories

class BaseAPIImpl(
    private val plugin: Base
) : BaseAPI {
    private val storageManager = StorageManagerImpl(this.plugin)
    private val storageSaveTask = StorageSaveTask(this.plugin.logger, this.storageManager)
    private lateinit var storageSaveTimer: BukkitTask

    private val commandManager = CommandManagerImpl()
    private val mapManager = MapManagerImpl()
    private val i18nManager = I18nManagerImpl(this.plugin)

    // Internal
    fun onDisable() {
        this.storageSaveTimer.cancel()
        this.storageSaveTask.run()
        this.commandManager.onDisable()
    }

    fun onEnable() {
        this.storageSaveTimer = this.plugin.server.scheduler.runTaskTimerAsynchronously(plugin, storageSaveTask, 20L * 60L * 5L, 20L * 60L * 5L)
    }

    fun consumeChatInput(player: Player, value: String): Boolean {
        if (player.uniqueId !in this.chatInputListeners) {
            return false
        }

        this.chatInputListeners[player.uniqueId]!!.accept(value)
        this.chatInputListeners -= player.uniqueId

        return true
    }

    private val chatInputListeners = mutableMapOf<UUID, Consumer<String>>()

    // Implementation
    override fun getPlugin() = this.plugin

    override fun setChatInputListener(player: Player, listener: Consumer<String>?) {
        if (listener == null) {
            this.chatInputListeners -= player.uniqueId
            return
        }

        this.chatInputListeners[player.uniqueId] = listener
    }
    override fun getChatInputListener(player: Player): Consumer<String>? = this.chatInputListeners[player.uniqueId]

    override fun getStorageManager() = this.storageManager
    override fun getMapManager() = this.mapManager
    override fun getI18nManager() = this.i18nManager
    override fun getCommandManager() = this.commandManager
}