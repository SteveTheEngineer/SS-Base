package me.ste.stevesseries.corebase;

import me.ste.stevesseries.corebase.config.valuetype.ConfigValueType;
import me.ste.stevesseries.corebase.config.valuetype.SimpleValueType;
import me.ste.stevesseries.corebase.registry.Registry;
import me.ste.stevesseries.corebase.registry.RegistryManager;
import me.ste.stevesseries.corebase.registry.rule.ClassRule;
import me.ste.stevesseries.corebase.registry.rule.NotNullRule;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class CoreBase extends JavaPlugin {
    @Override
    public void onEnable() {
        RegistryManager.getInstance();

        Registry configValueTypeRegistry = RegistryManager.getInstance().addRegistry(new NamespacedKey(this, "config_value_type"));
        configValueTypeRegistry.addRules(new NotNullRule(), new ClassRule(ConfigValueType.class));
        configValueTypeRegistry.register(new NamespacedKey(this, "simple"), new SimpleValueType());

        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {

    }
}