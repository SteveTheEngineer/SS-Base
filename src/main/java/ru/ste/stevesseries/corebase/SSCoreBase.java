package ru.ste.stevesseries.corebase;

import org.bukkit.plugin.java.JavaPlugin;

public final class SSCoreBase extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {

    }
}