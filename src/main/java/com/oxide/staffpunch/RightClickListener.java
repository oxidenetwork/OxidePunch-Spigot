package com.oxide.staffpunch;

import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class RightClickListener implements Listener {

    private ArrayList<Player> cooldown = new ArrayList<>();
    private ArrayList<Player> nofall = new ArrayList<>();

    @EventHandler
    public void RightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Player click_player = ((Player) event.getRightClicked());
        if (!Main.plugin.getConfig().getStringList("disabled-worlds").contains(event.getPlayer().getWorld().getName())) {
            if (event.getHand() == EquipmentSlot.HAND) {
                if (!cooldown.contains(player)) {
                    if (player.hasPermission("oxidepunch.use") || click_player.hasPermission("oxidepunch.use")) {
                        if (event.getRightClicked() instanceof Player) {
                            int cooldowntime = Main.plugin.getConfig().getInt("cooldowntime");

                            fireworkgen(click_player, click_player.getLocation(), Main.plugin.getConfig().getInt("amount-of-fireworks"), Main.plugin.getConfig().getInt("flight-time-of-fireworks"));
                            for (int i = 0; i < 4; i++) {
                                click_player.getWorld().playEffect(click_player.getLocation(), Effect.valueOf(Main.plugin.getConfig().getString("particle")), 4);
                            }
                            click_player.setVelocity(new Vector(0, Main.plugin.getConfig().getInt("launch-power"), 0));
                            for (int i = 0; i < 2; i++) {
                                click_player.playSound(click_player.getLocation(), org.bukkit.Sound.valueOf(Main.plugin.getConfig().getString("sound")), 1.0F, 0.0F);
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
        Firework f = launchy.getWorld().spawn(location, Firework.class);
        FireworkMeta fm = f.getFireworkMeta();
        fm.setPower(power);
        for (int i = 0; i < amount; i++) {
            Firework f2 = launchy.getWorld().spawn(location.add(new Vector(Math.random() - 0.5, 0, Math.random() - 0.5).multiply(10)), Firework.class);
            f2.setFireworkMeta(fm);
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
}
