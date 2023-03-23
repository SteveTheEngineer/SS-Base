package me.ste.stevesseries.base.api.storage

import me.ste.stevesseries.base.api.storage.key.StorageKey
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataHolder

interface StorageManager {
    // Provider
    fun getStorageProvider(): StorageProvider
    fun getStorageProviders(): Map<NamespacedKey, StorageProvider>
    fun registerStorageProvider(key: NamespacedKey, provider: StorageProvider)
    fun setStorageProvider(key: NamespacedKey)

    // Handler
    fun <T> setHandler(type: Class<T>, serializer: StorageHandler<T>)
    fun <T> getHandler(type: Class<T>): StorageHandler<T>?

    // Persistent Data Holder
    fun <T> getHolderStorage(holder: PersistentDataHolder, key: StorageKey<T>): T
    fun <T> setHolderStorage(holder: PersistentDataHolder, key: StorageKey<T>, data: T)
    fun removeHolderStorage(holder: PersistentDataHolder, key: StorageKey<*>)
    fun isHolderStorage(holder: PersistentDataHolder, key: StorageKey<*>): Boolean

    // Global
    fun <T> getGlobalStorage(key: StorageKey<T>): T
    fun removeGlobalStorage(key: StorageKey<*>)
    fun isGlobalStorage(key: StorageKey<*>): Boolean
}
