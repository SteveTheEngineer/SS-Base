package me.ste.stevesseries.corebase.config.valuetype;

import me.ste.stevesseries.corebase.config.ConfigValue;
import org.bukkit.configuration.ConfigurationSection;

public class SimpleValueType implements ConfigValueType {
    @Override
    public String getValue(ConfigValue configValue, ConfigurationSection section, String path) {
        if(section.isConfigurationSection(path)) {
            return section.getConfigurationSection(path).get("value").toString();
        } else {
            return section.get(path).toString();
        }
    }
}