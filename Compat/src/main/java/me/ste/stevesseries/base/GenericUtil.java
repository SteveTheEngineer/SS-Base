package me.ste.stevesseries.base;

import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @deprecated For backwards compatibility only.
 */
@Deprecated
public final class GenericUtil {
    private GenericUtil() {}

    public static NamespacedKey parseNamespacedKey(String namespacedKey) {
        return GenericUtil.parseNamespacedKey("ss-base", namespacedKey);
    }

    public static NamespacedKey parseNamespacedKey(String defaultNamespace, String namespacedKey) {
        return me.ste.stevesseries.base.util.GenericUtil.INSTANCE.parseNamespacedKey(defaultNamespace, namespacedKey);
    }

    public static JsonObject locationToJson(Location location) {
        return me.ste.stevesseries.base.util.GenericUtil.INSTANCE.locationToJson(location);
    }

    public static Location locationFromJson(JsonObject jsonObject) {
        return me.ste.stevesseries.base.util.GenericUtil.INSTANCE.locationFromJson(jsonObject);
    }

    public static List<Location> getBlocksInside(Location loc1, Location loc2) {
        return me.ste.stevesseries.base.util.GenericUtil.INSTANCE.getBlocksInBounds(loc1.toVector().toBlockVector(), loc2.toVector().toBlockVector()).stream().map(vector -> vector.toLocation(loc1.getWorld())).collect(Collectors.toList());
    }

    public static Vector rotateVector(Vector vector, float yaw, float pitch, float roll) {
        return vector.clone().rotateAroundY(Math.toRadians(-1 * (yaw + 90))).rotateAroundX(Math.toRadians(-pitch)).rotateAroundZ(Math.toRadians(roll));
    }
}

