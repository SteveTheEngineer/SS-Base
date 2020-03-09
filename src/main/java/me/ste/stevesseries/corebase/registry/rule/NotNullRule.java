package me.ste.stevesseries.corebase.registry.rule;

import me.ste.stevesseries.corebase.registry.RegistryEntry;

import java.util.function.Function;

public class NotNullRule implements Function<RegistryEntry<?>, String> {
    @Override
    public String apply(RegistryEntry registryEntry) {
        return registryEntry.getObject() == null ? "Object must not be null" : null;
    }
}
