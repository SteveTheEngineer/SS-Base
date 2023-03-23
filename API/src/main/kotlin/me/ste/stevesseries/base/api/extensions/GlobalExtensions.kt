package me.ste.stevesseries.base.api.extensions

import me.ste.stevesseries.base.api.BaseAPI
import me.ste.stevesseries.base.api.storage.key.StorageKey

fun <T> getGlobalStorage(key: StorageKey<T>) =
    BaseAPI.getInstance().getStorageManager().getGlobalStorage(key)

fun removeGlobalStorage(key: StorageKey<*>) {
    BaseAPI.getInstance().getStorageManager().removeGlobalStorage(key)
}

fun isGlobalStorage(key: StorageKey<*>) =
    BaseAPI.getInstance().getStorageManager().isGlobalStorage(key)

