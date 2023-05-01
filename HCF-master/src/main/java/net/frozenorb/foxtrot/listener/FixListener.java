package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * a listener class that represents bootleg fixes to things out of our control
 */
public final class FixListener implements Listener {

    private final List<DTRBitmask> bitmasks = Arrays.asList(
            DTRBitmask.KOTH,
            DTRBitmask.SAFE_ZONE,
            DTRBitmask.ROAD,
            DTRBitmask.CITADEL,
            DTRBitmask.CONQUEST
    );

    @EventHandler(priority = EventPriority.LOWEST)
    private void onCommand(PlayerCommandPreprocessEvent event) {
        final String message = event.getMessage();
        if ((message.startsWith("/worldedit") || message.startsWith("//")) && message.contains("calc") || event.getMessage().replace("/", "").startsWith("calc")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Dont do that!");
        }
    }

    @EventHandler
    private void onEnchant(EnchantItemEvent event) {
        Map<Enchantment, Integer> enchantsToAdd = event.getEnchantsToAdd();
        enchantsToAdd.entrySet().removeIf(entry -> entry.getKey().equals(Enchantment.FIRE_ASPECT) || entry.getKey().equals(Enchantment.KNOCKBACK));
    }

    @EventHandler
    private void onLeavesDecay(LeavesDecayEvent event) {
        for (DTRBitmask bitmask : bitmasks) {
            if (bitmask.appliesAt(event.getBlock().getLocation())) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPearl(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.ENDER_PEARL) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location location = event.getPlayer().getLocation();
                if (DTRBitmask.CITADEL.appliesAt(location)) {
                    if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(CC.RED + "You cannot use this in citadel.");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPearl(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl && event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            Location location = player.getLocation();
            if (DTRBitmask.CITADEL.appliesAt(location)) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    event.setCancelled(true);
                    player.sendMessage(CC.RED + "You cannot use this in citadel.");
                }
            }
        }
    }

}
