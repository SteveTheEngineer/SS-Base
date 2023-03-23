package me.ste.stevesseries.base.api.extensions

import org.bukkit.World
import org.bukkit.util.BlockVector

fun BlockVector.toBlock(world: World) = world.getBlockAt(this.blockX, this.blockY, this.blockZ)
