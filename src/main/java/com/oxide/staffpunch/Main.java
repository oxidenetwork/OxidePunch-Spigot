package com.oxide.staffpunch;


import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new RightClickListener(), this);
    }
}
