package com.oxide.staffpunch;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        getLogger().log(Level.INFO, "OnCommand called");
        if (command.getName().equalsIgnoreCase("oxidepunch")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.GRAY + "§cThanks for installing OxidePunch " + Bukkit.getServer().getPluginManager().getPlugin("OxidePunch").getDescription().getVersion());
                sender.sendMessage(ChatColor.GRAY + "§cCreated by deadman96385");
            } else if ((args[0].equalsIgnoreCase("reload"))) {
                if (sender.hasPermission("oxidepunch.reload")) {
                    Main.plugin.saveConfig();
                    Main.plugin.reloadConfig();
                    getLogger().log(Level.INFO, "Config Reloaded.");
                    sender.sendMessage(ChatColor.GRAY + "Config File Reloaded!");
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission for this command.");
                }
            }
        }

        return false;
    }
}
