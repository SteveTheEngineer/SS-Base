package me.ste.stevesseries.corebase;

import org.bukkit.NamespacedKey;

import java.util.Arrays;
import java.util.Locale;

public final class GenericUtil {
    private GenericUtil() {}

    public static NamespacedKey parseNamespacedKey(String namespacedKey) {
        return parseNamespacedKey(CoreBase.getPlugin(CoreBase.class).getName().toLowerCase(Locale.ROOT), namespacedKey);
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
}