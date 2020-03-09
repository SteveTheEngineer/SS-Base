package me.ste.stevesseries.corebase;

import me.ste.stevesseries.corebase.registry.RegistryManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public final class CoreBase extends JavaPlugin {
    public ScriptEngine scriptEngine = null;

    @Override
    public void onEnable() {
        RegistryManager.getInstance();

        scriptEngine = new ScriptEngineManager(null).getEngineByName("JavaScript");
        if(scriptEngine == null) {
            this.getLogger().warning("No JavaScript engine is available, JavaScript support is disabled");
        }

        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {

    }
}