package me.ste.stevesseries.base;

import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

/**
 * NMS-related utility class
 * @deprecated use {@link ReflectionUtil} instead
 */
@Deprecated
public final class NMSUtil {
    private NMSUtil() {}

    public static String getNMSVersion() {
        return ReflectionUtil.NMS_VERSION;
    }
    public static Class<?> getNMSClass(String name) {

        try {
            return ReflectionUtil.resolveClass(ReflectionUtil.NMS_PACKAGE, name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    public static Class<?> getOBCClass(String name) {
        try {
            return ReflectionUtil.resolveClass(ReflectionUtil.OBC_PACKAGE, name);
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
        Object handle = NMSUtil.getHandle(p);
        try {
            return handle.getClass().getField("playerConnection").get(handle);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return null;
        }
    }
    public static void sendPacket(Player p, Object packet) {
        Object connection = NMSUtil.getConnection(p);
        try {
            connection.getClass().getMethod("sendPacket", NMSUtil.getNMSClass("Packet")).invoke(connection, packet);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {}
    }
}