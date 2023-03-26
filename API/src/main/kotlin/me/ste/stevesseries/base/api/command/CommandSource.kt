package me.ste.stevesseries.base.api.command

import me.ste.stevesseries.base.api.util.ReflectionUtil
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.lang.reflect.InvocationTargetException

class CommandSource(
    val obj: Any
) {
    companion object {
        private val VANILLA_COMMAND_WRAPPER = Class.forName("${ReflectionUtil.CRAFT_BUKKIT_PACKAGE}.command.VanillaCommandWrapper")
        private val COMMAND_SOURCE = ReflectionUtil.getNMSClass("commands.ICommandListener")
        private val VEC_3D = ReflectionUtil.getNMSClass("world.phys.Vec3D")
        private val VEC_2F = ReflectionUtil.getNMSClass("world.phys.Vec2F")
        private val WORLD_SERVER = ReflectionUtil.getNMSClass("server.level.WorldServer")

        private val VEC_3D_CONSTRUCTOR = VEC_3D.getConstructor(Double::class.javaPrimitiveType!!, Double::class.javaPrimitiveType!!, Double::class.javaPrimitiveType!!)
        private val VEC_2F_CONSTRUCTOR = VEC_2F.getConstructor(Float::class.javaPrimitiveType!!, Float::class.javaPrimitiveType!!)

        private val GET_LISTENER_METHOD = VANILLA_COMMAND_WRAPPER.getDeclaredMethod("getListener", CommandSender::class.java)
    }

    val sender get() = this.obj::class.java.getDeclaredMethod("getBukkitSender").invoke(this.obj) as CommandSender
    val location get() = this.obj::class.java.getDeclaredMethod("getBukkitLocation").invoke(this.obj) as Location

    val entity: Entity? get() {
        val mcEntity = this.obj::class.java.getDeclaredMethod("f").invoke(this.obj) ?: return null
        return ReflectionUtil.getMethod(mcEntity::class.java, "getBukkitEntity")!!.invoke(mcEntity) as Entity
    }

    val player: Player? get() {
        val mcPlayer = this.obj::class.java.getDeclaredMethod("i").invoke(this.obj) ?: return null
        return ReflectionUtil.getMethod(mcPlayer::class.java, "getBukkitEntity")!!.invoke(mcPlayer) as Player
    }

    val entityOrException: Entity get() {
        val mcEntity: Any

        try {
            mcEntity = this.obj::class.java.getDeclaredMethod("g").invoke(this.obj)
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }

        return ReflectionUtil.getMethod(mcEntity::class.java, "getBukkitEntity")!!.invoke(mcEntity) as Entity
    }

    val playerOrException: Player get() {
        val mcPlayer: Any

        try {
            mcPlayer = this.obj::class.java.getDeclaredMethod("h").invoke(this.obj)
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }

        return  ReflectionUtil.getMethod(mcPlayer::class.java, "getBukkitEntity")!!.invoke(mcPlayer) as Player
    }

    fun withSender(sender: CommandSender): CommandSource {
        val listener = GET_LISTENER_METHOD.invoke(null, sender)
        val source = listener::class.java.getDeclaredField("c").get(listener)

        val sourceStack = this.obj::class.java.getDeclaredMethod("a", COMMAND_SOURCE)

        return CommandSource(sourceStack.invoke(this.obj, source))
    }

    fun withLocation(location: Location): CommandSource {
        if (location.world == null) {
            throw IllegalStateException("Location world may not be null!")
        }

        val position = VEC_3D_CONSTRUCTOR.newInstance(location.x, location.y, location.z)
        val rotation = VEC_2F_CONSTRUCTOR.newInstance(location.pitch, location.yaw)
        val level = location.world!!::class.java.getMethod("getHandle").invoke(location.world)

        var sourceStack = this.obj::class.java.getDeclaredMethod("a", VEC_3D).invoke(this.obj, position)
        sourceStack = this.obj::class.java.getDeclaredMethod("a", VEC_2F).invoke(sourceStack, rotation)
        sourceStack = this.obj::class.java.getDeclaredMethod("a", WORLD_SERVER).invoke(sourceStack, level)

        return CommandSource(sourceStack)
    }
}