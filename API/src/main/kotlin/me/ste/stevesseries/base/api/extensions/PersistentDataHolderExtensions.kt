package me.ste.stevesseries.base.api.extensions

import me.ste.stevesseries.base.api.BaseAPI
import me.ste.stevesseries.base.api.storage.key.StorageKey
import org.bukkit.persistence.PersistentDataHolder

fun <T> PersistentDataHolder.getStorage(key: StorageKey<T>) =
    BaseAPI.getInstance().getStorageManager().getHolderStorage(this, key)

fun <T> PersistentDataHolder.setStorage(key: StorageKey<T>, data: T) {
    BaseAPI.getInstance().getStorageManager().setHolderStorage(this, key, data)
}

fun PersistentDataHolder.removeStorage(key: StorageKey<*>) {
    BaseAPI.getInstance().getStorageManager().removeHolderStorage(this, key)
}

fun PersistentDataHolder.isStorage(key: StorageKey<*>): Boolean =
    BaseAPI.getInstance().getStorageManager().isHolderStorage(this, key)
