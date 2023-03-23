package me.ste.stevesseries.base.storage

import me.ste.stevesseries.base.Base
import me.ste.stevesseries.base.api.extensions.dataPath
import me.ste.stevesseries.base.api.storage.*
import me.ste.stevesseries.base.api.storage.key.StorageKey
import me.ste.stevesseries.base.api.storage.StorageHandler
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import java.io.ByteArrayOutputStream
import java.util.function.Supplier
import java.util.logging.Level
import kotlin.io.path.createDirectories

class StorageManagerImpl(
    private val plugin: Base
) : StorageManager {
    // Implementation State
    private val serializers = mutableMapOf<Class<*>, StorageHandler<*>>()

    val globalStorage = mutableMapOf<StorageKey<*>, Any?>()

    val fileProvider = NamespacedKey(this.plugin, "file")
    var currentProvider = this.fileProvider
    val providers = mutableMapOf<NamespacedKey, StorageProvider>(
        this.fileProvider to FileStorageProvider(
            this.plugin.dataPath.resolve("storage").createDirectories()
        )
    )
    // Internal Helper Functions
    fun <T> requireSerializer(type: Class<T>) =
        this.getHandler(type) ?: throw IllegalArgumentException("No storage serializer for $type.")

    private fun createContainerKey(key: StorageKey<*>) = NamespacedKey(key.id.namespace, "storage_${key.id.key}")

    // Provider Registry
    override fun getStorageProvider() = this.providers[currentProvider]!!

    override fun getStorageProviders() = this.providers

    override fun registerStorageProvider(key: NamespacedKey, provider: StorageProvider) {
        this.providers[key] = provider
    }

    override fun setStorageProvider(key: NamespacedKey) {
        if (key !in this.providers) {
            throw IllegalArgumentException("Storage provider $key is not registered.")
        }

        this.currentProvider = key
    }

    // Serializer Registry
    override fun <T> setHandler(type: Class<T>, serializer: StorageHandler<T>) {
        this.serializers[type] = serializer
    }

    override fun <T> getHandler(type: Class<T>) = this.serializers[type] as StorageHandler<T>?

    // Storage
    override fun <T> getHolderStorage(holder: PersistentDataHolder, key: StorageKey<T>): T {
        val serializer = this.requireSerializer(key.type)

        try {
            val containerKey = this.createContainerKey(key)
            val byteArray = holder.persistentDataContainer.get(containerKey, PersistentDataType.BYTE_ARRAY)
                ?: return serializer.default()
            return serializer.read(byteArray.inputStream())
        } catch (t: Throwable) {
            this.plugin.logger.log(Level.SEVERE, "Failed to load holder $holder storage: $key. Will revert to default value.", t)
            return serializer.default()
        }
    }

    override fun <T> setHolderStorage(holder: PersistentDataHolder, key: StorageKey<T>, data: T) {
        val serializer = this.requireSerializer(key.type)
        val containerKey = this.createContainerKey(key)

        try {
            val outputStream = ByteArrayOutputStream()
            serializer.write(data, outputStream)

            holder.persistentDataContainer.set(containerKey, PersistentDataType.BYTE_ARRAY, outputStream.toByteArray())
        } catch (t: Throwable) {
            this.plugin.logger.log(Level.SEVERE, "Failed to save holder $holder storage: $key.", t)
        }
    }

    override fun removeHolderStorage(holder: PersistentDataHolder, key: StorageKey<*>) {
        val containerKey = this.createContainerKey(key)
        holder.persistentDataContainer.remove(containerKey)
    }

    override fun isHolderStorage(holder: PersistentDataHolder, key: StorageKey<*>): Boolean {
        val containerKey = this.createContainerKey(key)
        return holder.persistentDataContainer.has(containerKey, PersistentDataType.BYTE_ARRAY)
    }

    override fun <T> getGlobalStorage(key: StorageKey<T>): T {
        val serializer = this.requireSerializer(key.type)

        try {
            if (key in this.globalStorage) {
                var data = this.globalStorage[key]

                if (data == null) {
                    data = serializer.default()
                    this.globalStorage[key] = data
                }

                return data as T
            }

            val value = this.getStorageProvider().loadGlobal(key)

            val data = if (value != null) serializer.read(value.inputStream()) else serializer.default()
            this.globalStorage[key] = data as Any

            return data
        } catch (t: Throwable) {
            this.plugin.logger.log(Level.SEVERE, "Failed to load global storage: $key. Will revert to default value.", t)
            return serializer.default()
        }
    }

    override fun removeGlobalStorage(key: StorageKey<*>) {
        this.getStorageProvider().deleteGlobal(key)
        this.globalStorage -= key
    }

    override fun isGlobalStorage(key: StorageKey<*>): Boolean {
        val serializer = this.requireSerializer(key.type)

        if (key in this.globalStorage) {
            return this.globalStorage[key] != null
        }

        return try {
            val value = this.getStorageProvider().loadGlobal(key)
            this.globalStorage[key] = if (value != null) serializer.read(value.inputStream()) else null

            value != null
        } catch (t: Throwable) {
            this.plugin.logger.log(Level.SEVERE, "Failed to load global storage: $key. Will return false.", t)
            false
        }
    }
}