package me.ste.stevesseries.corebase.registry.rule;

import me.ste.stevesseries.corebase.registry.Registry;
import me.ste.stevesseries.corebase.registry.RegistryEntry;

import java.util.function.Function;

public class NoOverrideRule implements Function<RegistryEntry<?>, String> {
    private Registry registry;

    public NoOverrideRule(Registry registry) {
        this.registry = registry;
    }

    @Override
    public String apply(RegistryEntry registryEntry) {
        return registry.getEntry(registryEntry.getId()).isPresent() ? "Cannot override existing entry" : null;
    }
}
