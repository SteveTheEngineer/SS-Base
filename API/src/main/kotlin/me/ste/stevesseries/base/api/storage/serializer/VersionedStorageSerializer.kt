package me.ste.stevesseries.base.api.storage.serializer

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream

class VersionedStorageSerializer<T>(
    vararg versions: Pair<Int, StorageSerializer<T>>
) : StorageSerializer<T> {
    private val versions = mutableMapOf(*versions)
    private val latestVersion = this.versions.keys.max()

    override fun write(data: T, stream: OutputStream) {
        val dataStream = DataOutputStream(stream)

        dataStream.writeInt(this.latestVersion)
        this.versions[this.latestVersion]!!.write(data, dataStream)
    }

    override fun read(stream: InputStream): T {
        val dataStream = DataInputStream(stream)

        val versionNumber = dataStream.readInt()
        val version =
            this.versions[versionNumber] ?: throw IllegalStateException("Unsupported data version: $versionNumber.")

        return version.read(dataStream)
    }
}