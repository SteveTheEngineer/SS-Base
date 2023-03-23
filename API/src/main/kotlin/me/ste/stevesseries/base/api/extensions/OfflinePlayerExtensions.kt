package me.ste.stevesseries.base.api.extensions

import me.ste.stevesseries.base.api.BaseAPI
import me.ste.stevesseries.base.api.storage.key.StorageKey
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer

fun <T> OfflinePlayer.getGlobalStorageKey(key: StorageKey<T>) =
    key.copy(id = NamespacedKey(key.id.namespace, "player_${this.uniqueId}_${key.id.key}"))

fun <T> OfflinePlayer.getOfflineStorage(key: StorageKey<T>): T {
    val globalKey = this.getGlobalStorageKey(key)
    return BaseAPI.getInstance().getStorageManager().getGlobalStorage(globalKey)
}

fun OfflinePlayer.removeOfflineStorage(key: StorageKey<*>) {
    val globalKey = this.getGlobalStorageKey(key)
    BaseAPI.getInstance().getStorageManager().removeGlobalStorage(globalKey)
}

fun OfflinePlayer.isOfflineStorage(key: StorageKey<*>): Boolean {
    val globalKey = this.getGlobalStorageKey(key)
    return BaseAPI.getInstance().getStorageManager().isGlobalStorage(globalKey)
}
