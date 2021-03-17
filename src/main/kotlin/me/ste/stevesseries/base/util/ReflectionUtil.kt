package me.ste.stevesseries.base.util

import org.bukkit.Bukkit

object ReflectionUtil {
    val NMS_VERSION = Bukkit.getServer().javaClass.getPackage().name.split("\\.").toTypedArray()[3]
    val NMS_PACKAGE = "net.minecraft.server.${NMS_VERSION}"
    val OBC_PACKAGE = "org.bukkit.craftbukkit.${NMS_VERSION}"

    fun resolveClass(javaPackage: String, className: String) = Class.forName("$javaPackage.$className")
}