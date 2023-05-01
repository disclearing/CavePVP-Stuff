package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CrappleLimitListener implements Listener {
    public static Map<UUID, AtomicInteger> consumed = new HashMap<>();

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
            return;
        }

        consumed.putIfAbsent(event.getPlayer().getUniqueId(), new AtomicInteger());
    }

    @EventHandler
    private void onChange(PlayerChangedWorldEvent event) {
        if (!event.getFrom().getEnvironment().equals(World.Environment.NORMAL) && event.getPlayer().getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
            consumed.remove(event.getPlayer().getUniqueId());
        } else if (event.getFrom().getEnvironment().equals(World.Environment.NORMAL) && !event.getPlayer().getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
            consumed.put(event.getPlayer().getUniqueId(), new AtomicInteger());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void onPlayerItemConsume(PlayerItemConsumeEvent event) {

        Player player = event.getPlayer();

        if (event.getItem() == null || event.getItem().getType() != Material.GOLDEN_APPLE) {
            return;
        }

        if (event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getDurability() != 0) {
            return;
        }

        if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        consumed.putIfAbsent(player.getUniqueId(), new AtomicInteger()); 
        final AtomicInteger consumednum = CrappleLimitListener.consumed.getOrDefault(player.getUniqueId(), new AtomicInteger(0));

        if (consumednum.get() >= 12) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You have already consumed 12 Golden Apples in the " + WordUtils.capitalizeFully(player.getWorld().getEnvironment().name().replace("_", " ")) + "!");
        } else {
            consumed.put(player.getUniqueId(), new AtomicInteger(consumednum.incrementAndGet()));
        }
    }
}
