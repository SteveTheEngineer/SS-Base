package me.ste.stevesseries.base.api.storage.serializer

import java.io.InputStream
import java.io.OutputStream

interface StorageSerializer<T> {
    fun write(data: T, stream: OutputStream)
    fun read(stream: InputStream): T
}