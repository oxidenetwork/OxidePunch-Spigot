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
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class RightClickListener implements Listener {

    private ArrayList<Player> cooldown = new ArrayList<>();
    private ArrayList<Player> nofall = new ArrayList<>();

    @EventHandler
    public void RightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Player click_player = ((Player) event.getRightClicked());
        if (!player.hasPermission("oxidepunch.use") || !click_player.hasPermission("oxidepunch.use")) {
            return;
        }
        if (cooldown.contains(player)) {
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', Main.plugin.getConfig().getString("cooldown-message")));
            return;
        }
        if (event.getRightClicked() instanceof Player) {
            int cooldowntime = Main.plugin.getConfig().getInt("cooldowntime");
            if (event.getHand() == EquipmentSlot.HAND) {
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', Main.plugin.getConfig().getString("message").replaceAll("%player%", player.getDisplayName()).replaceAll("%click_player%", click_player.getDisplayName())));
                click_player.setVelocity(new Vector(0, Main.plugin.getConfig().getInt("launch-power"), 0));
                cooldown.add(player);
                if (player.hasPermission("oxidepunch.bypasscooldown")) {
                    cooldown.remove(player);
                }
                if (!nofall.contains(click_player)) {
                    nofall.add(click_player);
                }
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin) this, () -> cooldown.remove(player), cooldowntime * 20);
            }
            fireworkgen(click_player, click_player.getLocation(), Main.plugin.getConfig().getInt("amount-of-fireworks"), Main.plugin.getConfig().getInt("flight-time-of-fireworks"));

        }
    }

    private void fireworkgen(Player launchy, Location location, int amount, int power) {
        Firework f = launchy.getWorld().spawn(location, Firework.class);
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder().flicker(false).trail(false).with(FireworkEffect.Type.BURST).withColor(Color.ORANGE).withFade(Color.AQUA).build());
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
