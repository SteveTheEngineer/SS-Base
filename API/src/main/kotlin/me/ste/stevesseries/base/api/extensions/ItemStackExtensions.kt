package me.ste.stevesseries.base.api.extensions

import me.ste.stevesseries.base.api.storage.key.StorageKey
import org.bukkit.inventory.ItemStack

fun ItemStack.requireItemMeta() = this.itemMeta ?: throw IllegalStateException("Item stack has no item meta.")

fun <T> ItemStack.getStorage(key: StorageKey<T>): T {
    val meta = this.requireItemMeta()
    return meta.getStorage(key)
}

fun <T> ItemStack.setStorage(key: StorageKey<T>, data: T) {
    val meta = this.requireItemMeta()
    meta.setStorage(key, data)
    this.itemMeta = meta
}

fun ItemStack.removeStorage(key: StorageKey<*>) {
    val meta = this.requireItemMeta()
    meta.removeStorage(key)
    this.itemMeta = meta
}

fun ItemStack.isStorage(key: StorageKey<*>): Boolean {
    val meta = this.requireItemMeta()
    return meta.isStorage(key)
}