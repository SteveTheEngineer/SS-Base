package me.ste.stevesseries.base.map

import me.ste.stevesseries.base.api.extensions.axis
import me.ste.stevesseries.base.api.extensions.times
import me.ste.stevesseries.base.api.map.MapHandler
import me.ste.stevesseries.base.api.util.vector.Vector2D
import org.bukkit.Axis
import org.bukkit.Rotation
import org.bukkit.block.BlockFace
import org.bukkit.entity.ItemFrame
import org.bukkit.util.Vector
import kotlin.math.absoluteValue

data class MapRayTraceResult(
    val entity: ItemFrame,
    val handler: MapHandler,
    val position: Vector
) {
    private fun transformCoord(value: Double) = if (value > 0.0) 1.0 - value else value.absoluteValue

    fun getMapPosition(): Vector2D {
        val facing = this.entity.facing
        val direction = facing.direction
        val axis = facing.axis

        val worldUp =
            if (axis == Axis.Y) {
                if (facing == BlockFace.UP) {
                    Vector(0.0, 0.0, -1.0)
                } else {
                    Vector(0.0, 0.0, 1.0)
                }
            } else {
                Vector(0.0, 1.0, 0.0)
            }

        when (this.entity.rotation) {
            Rotation.CLOCKWISE_45, Rotation.FLIPPED_45 -> worldUp.rotateAroundAxis(direction, -Math.PI / 2.0)
            Rotation.CLOCKWISE, Rotation.COUNTER_CLOCKWISE -> worldUp * -1.0
            Rotation.CLOCKWISE_135, Rotation.COUNTER_CLOCKWISE_45 -> worldUp.rotateAroundAxis(direction, Math.PI / 2.0)
            else -> {}
        }

        val right = direction.getCrossProduct(worldUp)
        val up = right.getCrossProduct(direction)

        val positionFraction = Vector(
            this.position.x % 1.0,
            this.position.y % 1.0,
            this.position.z % 1.0
        )

        val verticalFraction = positionFraction * up
        val horizontalFraction = positionFraction * right

        return Vector2D(
            this.transformCoord(horizontalFraction.x + horizontalFraction.y + horizontalFraction.z),
            this.transformCoord(verticalFraction.x + verticalFraction.y + verticalFraction.z)
        )
    }
}
