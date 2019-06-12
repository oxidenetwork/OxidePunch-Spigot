package com.oxide.staffpunch;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
    private Integer playerfireworks;
    private String playerparticles;
    private String playersound;
    private Integer playerflighttime;

    @EventHandler
    public void RightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Player click_player = ((Player) event.getRightClicked());
        if (!Main.plugin.getConfig().getStringList("disabled-worlds").contains(event.getPlayer().getWorld().getName())) {
            if (event.getHand() == EquipmentSlot.HAND) {
                if (!cooldown.contains(player)) {
                    if (player.hasPermission("oxidepunch.use") && click_player.hasPermission("oxidepunch.use")) {
                        if (event.getRightClicked() instanceof Player) {
                            int cooldowntime = Main.plugin.getConfig().getInt("cooldowntime");

                            updateValues(player);

                            fireworkgen(click_player, click_player.getLocation(), playerfireworks, playerflighttime);
                            for (int i = 0; i < 4; i++) {
                                click_player.getWorld().playEffect(click_player.getLocation(), Effect.valueOf(playerparticles), 4);
                            }
                            click_player.setVelocity(new Vector(0, playerpower, 0));
                            for (int i = 0; i < 2; i++) {
                                click_player.playSound(click_player.getLocation(), org.bukkit.Sound.valueOf(playersound), 1.0F, 0.0F);
                            }
                            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', Main.plugin.getConfig().getString("message").replaceAll("%player%", player.getDisplayName()).replaceAll("%click_player%", click_player.getDisplayName())));

                            cooldown.add(player);
                            if (player.hasPermission("oxidepunch.bypasscooldown")) {
                                cooldown.remove(player);
                            }
                            if (!nofall.contains(click_player)) {
                                nofall.add(click_player);
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

    private void fireworkgen(Player launchy, Location location, int amount, int power) {
        for (int i = 0; i < amount; i++) {
            Firework f = launchy.getWorld().spawn(location.add(new Vector(Math.random() - 0.5, 0, Math.random() - 0.5).multiply(10)), Firework.class);
            FireworkMeta fm = f.getFireworkMeta();
            fm.setPower(power);
            f.setFireworkMeta(fm);
        }
    }

    private void updateValues(Player player) {
        File userdata = new File(Main.plugin.getDataFolder() + File.separator + "Players");
        File f = new File(userdata + File.separator + player.getUniqueId().toString() + ".yml");
        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(f);

        playerpower = playerConfig.getInt("player.power");
        playerfireworks = playerConfig.getInt("player.fireworks");
        playerparticles = playerConfig.getString("player.particles");
        playersound = playerConfig.getString("player.sound");
        playerflighttime = playerConfig.getInt("player.flighttime");
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
}
