package me.ste.stevesseries.base.api.util

import com.google.common.hash.Hashing
import com.google.common.io.ByteStreams
import me.ste.stevesseries.base.api.BaseAPI
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.file.Path
import java.util.function.Consumer
import java.util.function.Function
import java.util.logging.Level
import kotlin.io.path.*

object SafeIO {
    fun getNewFile(path: Path) = path.resolveSibling("${path.fileName}.new")

    fun <T> readFile(path: Path, read: Function<Path, T>): T {
        val newFile = this.getNewFile(path)

        if (newFile.isRegularFile()) {
            try {
                return read.apply(newFile)
            } catch (t: Throwable) {
                BaseAPI.getInstance().getPlugin().logger.log(Level.WARNING, "Failed to load the new version of file $path ($newFile). Will now try to load the old version.", t)
            }
        }

        if (path.isRegularFile()) {
            try {
                return read.apply(path)
            } catch (t: Throwable) {
                BaseAPI.getInstance().getPlugin().logger.log(Level.WARNING, "Failed to load the old version of file $path ($newFile).", t)
            }
        }

        throw RuntimeException("Failed to read file $path.")
    }

    fun writeFile(path: Path, write: Consumer<Path>) {
        val newFile = this.getNewFile(path)

        write.accept(newFile)
        newFile.copyTo(path, true)
        newFile.deleteExisting()
    }

    fun fileExists(path: Path): Boolean {
        val newFile = this.getNewFile(path)
        return path.isRegularFile() || newFile.isRegularFile()
    }

    fun deleteFile(path: Path) {
        val newFile = this.getNewFile(path)

        newFile.deleteIfExists()
        path.deleteIfExists()
    }

    fun readFileWithIntegrityData(path: Path) = this.readFile(path) {
        val stream = DataInputStream(it.inputStream())

        val size = stream.readInt()
        val hashSize = stream.readByte()

        val hash = ByteArray(hashSize.toInt())
        stream.read(hash, 0, hash.size)

        val data = stream.readBytes()

        if (data.size != size) {
            throw RuntimeException("Data size does not match the expected size (${data.size} != ${size}).")
        }

        val dataHash = Hashing.murmur3_32().hashBytes(data).asBytes()

        if (!hash.contentEquals(dataHash)) {
            throw RuntimeException("Data hash does not match the expected hash ([${dataHash.joinToString(",")}] != [${hash.joinToString(",")}]).")
        }

        stream.close()

        data
    }

    fun writeFileWithIntegrityData(path: Path, data: ByteArray) = this.writeFile(path) {
        val dataStream = DataOutputStream(it.outputStream())

        val hash = Hashing.murmur3_32().hashBytes(data).asBytes()

        dataStream.writeInt(data.size)
        dataStream.writeByte(hash.size)
        dataStream.write(hash)
        dataStream.write(data)

        dataStream.close()
    }
}