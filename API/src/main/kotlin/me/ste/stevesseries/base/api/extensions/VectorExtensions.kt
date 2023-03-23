package me.ste.stevesseries.base.api.extensions

import org.bukkit.util.Vector

operator fun Vector.plus(other: Vector) = this.clone().add(other)
operator fun Vector.minus(other: Vector) = this.clone().subtract(other)
operator fun Vector.times(other: Vector) = this.clone().multiply(other)
operator fun Vector.div(other: Vector) = this.clone().divide(other)

operator fun Vector.times(factor: Double) = this.clone().multiply(factor)
operator fun Vector.times(factor: Int) = this.clone().multiply(factor)
operator fun Vector.times(factor: Float) = this.clone().multiply(factor)

operator fun Vector.plusAssign(other: Vector) {
    this.add(other)
}
operator fun Vector.minusAssign(other: Vector) {
    this.subtract(other)
}
operator fun Vector.timesAssign(other: Vector) {
    this.multiply(other)
}
operator fun Vector.divAssign(other: Vector) {
    this.divide(other)
}

operator fun Vector.timesAssign(factor: Double) {
    this.multiply(factor)
}
operator fun Vector.timesAssign(factor: Int) {
    this.multiply(factor)
}
operator fun Vector.timesAssign(factor: Float) {
    this.multiply(factor)
}
