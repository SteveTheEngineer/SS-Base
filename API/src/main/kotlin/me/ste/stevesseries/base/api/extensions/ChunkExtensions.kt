package me.ste.stevesseries.base.api.extensions

import me.ste.stevesseries.base.api.util.vector.ChunkLocation
import org.bukkit.Chunk

fun Chunk.toChunkLocation() = ChunkLocation.fromChunk(this)
