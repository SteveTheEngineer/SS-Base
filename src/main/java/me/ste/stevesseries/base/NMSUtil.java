package me.ste.stevesseries.base;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public final class NMSUtil {
    private NMSUtil() {}

    public static String getNMSVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getNMSVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    public static Class<?> getOBCClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getNMSVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    public static Object getHandle(Player p) {
        if(p == null) return null;
        try {
            return p.getClass().getMethod("getHandle").invoke(p);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return null;
        }
    }
    public static Object getConnection(Player p) {
        Object handle = getHandle(p);
        try {
            return handle.getClass().getField("playerConnection").get(handle);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return null;
        }
    }
    public static void sendPacket(Player p, Object packet) {
        Object connection = getConnection(p);
        try {
            connection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(connection, packet);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {}
    }
}