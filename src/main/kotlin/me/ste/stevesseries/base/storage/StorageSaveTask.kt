package me.ste.stevesseries.base.storage

import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.logging.Level
import java.util.logging.Logger

class StorageSaveTask(
    private val logger: Logger,
    private val manager: StorageManagerImpl
) : Runnable {
    override fun run() {
        // Globals
        for ((key, value) in this.manager.globalStorage) {
            if (value == null) {
                continue
            }

            try {
                val stream = ByteArrayOutputStream()
                this.writeData(stream, key.type, value)
                this.manager.getStorageProvider().saveGlobal(key, stream.toByteArray())
            } catch (t: Throwable) {
                this.logger.log(Level.SEVERE, "Failed to save global storage key: ${key}.", t)
            }
        }
    }

    private fun <T> writeData(stream: OutputStream, type: Class<T>, value: Any) {
        val serializer = this.manager.requireSerializer(type)
        serializer.write(value as T, stream)
    }
}