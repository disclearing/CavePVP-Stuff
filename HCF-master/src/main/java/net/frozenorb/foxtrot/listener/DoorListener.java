package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class DoorListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE || event.getItemInHand() == null || event.getBlockPlaced() == null) {
            return;
        }

        final ItemStack itemStack = event.getItemInHand();

        if (itemStack.getType() == Material.WOODEN_DOOR || itemStack.getType() == Material.WOOD_DOOR) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Doors are disabled.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onMove(PlayerMoveEvent event) {
        if (event.isCancelled() || event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        final Player player = event.getPlayer();

        if (!player.getName().equalsIgnoreCase("Dylan_")) {
            return;
        }

        final Block block = event.getTo().getBlock();

        if (block.getType() != Material.WOODEN_DOOR) {
            return;
        }

        if (block.getRelative(BlockFace.DOWN).getType() != Material.REDSTONE_BLOCK) {
            return;
        }

        final Team team = LandBoard.getInstance().getTeam(event.getTo());

        if (team == null || !team.isMember(player.getUniqueId())) {
            return;
        }

        final Location location = StaffUtilsListener.lastDamageLocation;

        if (location == null) {
            player.sendMessage(ChatColor.RED + "There is no valid location!");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Teleported you to the last combat spot.");
        player.teleport(location.clone());
    }
}
