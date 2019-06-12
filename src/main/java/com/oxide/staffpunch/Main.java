package com.oxide.staffpunch;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        getCommand("oxidepunch").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("oxidepunch")) {
            if (!(sender instanceof Player))
                return true;

            File userdata = new File(Main.plugin.getDataFolder() + File.separator + "Players");
            File f = new File(userdata + File.separator + ((Player) sender).getUniqueId().toString() + ".yml");
            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(f);

            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Thanks for installing OxidePunch " + Bukkit.getServer().getPluginManager().getPlugin("OxidePunch").getDescription().getVersion());
                sender.sendMessage(ChatColor.RED + "Created by deadman96385");
                return true;
            }

            if (args[0].equalsIgnoreCase("power")) {
                if (args.length == 1) {
                    sender.sendMessage("No Power Level specified eg. 1-10");
                    return true;
                } else {
                    for (int i = 1; i < 11; i++) {
                        if (args[1].equalsIgnoreCase(Integer.toString(i))) {
                            playerConfig.set("player.power", i);
                            try {
                                playerConfig.save(f);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    }
                    sender.sendMessage("Invalid power level");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (args.length == 1) {
                    if (sender.hasPermission("oxidepunch.reload")) {
                        Main.plugin.saveConfig();
                        Main.plugin.reloadConfig();
                        getLogger().log(Level.INFO, "Config Reloaded.");
                        sender.sendMessage(ChatColor.GRAY + "Config File Reloaded!");
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have permission for this command.");
                        return true;
                    }
                }
            } else if (args[0].equalsIgnoreCase("firework")) {
                if (args.length == 1) {
                    sender.sendMessage("Number of fireworks not specified eg. 1-2");
                    return true;
                } else {
                    for (int i = 1; i < 11; i++) {
                        if (args[1].equalsIgnoreCase(Integer.toString(i))) {
                            playerConfig.set("player.fireworks", i);
                            try {
                                playerConfig.save(f);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    }
                    sender.sendMessage("Invalid number of fireworks");
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> onTabComplete(CommandSender sender,
                                      Command command,
                                      String alias,
                                      String[] args) {
        if (command.getName().equalsIgnoreCase("oxidepunch")) {
            List<String> l = new ArrayList<>();

            if (args.length == 1) {
                l.add("power");
                l.add("firework");
                l.add("reload");
            }
            if (args[0].equalsIgnoreCase("firework") || args[0].equalsIgnoreCase("power")) {
                if (args.length == 2) {
                    for (int i = 1; i < 11; i++) {
                        l.add(Integer.toString(i));
                    }
                }
            }
            return l;
        }
        return null;
    }
}
