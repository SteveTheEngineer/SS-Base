package me.ste.stevesseries.base.api.storage.key

import java.util.*

data class UUIDStorageKey<T>(
    val base: StorageKey<T>,
    val worldId: UUID
)
