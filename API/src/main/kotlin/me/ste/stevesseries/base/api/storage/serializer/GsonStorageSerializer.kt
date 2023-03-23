package me.ste.stevesseries.base.api.storage.serializer

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.InputStream
import java.io.OutputStream

class GsonStorageSerializer<T>(private val clazz: Class<T>,
                               private val gson: Gson = GsonBuilder().serializeNulls().setLenient()
                                   .enableComplexMapKeySerialization().create()
) : StorageSerializer<T> {
    override fun write(data: T, stream: OutputStream) {
        val bytes = this.gson.toJson(data).encodeToByteArray()
        stream.write(bytes)
    }

    override fun read(stream: InputStream): T {
        val json = stream.readBytes().decodeToString()
        return this.gson.fromJson(json, this.clazz)
    }
}