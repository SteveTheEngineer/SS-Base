package me.ste.stevesseries.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Function;

public final class GenericUtil {
    private GenericUtil() {}

    public static NamespacedKey parseNamespacedKey(String namespacedKey) {
        return GenericUtil.parseNamespacedKey(Base.getPlugin(Base.class).getName().toLowerCase(Locale.ROOT), namespacedKey);
    }

    public static NamespacedKey parseNamespacedKey(String defaultNamespace, String namespacedKey) {
        String[] split = namespacedKey.split(":");
        if(namespacedKey.matches("[a-z0-9/._-]:[a-z0-9/._-]")) {
            return new NamespacedKey(split[0], String.join(":", Arrays.copyOfRange(split, 1, split.length - 1)));
        } else if(namespacedKey.matches("[a-z0-9/._-]")) {
            return new NamespacedKey(defaultNamespace, split[0]);
        } else {
            throw new IllegalArgumentException("String is empty");
        }
    }

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

    public static Location locationFromJson(JsonObject jsonObject) {
        return new Location(Bukkit.getWorld(UUID.fromString(jsonObject.get("world").getAsString())), jsonObject.get("x").getAsDouble(), jsonObject.get("y").getAsDouble(), jsonObject.get("z").getAsDouble(), jsonObject.get("yaw").getAsFloat(), jsonObject.get("pitch").getAsFloat());
    }

    public static List<Location> getBlocksInside(Location loc1, Location loc2) {
        List<Location> result = new ArrayList<>();
        if(!Objects.equals(loc1.getWorld().getUID(), loc2.getWorld().getUID())) {
            return result;
        }

        Vector min = Vector.getMinimum(loc1.toVector(), loc2.toVector());
        Vector max = Vector.getMaximum(loc1.toVector(), loc2.toVector());

        for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for(int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    result.add(new Location(loc1.getWorld(), x, y, z));
                }
            }
        }

        return result;
    }

    public static Vector rotateVector(Vector v, float yaw, float pitch, float roll) {
        return v.clone().rotateAroundY(Math.toRadians(-1 * (yaw + 90))).rotateAroundX(Math.toRadians(-pitch)).rotateAroundZ(Math.toRadians(roll));
    }

    public static <T> T ifHas(JsonObject object, String key, Function<JsonElement, T> has) {
        if(object.has(key)) {
            return has.apply(object.get(key));
        } else {
            return null;
        }
    }

    public static <T> void addReturnedIfNotNull(JsonObject object, String key, T value, Function<T, ? extends JsonElement> function) {
        if(value != null) {
            object.add(key, function.apply(value));
        }
    }
}