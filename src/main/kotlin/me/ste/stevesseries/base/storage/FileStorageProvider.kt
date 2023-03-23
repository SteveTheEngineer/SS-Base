package me.ste.stevesseries.base.storage

import com.google.common.hash.Hashing
import me.ste.stevesseries.base.api.storage.key.StorageKey
import me.ste.stevesseries.base.api.storage.StorageProvider
import me.ste.stevesseries.base.api.util.SafeIO
import org.bukkit.Bukkit
import java.io.DataInputStream
import java.nio.file.Path
import kotlin.io.path.*

class FileStorageProvider(
    rootDir: Path
) : StorageProvider {
    private val globalsDir = rootDir.resolve("global").createDirectories()

    private fun encodeKey(key: StorageKey<*>) = key.id.toString()

    private fun getGlobalFile(key: StorageKey<*>) =
        globalsDir.resolve(key.id.namespace).createDirectories().resolve("${key.id.key}.bin")

    override fun loadGlobal(key: StorageKey<*>): ByteArray? {
        val file = this.getGlobalFile(key)

        if (!file.isRegularFile()) {
            return null
        }

        return SafeIO.readFileWithIntegrityData(file)
    }

    override fun saveGlobal(key: StorageKey<*>, value: ByteArray) {
        val file = this.getGlobalFile(key)
        SafeIO.writeFileWithIntegrityData(file, value)
    }

    override fun deleteGlobal(key: StorageKey<*>) {
        val file = this.getGlobalFile(key)
        SafeIO.deleteFile(file)
    }
}