package me.ste.stevesseries.base.listener

import me.ste.stevesseries.base.BaseAPIImpl
import me.ste.stevesseries.base.api.event.EventManager
import me.ste.stevesseries.base.api.extensions.*
import me.ste.stevesseries.base.api.map.MapClickType
import me.ste.stevesseries.base.api.map.MapHandler
import me.ste.stevesseries.base.map.MapRayTraceResult
import org.bukkit.Axis
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerAnimationType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.util.BoundingBox
import java.util.UUID
import kotlin.math.roundToInt

class MapInteractListener(
    private val impl: BaseAPIImpl
) {
    private val ignoreInteract = mutableSetOf<UUID>()
    private val ignoreAnimation = mutableSetOf<UUID>()

    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) {
            return
        }

        if (event.player.uniqueId in this.ignoreInteract) {
            this.ignoreInteract -= event.player.uniqueId
            return
        }

        if (event.action.isLeftClick && event.player.gameMode == GameMode.SURVIVAL) {
            this.ignoreAnimation += event.player.uniqueId

            if (this.onInteract(event.player, MapClickType.LEFT)) {
                event.setUseItemInHand(Event.Result.DENY)
                event.setUseInteractedBlock(Event.Result.DENY)
            }
        }

        if (event.action.isRightClick) {
            if (this.onInteract(event.player, MapClickType.RIGHT)) {
                event.setUseItemInHand(Event.Result.DENY)
                event.setUseInteractedBlock(Event.Result.DENY)
            }
        }
    }

    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val frame = event.rightClicked as? ItemFrame ?: return
        val handStack = event.player.inventory.getItem(event.hand)

        if (frame.item.type != Material.AIR || (handStack != null && handStack.type != Material.AIR)) {
            this.ignoreAnimation += event.player.uniqueId
        }

        if (event.hand != EquipmentSlot.HAND) {
            val handler = this.getFrameHandler(frame)
            if (handler?.itemFrameClickUnsure(frame, event.player, MapClickType.RIGHT) == true) {
                event.isCancelled = true
            }

            return
        }

        if (this.onInteract(event.player, MapClickType.RIGHT, event.rightClicked as? ItemFrame)) {
            event.isCancelled = true
        }
    }

    fun onPlayerAttack(event: EntityDamageByEntityEvent) {
        val player = event.damager as? Player ?: return

        if (player.gameMode == GameMode.SURVIVAL) {
            if (this.onInteract(player, MapClickType.LEFT, event.entity as? ItemFrame)) {
                event.isCancelled = true
            }
        } else {
            val frame = event.entity as? ItemFrame ?: return

            val handler = this.getFrameHandler(frame)
            if (handler?.itemFrameClickUnsure(frame, player, MapClickType.LEFT) == true) {
                event.isCancelled = true
            }
        }
    }

    fun onHangingBreak(event: HangingBreakByEntityEvent) {
        val player = event.remover as? Player ?: return

        if (player.gameMode == GameMode.SURVIVAL) {
            if (this.onInteract(player, MapClickType.LEFT, event.entity as? ItemFrame)) {
                event.isCancelled = true
            }
        } else {
            val frame = event.entity as? ItemFrame ?: return

            val handler = this.getFrameHandler(frame)
            if (handler?.itemFrameClickUnsure(frame, player, MapClickType.LEFT) == true) {
                event.isCancelled = true
            }
        }
    }

    fun onPlayerAnimation(event: PlayerAnimationEvent) {
        if (event.animationType != PlayerAnimationType.ARM_SWING) {
            return
        }

        if (event.player.uniqueId in this.ignoreAnimation) {
            this.ignoreAnimation -= event.player.uniqueId
            return
        }

        if (event.player.gameMode == GameMode.SURVIVAL && event.player.getTargetBlockExact(event.player.getBuildingReach().roundToInt()) != null) {
            return
        }

        this.onInteract(event.player, MapClickType.LEFT)
    }

    private fun onItemDrop(event: PlayerDropItemEvent) {
        this.ignoreAnimation += event.player.uniqueId
    }

    private fun getFrameHandler(entity: ItemFrame): MapHandler? {
        val stack = entity.item

        if (stack.type != Material.FILLED_MAP) {
            return null
        }

        val meta = stack.requireItemMeta() as MapMeta
        val view = meta.mapView ?: return null

        return this.impl.getMapManager().getHandler(view)
    }

    private fun getMapBoundingBox(entity: ItemFrame): BoundingBox {
        val facing = entity.facing
        val axis = facing.axis

        val box = entity.boundingBox

        // Expand the bounding box
        if (axis != Axis.Y) {
            box.expand(0.0, 2.0 / 16.0, 0.0)
        }

        if (axis == Axis.X || axis == Axis.Y) {
            box.expand(0.0, 0.0, 2.0 / 16.0)
        }

        if (axis == Axis.Z || axis == Axis.Y) {
            box.expand(2.0 / 16.0, 0.0, 0.0)
        }

        box.expand(facing, 0.00789)

        return box
    }

    private fun getFramesInReach(player: Player, entity: ItemFrame? = null): Iterable<ItemFrame> {
        if (entity != null) {
            return listOf(entity)
        }

        val world = player.world
        val maxDistance = player.getAttackReach().toDouble() + 1.0 / 16.0 / 2.0 // Compensate for item frame width

        return world.getNearbyEntities(player.eyeLocation, maxDistance, maxDistance, maxDistance) { it is ItemFrame } as Iterable<ItemFrame>
    }

    private fun rayTraceFrames(player: Player, entities: Iterable<ItemFrame>): MapRayTraceResult? {
        val reach = player.getAttackReach().toDouble()
        val source = player.eyeLocation.toVector()
        val direction = player.location.direction

        // First make sure that no block obstructs the item frame
        val blockResult = player.rayTraceBlocks(reach)

        var closestDistance = blockResult?.hitPosition?.distanceSquared(source) ?: Double.MAX_VALUE
        var finalResult: MapRayTraceResult? = null

        for (entity in entities) {
            val handler = this.getFrameHandler(entity) ?: continue
            val box = this.getMapBoundingBox(entity)

            val result = box.rayTrace(source, direction, reach) ?: continue
            val position = result.hitPosition
            val distance = position.distanceSquared(source)

            if (result.hitBlockFace != entity.facing) {
                continue
            }

            if (distance > closestDistance) {
                continue
            }

            closestDistance = distance
            finalResult = MapRayTraceResult(entity, handler, position)
        }

        return finalResult
    }

    private fun onInteract(player: Player, click: MapClickType, targetEntity: ItemFrame? = null): Boolean {
        if (player.gameMode == GameMode.SPECTATOR) {
            return false
        }

        val targetHandler = if (targetEntity != null) this.getFrameHandler(targetEntity) else null

        val entities = this.getFramesInReach(player, targetEntity)
        val result = this.rayTraceFrames(player, entities) ?: return targetHandler != null

        val position = result.getMapPosition()

        return result.handler.itemFrameClick(result.entity, player, position, click)
    }

    fun register(manager: EventManager) {
        manager.listen(listener = this::onPlayerInteract)
        manager.listen(listener = this::onPlayerInteractEntity)
        manager.listen(listener = this::onPlayerAttack)
        manager.listen(listener = this::onHangingBreak)
        manager.listen(listener = this::onPlayerAnimation)
        manager.listen(listener = this::onItemDrop)
    }
}