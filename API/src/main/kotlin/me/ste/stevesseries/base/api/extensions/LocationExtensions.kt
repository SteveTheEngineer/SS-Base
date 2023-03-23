package me.ste.stevesseries.base.api.extensions

import org.bukkit.Location
import org.bukkit.util.Vector

operator fun Location.plus(other: Location) = this.clone().add(other)
operator fun Location.plus(other: Vector) = this.clone().add(other)
operator fun Location.minus(other: Location) = this.clone().subtract(other)
operator fun Location.minus(other: Vector) = this.clone().subtract(other)

operator fun Location.times(factor: Double) = this.clone().multiply(factor)

operator fun Location.plusAssign(other: Location) {
    this.add(other)
}
operator fun Location.plusAssign(other: Vector) {
    this.add(other)
}
operator fun Location.minusAssign(other: Location) {
    this.subtract(other)
}
operator fun Location.minusAssign(other: Vector) {
    this.subtract(other)
}

operator fun Location.timesAssign(factor: Double) {
    this.multiply(factor)
}
