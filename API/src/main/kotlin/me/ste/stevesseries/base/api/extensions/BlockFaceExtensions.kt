package me.ste.stevesseries.base.api.extensions

import org.bukkit.Axis
import org.bukkit.block.BlockFace

val BlockFace.axis get() = when (this) {
    BlockFace.DOWN, BlockFace.UP -> Axis.Y
    BlockFace.NORTH, BlockFace.SOUTH -> Axis.Z
    BlockFace.EAST, BlockFace.WEST -> Axis.X
    else -> throw IllegalArgumentException("Provided block face has no singular axis.")
}
