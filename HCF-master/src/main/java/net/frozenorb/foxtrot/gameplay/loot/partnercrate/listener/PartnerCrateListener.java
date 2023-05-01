package net.frozenorb.foxtrot.gameplay.loot.partnercrate.listener;

import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PartnerCrateListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    private void onClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation()) || block == null || block.getType() != Material.ENDER_CHEST || block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() != Material.REDSTONE_BLOCK) {
            return;
        }

        player.chat("/partnercrate");
    }

}
