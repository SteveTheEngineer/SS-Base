package me.ste.stevesseries.base;

import org.bukkit.plugin.java.JavaPlugin;

public final class Base extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new ChatInputListener(), this);
    }

    @Override
    public void onDisable() {

    }
}