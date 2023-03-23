package me.ste.stevesseries.base.api.storage.key

import org.bukkit.NamespacedKey

data class StorageKey<T>(
    val id: NamespacedKey,
    val type: Class<T>
)
