package com.oxide.staffpunch;


import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Main plugin;

    @Override
    public void onDisable() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new RightClickListener(), this);
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        plugin = this;
    }
}
