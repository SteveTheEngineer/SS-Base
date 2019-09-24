package ru.ste.stevesseries.corebase;

import org.bukkit.plugin.Plugin;

public class Identifier {
    private String namespace, name;

    /**
     * @param namespace namespace
     * @param name name
     * @deprecated See {@link Identifier(Plugin, String)}
     */
    @Deprecated
    public Identifier(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    public Identifier(Plugin plugin, String name) {
        this(plugin.getDescription().getRawName().toLowerCase(), name);
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public int hashCode() {
        return 16 * namespace.hashCode() * name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass().isAssignableFrom(this.getClass())) return false;
        Identifier id = (Identifier) obj;
        return id.namespace.equals(this.namespace) && id.name.equals(this.name);
    }

    @Override
    public String toString() {
        return namespace + ":" + name;
    }
}