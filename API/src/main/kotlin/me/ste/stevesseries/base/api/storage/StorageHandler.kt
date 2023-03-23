package me.ste.stevesseries.base.api.storage

import me.ste.stevesseries.base.api.storage.serializer.StorageSerializer
import java.util.function.Supplier

class StorageHandler<T>(serializer: StorageSerializer<T>, private val defaultSupplier: Supplier<T>) :
    StorageSerializer<T> by serializer {
    fun default() = this.defaultSupplier.get()
}