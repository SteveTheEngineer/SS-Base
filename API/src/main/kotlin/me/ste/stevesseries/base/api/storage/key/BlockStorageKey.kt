package me.ste.stevesseries.base.api.storage.key

import org.bukkit.util.BlockVector

data class BlockStorageKey<T>(
    val base: StorageKey<T>,
    val block: BlockVector
)
