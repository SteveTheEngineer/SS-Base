package me.ste.stevesseries.base;

import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.util.Vector;

import java.util.ArrayList;
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
        List<Location> blocks = new ArrayList<>();

        Vector min = Vector.getMinimum(loc1.toVector(), loc2.toVector());
        Vector max = Vector.getMaximum(loc1.toVector(), loc2.toVector());

        for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for(int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    blocks.add(new Location(loc1.getWorld(), x, y, z));
                }
            }
        }

        return blocks;
    }

    public static Vector rotateVector(Vector vector, float yaw, float pitch, float roll) {
        return vector.clone().rotateAroundY(Math.toRadians(-1 * (yaw + 90))).rotateAroundX(Math.toRadians(-pitch)).rotateAroundZ(Math.toRadians(roll));
    }
}

