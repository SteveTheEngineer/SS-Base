package me.ste.stevesseries.corebase.config;

import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class BaseConfiguration {

    private File file;
    private FileConfiguration configuration;

    public BaseConfiguration(File file) {
        this.file = file;
        if (file.exists()) {
            this.configuration = YamlConfiguration.loadConfiguration(file);
        }
    }

    public void copyDefaults() throws IOException {
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(file.getName())) {
                if (in == null) {
                    file.createNewFile();
                    return;
                }
                Files.copy(in, file.getAbsoluteFile().toPath());
            }
            configuration = YamlConfiguration.loadConfiguration(file);
        }
    }

    public ConfigValue getConfigValue(String path) {
        Preconditions.checkNotNull(configuration, "Configuration not initialized. Have you created your file?");
        return new ConfigValue(configuration, path);
    }

    public void setAndSave(String path, Object value) throws IOException {
        configuration.set(path, value);
        if (!file.exists()) {
            copyDefaults();
        }
        configuration.save(file);
    }
}
