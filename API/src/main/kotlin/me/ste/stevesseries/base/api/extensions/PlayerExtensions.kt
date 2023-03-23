package me.ste.stevesseries.base.api.extensions

import me.ste.stevesseries.base.api.BaseAPI
import org.bukkit.Chunk
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.function.Consumer
import kotlin.math.absoluteValue
import kotlin.math.max

fun Player.acceptChatInput(consumer: Consumer<String>) {
    BaseAPI.getInstance().setChatInputListener(this, consumer)
}

fun Player.cancelChatInput() {
    BaseAPI.getInstance().setChatInputListener(this, null)
}

fun Player.isTracking(chunk: Chunk): Boolean {
    val ownChunk = this.location.chunk

    val distance = max(
        (chunk.x - ownChunk.x).absoluteValue,
        (chunk.z - ownChunk.z).absoluteValue
    )

    return distance <= this.server.viewDistance
}

fun Player.isTracking(block: Block) = this.isTracking(block.chunk)

fun Player.isTracking(entity: Entity) = this.isTracking(entity.location.chunk)

fun Player.isTracking(location: Location) = this.isTracking(location.chunk)

fun Player.getBuildingReach() = when (this.gameMode) {
    GameMode.CREATIVE -> 5F
    else -> 4.5F
}

fun Player.getAttackReach() = when (this.gameMode) {
    GameMode.CREATIVE -> 5F
    else -> 3F
}

fun Player.i18n(translationKey: NamespacedKey, arguments: Map<String, String> = emptyMap()) =
    BaseAPI.getInstance().getI18nManager().translate(this, translationKey, arguments)
