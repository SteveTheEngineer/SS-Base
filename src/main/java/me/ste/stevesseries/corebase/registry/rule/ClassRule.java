package me.ste.stevesseries.corebase.registry.rule;

import me.ste.stevesseries.corebase.registry.RegistryEntry;

import java.util.function.Function;

public class ClassRule implements Function<RegistryEntry, String> {
    private Class clazz;

    public ClassRule(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public String apply(RegistryEntry registryEntry) {
        return !clazz.isInstance(registryEntry.getObject()) ? "Object (" + registryEntry.getObject().getClass().getName() + ") must be instance of " + clazz.getName() : null;
    }
}
