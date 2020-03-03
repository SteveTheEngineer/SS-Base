package me.ste.stevesseries.corebase.registry;

import me.ste.stevesseries.corebase.CoreBase;
import org.bukkit.NamespacedKey;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class Registry {
    private NamespacedKey id;
    private Set<Function<RegistryEntry, String>> entryRules;
    private Set<RegistryEntry> entries;
    private boolean writable = true;

    public Registry(NamespacedKey id) {
        this.id = id;
    }

    public Set<Function<RegistryEntry, String>> getEntryRules() {
        return entryRules;
    }

    public NamespacedKey getId() {
        return id;
    }

    public void addRules(Function<RegistryEntry, String>... rules) {
        entryRules.addAll(Arrays.asList(rules));
    }

    public Optional<RegistryEntry> getEntry(NamespacedKey id) {
        for(RegistryEntry entry : entries) {
            if(entry.getId().equals(id)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public Optional<String> checkEntry(RegistryEntry entry) {
        for(Function<RegistryEntry, String> rule : entryRules) {
            String result = rule.apply(entry);
            if(result != null) {
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

    public void register(NamespacedKey id, Object object) {
        if(!writable) {
            throw new RegistryException("Registry (" + this.id.toString() + ") is closed");
        }
        RegistryEntry entry = new RegistryEntry(id, object);
        Optional<String> checkResult = checkEntry(entry);
        if(!checkResult.isPresent()) {
            Optional<RegistryEntry> existing = getEntry(id);
            if(existing.isPresent()) {
                CoreBase.getPlugin(CoreBase.class).getLogger().warning("Overriding existing registry (" + this.id.toString() + ") entry: " + id.toString() + " (" + existing.get().getClass().getName() + " with " + object.getClass().getName() + ")");
                entries.remove(existing.get());
            }
            entries.add(entry);
        } else {
            throw new RegistryException("Requirement unmet (" + this.id.toString() + "): " + checkResult.get());
        }
    }

    public void close() {
        this.writable = false;
    }
}