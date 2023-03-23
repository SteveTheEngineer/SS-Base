package me.ste.stevesseries.base.api.extensions

import org.bukkit.event.block.Action

val Action.isRightClick get() = this == Action.RIGHT_CLICK_BLOCK || this == Action.RIGHT_CLICK_AIR
val Action.isLeftClick get() = this == Action.LEFT_CLICK_BLOCK || this == Action.LEFT_CLICK_AIR
