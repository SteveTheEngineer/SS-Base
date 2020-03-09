package me.ste.stevesseries.corebase;

import me.ste.stevesseries.corebase.util.Utilities;
import org.bukkit.plugin.java.JavaPlugin;

public final class CoreBase extends JavaPlugin {

    @Override
    public void onEnable() {
        Utilities.init(getLogger());
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {

    }
}