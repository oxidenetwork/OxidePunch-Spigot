package com.oxide.staffpunch;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;

public class RightClickListener implements Listener {

    private ArrayList<Player> cooldown = new ArrayList<>();
    private ArrayList<Player> nofall = new ArrayList<>();

    private Integer playerpower;
    private String playerparticles;
    private String playersound;
    private Boolean playerfireworkflicker;
    private Boolean playerfireworktrail;
    private String playerfireworktype;
    private String playerfireworkcolor;
    private String playerfireworkfade;
    private Integer playerfireworknumber;
    private Integer playerfireworkflighttime;

    @EventHandler
    public void RightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity click_player = event.getRightClicked();
        if (click_player instanceof Player) {
            if (!Main.plugin.getConfig().getStringList("disabled-worlds").contains(event.getPlayer().getWorld().getName())) {
                if (event.getHand() == EquipmentSlot.HAND) {
                    if (!cooldown.contains(player)) {
                        if (player.hasPermission("oxidepunch.use") && click_player.hasPermission("oxidepunch.use")) {
                            if (event.getRightClicked() instanceof Player) {
                                int cooldowntime = Main.plugin.getConfig().getInt("cooldowntime");

                                updateValues(player);

                                fireworkgen(((Player) click_player).getPlayer(), click_player.getLocation(), playerfireworknumber, playerfireworkflighttime * 2);
                                for (int i = 0; i < 4; i++) {
                                    click_player.getWorld().playEffect(click_player.getLocation(), Effect.valueOf(playerparticles), 4);
                                }
                                click_player.setVelocity(new Vector(0, playerpower, 0));
                                for (int i = 0; i < 2; i++) {
                                    ((Player) click_player).getPlayer().playSound(click_player.getLocation(), org.bukkit.Sound.valueOf(playersound), 1.0F, 0.0F);
                                }
                                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', Main.plugin.getConfig().getString("message").replaceAll("%player%", player.getDisplayName()).replaceAll("%click_player%", ((Player) click_player).getPlayer().getDisplayName())));

                                cooldown.add(player);
                                if (player.hasPermission("oxidepunch.bypasscooldown")) {
                                    cooldown.remove(player);
                                }
                                if (!nofall.contains(click_player)) {
                                    nofall.add(((Player) click_player).getPlayer());
                                }
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> cooldown.remove(player), cooldowntime * 20L);
                            }
                        }
                    } else {
                        player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', Main.plugin.getConfig().getString("cooldown-message")));
                    }
                }
            } else {
                player.sendMessage(Main.plugin.getConfig().getString("world-disabled"));
            }
        }
    }

    private void fireworkgen(Player launchy, Location location, int amount, int power) {
        for (int i = 0; i < amount; i++) {
            Firework f = launchy.getWorld().spawn(location.add(new Vector(Math.random() - 0.5, 0, Math.random() - 0.5).multiply(10)), Firework.class);
            FireworkMeta fm = f.getFireworkMeta();
            fm.addEffect((FireworkEffect.builder()
                    .flicker((playerfireworkflicker))
                    .trail((playerfireworktrail))
                    .with(FireworkEffect.Type.valueOf(playerfireworktype))
                    .withColor(colorpicker(playerfireworkcolor))
                    .withFade(colorpicker(playerfireworkfade))
                    .build()));
            fm.setPower(power);
            f.setFireworkMeta(fm);
        }
    }

    private void updateValues(Player player) {
        File userdata = new File(Main.plugin.getDataFolder() + File.separator + "Players");
        File f = new File(userdata + File.separator + player.getUniqueId().toString() + ".yml");
        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(f);

        if (f.exists()) {
            playerpower = playerConfig.getInt("player.power");
            playerparticles = playerConfig.getString("player.particles");
            playersound = playerConfig.getString("player.sound");
            playerfireworknumber = playerConfig.getInt("player.fireworknumber");
            playerfireworkflighttime = playerConfig.getInt("player.fireworkflighttime");
            playerfireworkflicker = playerConfig.getBoolean("player.fireworkflicker");
            playerfireworktrail = playerConfig.getBoolean("player.fireworktrail");
            playerfireworktype = playerConfig.getString("player.fireworktype");
            playerfireworkcolor = playerConfig.getString("player.fireworkcolor");
            playerfireworkfade = playerConfig.getString("player.fireworkfade");
        } else {
            playerpower = Main.plugin.getConfig().getInt("launch-power");
            playerparticles = Main.plugin.getConfig().getString("particle");
            playersound = Main.plugin.getConfig().getString("sound");
            playerfireworknumber = Main.plugin.getConfig().getInt("amount-of-fireworks");
            playerfireworkflighttime = Main.plugin.getConfig().getInt("flight-time-of-fireworks");
            playerfireworkflicker = Main.plugin.getConfig().getBoolean("firework-flicker");
            playerfireworktrail = Main.plugin.getConfig().getBoolean("firework-trail");
            playerfireworktype = Main.plugin.getConfig().getString("firework-type");
            playerfireworkcolor = Main.plugin.getConfig().getString("firework-color");
            playerfireworkfade = Main.plugin.getConfig().getString("firework-fade");
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if ((((event.getEntity() instanceof Player)) && (event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.FALL))
                || (event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
            Player flier = (Player) event.getEntity();
            if (nofall.contains(flier)) {
                event.setCancelled(true);
                nofall.remove(flier);
            }
        }
    }

    private Color colorpicker(String color) {
        Color fireworkcolor;
        switch (color) {
            case "aqua":
                fireworkcolor = Color.AQUA;
                return fireworkcolor;
            case "black":
                fireworkcolor = Color.BLACK;
                return fireworkcolor;
            case "blue":
                fireworkcolor = Color.BLUE;
                return fireworkcolor;
            case "fuchsia":
                fireworkcolor = Color.FUCHSIA;
                return fireworkcolor;
            case "gray":
                fireworkcolor = Color.GRAY;
                return fireworkcolor;
            case "green":
                fireworkcolor = Color.GREEN;
                return fireworkcolor;
            case "lime":
                fireworkcolor = Color.LIME;
                return fireworkcolor;
            case "maroon":
                fireworkcolor = Color.MAROON;
                return fireworkcolor;
            case "navy":
                fireworkcolor = Color.NAVY;
                return fireworkcolor;
            case "olive":
                fireworkcolor = Color.OLIVE;
                return fireworkcolor;
            case "orange":
                fireworkcolor = Color.ORANGE;
                return fireworkcolor;
            case "purple":
                fireworkcolor = Color.PURPLE;
                return fireworkcolor;
            case "red":
                fireworkcolor = Color.RED;
                return fireworkcolor;
            case "silver":
                fireworkcolor = Color.SILVER;
                return fireworkcolor;
            case "teal":
                fireworkcolor = Color.TEAL;
                return fireworkcolor;
            case "white":
                fireworkcolor = Color.WHITE;
                return fireworkcolor;
            case "yellow":
                fireworkcolor = Color.YELLOW;
                return fireworkcolor;
            default:
                throw new IllegalStateException("Unexpected value: " + color);
        }
    }
}
