package net.frozenorb.foxtrot.listener;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import net.minecraft.util.com.google.common.collect.ImmutableSet;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;

public class FoundDiamondsListener implements Listener {

    public static final Set<BlockFace> CHECK_FACES = ImmutableSet.of(
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.NORTH_EAST,
            BlockFace.NORTH_WEST,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH_WEST,
            BlockFace.UP,
            BlockFace.DOWN);

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.DIAMOND_ORE) {
            event.getBlock().setMetadata("DiamondPlaced", new FixedMetadataValue(Foxtrot.getInstance(), true));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.DIAMOND_ORE && !event.getBlock().hasMetadata("DiamondPlaced")) {
            int diamonds = countRelative(event.getBlock());

            Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
            if (playerTeam != null) {
                    playerTeam.setDiamondsMined(playerTeam.getDiamondsMined() + diamonds);
            }

            for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                if (Foxtrot.getInstance().getToggleFoundDiamondsMap().isFoundDiamondToggled(player.getUniqueId())) {
                    player.sendMessage("[FD] " + ChatColor.AQUA + event.getPlayer().getName() + " found " + diamonds + " diamond" + (diamonds == 1 ? "" : "s") + ".");
                }
            }
        }
    }

    public int countRelative(Block block) {
        int diamonds = 1; // We start out with one because 'block' is going to be a diamond too.
        block.setMetadata("DiamondPlaced", new FixedMetadataValue(Foxtrot.getInstance(), true));

        for (BlockFace checkFace : CHECK_FACES) {
            Block relative = block.getRelative(checkFace);

            if (relative.getType() == Material.DIAMOND_ORE && !relative.hasMetadata("DiamondPlaced")) {
                relative.setMetadata("DiamondPlaced", new FixedMetadataValue(Foxtrot.getInstance(), true));
                diamonds += countRelative(relative);
            }
        }

        return (diamonds);
    }

}