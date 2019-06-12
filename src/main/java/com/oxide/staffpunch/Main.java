package com.oxide.staffpunch;


import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    public static Main plugin;

    private List<String> commandList = Arrays.asList("power", "firework", "sound", "particle", "reload", "menu");
    private List<String> soundList = Arrays.asList("ENTITY_EXPERIENCE_ORB_PICKUP", "ENTITY_PANDA_SNEEZE", "ENTITY_PUFFER_FISH_STING",
            "ENTITY_SHULKER_SHOOT", "ENTITY_SHULKER_TELEPORT", "ENTITY_STRAY_DEATH", "ENTITY_VEX_DEATH", "ENTITY_WANDERING_TRADER_YES",
            "ENTITY_WITHER_HURT", "ENTITY_ZOMBIE_ATTACK_IRON_DOOR", "ITEM_TRIDENT_RIPTIDE_1", "ITEM_TRIDENT_THROW");
    private List<String> particleList = Arrays.asList("MOBSPAWNER_FLAMES", "SMOKE", "POTION_BREAK", "VILLAGER_PLANT_GROW", "DRAGON_BREATH", "END_GATEWAY_SPAWN", "ENDER_SIGNAL");

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
            } else if (args[0].equalsIgnoreCase("sound")) {
                if (args.length == 1) {
                    sender.sendMessage("Sound type not selected please select one");
                    return true;
                } else {
                    for (String s : soundList) {
                        if (args[1].equalsIgnoreCase(s)) {
                            playerConfig.set("player.sound", s);
                            try {
                                playerConfig.save(f);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    }
                }

            } else if (args[0].equalsIgnoreCase("particle")) {
                if (args.length == 1) {
                    sender.sendMessage("Particle type not selected please select one");
                    return true;
                } else {
                    for (String p : particleList) {
                        if (args[1].equalsIgnoreCase(p)) {
                            playerConfig.set("player.particles", p);
                            try {
                                playerConfig.save(f);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("menu")) {
                if (args.length == 1) {
                    Player p = ((Player) sender).getPlayer();
                    GuiElementGroup group = new GuiElementGroup('x');
                    InventoryGui gui = new InventoryGui(plugin, p, "&cOnline&4Players", plugin.getConfig().getStringList("matrix").toArray(new String[0]));
                    gui.addElement(new GuiPageElement('b', new ItemStack(Material.COAL, 1), GuiPageElement.PageAction.PREVIOUS, "&cPREVIOUS"));
                    gui.addElement(new GuiPageElement('f', new ItemStack(Material.CHARCOAL, 1), GuiPageElement.PageAction.NEXT, "&aNEXT"));
                    gui.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS, 1));
                    group.setFiller(gui.getFiller());
                    gui.addElement(group);
                    gui.show(p);
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
                l.addAll(commandList);
            }
            if (args[0].equalsIgnoreCase("firework") || args[0].equalsIgnoreCase("power")) {
                if (args.length == 2) {
                    for (int i = 1; i < 11; i++) {
                        l.add(Integer.toString(i));
                    }
                }
            }
            if (args[0].equalsIgnoreCase("sound")) {
                if (args.length == 2) {
                    l.addAll(soundList);
                }
            }

            if (args[0].equalsIgnoreCase("particle")) {
                if (args.length == 2) {
                    l.addAll(particleList);
                }
            }
            return l;
        }
        return null;
    }
}
