package me.ste.stevesseries.corebase.config;

import me.ste.stevesseries.corebase.GenericUtil;
import me.ste.stevesseries.corebase.config.valuetype.ConfigValueType;
import me.ste.stevesseries.corebase.registry.Registry;
import me.ste.stevesseries.corebase.registry.RegistryManager;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class ConfigValue {
    private static Registry configValueTypeRegistry = RegistryManager.getInstance().getRegistryExceptional(GenericUtil.parseNamespacedKey("config_value_type"));
    private ConfigValueType type;
    private ConfigurationSection section;
    private String path;
    private Map<String, String> context;

    public ConfigValue(ConfigurationSection section, String path, Map<String, String> context) {
        this.section = section;
        this.path = path;
        this.context = context;
        if(section.contains("type")) {
            this.type = (ConfigValueType) configValueTypeRegistry.getEntry(GenericUtil.parseNamespacedKey(section.get("type").toString())).get().getObject();
        } else {
            this.type = (ConfigValueType) configValueTypeRegistry.getEntry(GenericUtil.parseNamespacedKey("simple")).get().getObject();
        }
    }

    public String getValue() {
        return type.getValue(this, section, path);
    }

    public Map<String, String> getContext() {
        return context;
    }

    public String processStringCtx(String str) {
        // TODO
        return null;
    }
}