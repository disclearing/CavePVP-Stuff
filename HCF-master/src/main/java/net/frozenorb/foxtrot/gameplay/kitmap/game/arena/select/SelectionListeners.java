package net.frozenorb.foxtrot.gameplay.kitmap.game.arena.select;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class SelectionListeners implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Block clicked = event.getClickedBlock();

        if (item != null && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) && item.getType() == Selection.SELECTION_WAND.getType()) {
            if (item.hasItemMeta() && item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().contains("Selection Wand")) {
                event.setCancelled(true);

                Selection selection = Selection.getOrCreateSelection(player);

                int set;

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    set = 2;
                    selection.setLoc1(event.getClickedBlock().getLocation());
                } else {
                    set = 1;
                    selection.setLoc2(event.getClickedBlock().getLocation());
                }

                String message = ChatColor.AQUA + (set == 1 ? "First" : "Second") +
                        " location " + ChatColor.YELLOW + "(" + ChatColor.GREEN +
                        clicked.getX() + ChatColor.YELLOW + ", " + ChatColor.GREEN +
                        clicked.getY() + ChatColor.YELLOW + ", " + ChatColor.GREEN +
                        clicked.getZ() + ChatColor.YELLOW + ")" + ChatColor.AQUA + " has been set!";

                if (selection.isComplete()) {
                    message += ChatColor.RED + " (" + ChatColor.YELLOW + selection.getCuboid().getVolume() + ChatColor.AQUA + " blocks" + ChatColor.RED + ")";
                }

                player.sendMessage(message);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(Selection.SELECTION_WAND)) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().getInventory().remove(Selection.SELECTION_WAND);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        event.getPlayer().getInventory().remove(Selection.SELECTION_WAND);
    }

}
