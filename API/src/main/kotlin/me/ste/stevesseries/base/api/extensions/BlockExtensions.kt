package me.ste.stevesseries.base.api.extensions

import me.ste.stevesseries.base.api.storage.key.StorageKey
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.util.BlockVector

fun Block.toBlockVector() = BlockVector(this.x, this.y, this.z)

private fun <T> Block.getChunkStorageKey(key: StorageKey<T>) =
    StorageKey(NamespacedKey(key.id.namespace, "block_${this.x}_${this.y}_${this.z}_${key.id.key}"), key.type)

fun <T> Block.getStorage(key: StorageKey<T>): T {
    val chunkKey = this.getChunkStorageKey(key)
    return this.chunk.getStorage(chunkKey)
}

fun <T> Block.setStorage(key: StorageKey<T>, data: T) {
    val chunkKey = this.getChunkStorageKey(key)
    this.chunk.setStorage(chunkKey, data)
}

fun Block.removeStorage(key: StorageKey<*>) {
    val chunkKey = this.getChunkStorageKey(key)
    this.chunk.removeStorage(chunkKey)
}

fun Block.isStorage(key: StorageKey<*>): Boolean {
    val chunkKey = this.getChunkStorageKey(key)
    return this.chunk.isStorage(chunkKey)
}
