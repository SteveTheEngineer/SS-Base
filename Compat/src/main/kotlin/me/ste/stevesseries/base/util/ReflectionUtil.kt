package me.ste.stevesseries.base.util

import org.bukkit.Bukkit

@Deprecated("For backwards compatibility only.")
object ReflectionUtil {
    private val packageName = Bukkit.getServer().javaClass.getPackage().name

    val NMS_VERSION = packageName.substring(packageName.lastIndexOf('.') + 1)
    val NMS_PACKAGE = "net.minecraft.server.${NMS_VERSION}"
    val OBC_PACKAGE = "org.bukkit.craftbukkit.${NMS_VERSION}"

    fun resolveClass(javaPackage: String, className: String) = Class.forName("$javaPackage.$className")
}