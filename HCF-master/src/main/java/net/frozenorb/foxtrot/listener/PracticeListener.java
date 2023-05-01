package net.frozenorb.foxtrot.listener;

import cc.fyre.universe.util.BungeeUtil;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PracticeListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    private void onMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ() && to.getBlockY() == from.getBlockY()) {
            return;
        }

        if (!DTRBitmask.SAFE_ZONE.appliesAt(to)) {
            return;
        }

        if (!to.getBlock().getType().name().contains("WATER")) {
            return;
        }

        final Block downBlock = to.getBlock().getRelative(BlockFace.DOWN);

        if (!downBlock.getType().equals(Material.QUARTZ_BLOCK)) {
            final Block downBlockTwo = downBlock.getRelative(BlockFace.DOWN);

            if (!downBlockTwo.getType().equals(Material.QUARTZ_BLOCK)) {
                return;
            }
        }

        player.playSound(player.getLocation(), Sound.PORTAL_TRAVEL, 1, 1);
        player.sendMessage(ChatColor.GREEN + "Warping...");
        player.teleport(player.getWorld().getSpawnLocation().clone());

        BungeeUtil.sendToServer(player, "NA-Practice");
    }
}
