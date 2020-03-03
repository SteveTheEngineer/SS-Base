package me.ste.stevesseries.corebase.config.valuetype;

import me.ste.stevesseries.corebase.config.ConfigValue;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public interface ConfigValueType {
     String getValue(ConfigValue configValue, ConfigurationSection section, String path);
}