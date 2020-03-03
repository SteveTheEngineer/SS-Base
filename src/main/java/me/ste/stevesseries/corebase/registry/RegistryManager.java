package me.ste.stevesseries.corebase.registry;

import org.bukkit.NamespacedKey;

import java.util.Map;
import java.util.Optional;

public class RegistryManager {
    private Map<NamespacedKey, Registry> registries;
    private static RegistryManager instance;

    private RegistryManager() {}

    public static RegistryManager getInstance() {
        if(instance == null) {
            instance = new RegistryManager();
        }
        return instance;
    }

    public Registry addRegistry(NamespacedKey id) {
        if(registries.containsKey(id)) {
            throw new IllegalArgumentException("Registry (" + id.toString() + ") already exists");
        }
        Registry registry = new Registry(id);
        registries.put(id, registry);
        return registry;
    }

    public Optional<Registry> getRegistry(NamespacedKey id) {
        return Optional.ofNullable(registries.get(id));
    }

    public Registry getRegistryExceptional(NamespacedKey id) {
        Optional<Registry> optionalRegistry = getRegistry(id);
        if(optionalRegistry.isPresent()) {
            return optionalRegistry.get();
        } else {
            throw new IllegalArgumentException("Unknown registry: " + id.toString());
        }
    }
}