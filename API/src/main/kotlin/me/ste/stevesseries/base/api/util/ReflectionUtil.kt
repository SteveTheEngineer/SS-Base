package me.ste.stevesseries.base.api.util

import org.bukkit.Bukkit
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

object ReflectionUtil {
    private val packageName = Bukkit.getServer().javaClass.getPackage().name

    val MAPPINGS_VERSION = packageName.substring(packageName.lastIndexOf('.') + 1)
    val LEGACY_NMS_PACKAGE = "net.minecraft.server.$MAPPINGS_VERSION"
    val CRAFT_BUKKIT_PACKAGE = "org.bukkit.craftbukkit.$MAPPINGS_VERSION"

    fun getField(clazz: Class<*>, name: String): Field? {
        try {
            val field = clazz.getDeclaredField(name)
            field.isAccessible = true
            return field
        } catch (_: Throwable) {}

        val superClass = clazz.superclass ?: return null
        return this.getField(superClass, name)
    }

    fun getMethod(clazz: Class<*>, name: String, vararg parameterTypes: Class<*>): Method? {
        try {
            val method = clazz.getDeclaredMethod(name, *parameterTypes)
            method.isAccessible = true
            return method
        } catch (_: Throwable) {}

        val superClass = clazz.superclass ?: return null
        return this.getMethod(superClass, name)
    }
    fun <T> getConstructor(clazz: Class<T>, vararg parameterTypes: Class<*>): Constructor<T>? {
        try {
            val constructor = clazz.getDeclaredConstructor(*parameterTypes)
            constructor.isAccessible = true
            return constructor
        } catch (_: Throwable) {}

        val superClass = clazz.superclass ?: return null
        return this.getConstructor(clazz, *parameterTypes)
    }

    fun getFirstMethod(clazz: Class<*>, vararg names: String, parameterTypes: Array<Class<*>> = emptyArray()): Method? {
        for (name in names) {
            return this.getMethod(clazz, name, *parameterTypes) ?: continue
        }

        return null
    }

    fun getFirstClass(vararg names: String): Class<*>? {
        for (name in names) {
            try {
                return Class.forName(name)
            } catch (_: Throwable) {}
        }

        return null
    }

    fun getNMSClass(name: String) =
        getFirstClass(
            "${LEGACY_NMS_PACKAGE}.${
                name.substring(
                    name.lastIndexOf('.').coerceAtLeast(0)
                )
            }", "net.minecraft.$name"
        )
            ?: throw IllegalStateException("Failed to find NMS class: $name.")
}