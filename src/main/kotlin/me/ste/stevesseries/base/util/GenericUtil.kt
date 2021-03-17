package me.ste.stevesseries.base.util

import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import java.util.*

object GenericUtil {
    fun parseNamespacedKey(defaultNamespace: String?, namespacedKey: String): NamespacedKey? {
        val split = namespacedKey.split(":")
        return when {
            namespacedKey.matches(Regex("[a-z0-9/._-]+:[a-z0-9/._-]+")) -> {
                NamespacedKey(split[0], split[1])
            }
            namespacedKey.matches(Regex("[a-z0-9/._-]+")) -> {
                NamespacedKey(defaultNamespace!!, namespacedKey)
            }
            else -> {
                null
            }
        }
    }

    fun getBlocksInBounds(a: BlockVector, b: BlockVector): Set<BlockVector> {
        val blocks: MutableSet<BlockVector> = HashSet()

        val min = Vector.getMinimum(a, b)
        val max = Vector.getMaximum(a, b)

        for (x in min.blockX..max.blockX) {
            for (y in min.blockY..max.blockY) {
                for (z in min.blockZ..max.blockZ) {
                    blocks.add(BlockVector())
                }
            }
        }

        return blocks
    }

    fun Location.locationToJson(): JsonObject {
        val jsonObject = JsonObject()
        if (this.world != null) {
            jsonObject.addProperty("world", this.world!!.uid.toString())
        }
        jsonObject.addProperty("x", this.x)
        jsonObject.addProperty("y", this.y)
        jsonObject.addProperty("z", this.z)
        jsonObject.addProperty("yaw", this.yaw)
        jsonObject.addProperty("pitch", this.pitch)
        return jsonObject
    }

    fun JsonObject.locationFromJson() = Location(
        if (this.has("world")) {
            Bukkit.getWorld(UUID.fromString(this.get("world").asString))
        } else {
            null
        },
        this.get("x").asDouble,
        this.get("y").asDouble,
        this.get("z").asDouble,
        this.get("yaw").asFloat,
        this.get("pitch").asFloat
    )
}