package me.ste.stevesseries.base;

import org.bukkit.Bukkit;

public class ReflectionUtil {
    public static final String NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    public static final String NMS_PACKAGE = "net.minecraft.server." + ReflectionUtil.NMS_VERSION;
    public static final String OBC_PACKAGE = "org.bukkit.craftbukkit." + ReflectionUtil.NMS_VERSION;

    private ReflectionUtil() {}

    public static <T> Class<T> resolveClass(String classPackage, String clazz) throws ClassNotFoundException {
        return (Class<T>) Class.forName(classPackage + "." + clazz);
    }
}