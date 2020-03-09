package me.ste.stevesseries.corebase.registry;

import org.bukkit.NamespacedKey;

public class RegistryEntry<T> {
    private NamespacedKey id;
    private T object;

    public RegistryEntry(NamespacedKey id, T object) {
        this.id = id;
        this.object = object;
    }

    public NamespacedKey getId() {
        return id;
    }

    public T getObject() {
        return object;
    }
}