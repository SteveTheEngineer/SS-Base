package me.ste.stevesseries.corebase.registry;

import org.bukkit.NamespacedKey;

public class RegistryEntry {
    private NamespacedKey id;
    private Object object;

    public RegistryEntry(NamespacedKey id, Object object) {
        this.id = id;
        this.object = object;
    }

    public NamespacedKey getId() {
        return id;
    }

    public Object getObject() {
        return object;
    }
}