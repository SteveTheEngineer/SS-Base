package me.ste.stevesseries.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Function;

/**
 * Generic util class
 */
public final class GenericUtil {
    private GenericUtil() {}

    /**
     * Same as {@link GenericUtil#parseNamespacedKey(String, String)}, but with the default namespace set to ss-base
     * @param namespacedKey stringified namespaced key
     * @return namespaced key
     */
    public static NamespacedKey parseNamespacedKey(String namespacedKey) {
        return GenericUtil.parseNamespacedKey(Base.getPlugin(Base.class).getName().toLowerCase(Locale.ROOT), namespacedKey);
    }

    /**
     * Parse a stringified namespaced key (for example, minecraft:stone) into a namespaced key
     * @param namespacedKey stringified namespaced key
     * @return namespaced key
     */
    public static NamespacedKey parseNamespacedKey(String defaultNamespace, String namespacedKey) {
        String[] split = namespacedKey.split(":");
        if(namespacedKey.matches("[a-z0-9/._-]:[a-z0-9/._-]")) {
            return new NamespacedKey(split[0], String.join(":", Arrays.copyOfRange(split, 1, split.length - 1)));
        } else if(namespacedKey.matches("[a-z0-9/._-]")) {
            return new NamespacedKey(defaultNamespace, split[0]);
        } else {
            return null;
        }
    }

    /**
     * Serialize the specified location to JSON
     * @param location the location
     * @return json serialized location
     */
    public static JsonObject locationToJson(Location location) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", location.getWorld().getUID().toString());
        jsonObject.addProperty("x", location.getX());
        jsonObject.addProperty("y", location.getY());
        jsonObject.addProperty("z", location.getZ());
        jsonObject.addProperty("yaw", location.getYaw());
        jsonObject.addProperty("pitch", location.getPitch());
        return jsonObject;
    }

    /**
     * Deserialize specified JSON to a location
     * @param jsonObject json serialized location
     * @return deserialized location
     */
    public static Location locationFromJson(JsonObject jsonObject) {
        return new Location(Bukkit.getWorld(UUID.fromString(jsonObject.get("world").getAsString())), jsonObject.get("x").getAsDouble(), jsonObject.get("y").getAsDouble(), jsonObject.get("z").getAsDouble(), jsonObject.get("yaw").getAsFloat(), jsonObject.get("pitch").getAsFloat());
    }

    /**
     * Get all block locations inside the specified area
     * @param loc1 first point
     * @param loc2 second point
     * @return blocks inside the area
     */
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

    /**
     * Rotate the vector by the specified angle
     * @param vector the vector
     * @param yaw yaw angle
     * @param pitch pitch angle
     * @param roll roll angle
     * @return rotated vector
     */
    public static Vector rotateVector(Vector vector, float yaw, float pitch, float roll) {
        return vector.clone().rotateAroundY(Math.toRadians(-1 * (yaw + 90))).rotateAroundX(Math.toRadians(-pitch)).rotateAroundZ(Math.toRadians(roll));
    }
}