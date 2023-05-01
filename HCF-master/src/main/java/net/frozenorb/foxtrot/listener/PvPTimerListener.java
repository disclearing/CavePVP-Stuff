package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.util.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PvPTimerListener implements Listener {

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
                player.sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = PlayerUtils.getDamageSource(event.getDamager()); // find the player damager if one exists

        if (damager == null) {
            return;
        }

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(damager.getUniqueId())) {
            damager.sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
            damager.sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
            event.setCancelled(true);
            return;
        }

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getEntity().getUniqueId())) {
            damager.sendMessage(ChatColor.RED + "That player currently has their PVP Timer!");
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && (event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)) {
            Player player = (Player) event.getEntity();

            if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

}