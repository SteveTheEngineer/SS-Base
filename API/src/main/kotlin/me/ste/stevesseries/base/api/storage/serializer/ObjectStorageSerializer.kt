package me.ste.stevesseries.base.api.storage.serializer

import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream

class ObjectStorageSerializer<T> : StorageSerializer<T> {
    override fun write(data: T, stream: OutputStream) {
        val objectStream = ObjectOutputStream(stream)
        objectStream.writeObject(data)
    }

    override fun read(stream: InputStream): T {
        val objectStream = ObjectInputStream(stream)
        return objectStream.readObject() as T
    }
}