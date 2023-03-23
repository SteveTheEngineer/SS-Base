package me.ste.stevesseries.base.api.storage

import me.ste.stevesseries.base.api.storage.key.StorageKey

interface StorageProvider {
    fun loadGlobal(key: StorageKey<*>): ByteArray?
    fun saveGlobal(key: StorageKey<*>, value: ByteArray)
    fun deleteGlobal(key: StorageKey<*>)
}