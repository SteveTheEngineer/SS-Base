package me.ste.stevesseries.base.api.util.vector

import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.util.BlockVector

data class ChunkLocation(
    val x: Int,
    val z: Int
) {
    companion object {
        fun atBlock(block: BlockVector) = ChunkLocation(block.blockX shr 4, block.blockZ shr 4)
        fun atBlock(block: Block) = ChunkLocation(block.x shr 4, block.z shr 4)

        fun fromChunk(chunk: Chunk) = ChunkLocation(chunk.x, chunk.z)
    }

    fun getChunk(world: World) = world.getChunkAt(this.x, this.z)
}
