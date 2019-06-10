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

    private ArrayList<Player> nofall = new ArrayList<>();

    @EventHandler
    public void RightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Player click_player = ((Player) event.getRightClicked()).getPlayer();
        if (event.getRightClicked() instanceof Player) {
            if (event.getHand() == EquipmentSlot.HAND) {
                Bukkit.getServer().broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " launched " + ChatColor.DARK_RED + click_player.getName() + ChatColor.GREEN + " into the air");
                click_player.setVelocity(new Vector(0, 5, 0));
                if (!nofall.contains(click_player)) {
                    nofall.add(click_player);
                }
                fireworkgen(click_player, click_player.getLocation(), 5, 10);

            }
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
