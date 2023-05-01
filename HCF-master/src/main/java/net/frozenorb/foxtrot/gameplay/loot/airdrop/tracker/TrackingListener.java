package net.frozenorb.foxtrot.gameplay.loot.airdrop.tracker;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.AirDropHandler;
import org.bukkit.event.Listener;

@AllArgsConstructor
public class TrackingListener implements Listener {
    private Foxtrot instance;
    private AirDropHandler airDropHandler;

//    @EventHandler(priority = EventPriority.MONITOR)
//    private void onDrop(PlayerDropItemEvent event) {
//        if (event.isCancelled()) {
//            return;
//        }
//
//        final Player player = event.getPlayer();
//
//        final ItemStack itemStack = event.getItemDrop().getItemStack();
//
//        if (!itemStack.isSimilar(airDropHandler.getItemStack())) {
//            return;
//        }
//        final UUID findUUID = UUID.fromString(ChatColor.stripColor(itemStack.getItemMeta().getLore().get(3)).replace("Tracking ID: ", ""));
//        final AirdropTracker airdropTracker = airDropHandler.getTracker().get(findUUID);
//
//        if (airdropTracker == null) {
//            player.sendMessage(ChatColor.RED + "Failed to drop airdrop, please contact an administrator immediately!");
//            event.setCancelled(true);
//            return;
//        }
//
//        final Location location = event.getItemDrop().getLocation();
//
//        airdropTracker.getActions().put(player.getName() + " has dropped " + itemStack.getAmount() + "x Airdrops at " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + " @ " + System.currentTimeMillis(), System.currentTimeMillis());
//    }
//
//    @EventHandler(priority = EventPriority.MONITOR)
//    private void onPickup(PlayerPickupItemEvent event) {
//        if (event.isCancelled()) {
//            return;
//        }
//
//        final Player player = event.getPlayer();
//
//        final ItemStack itemStack = event.getItem().getItemStack();
//
//        if (!itemStack.isSimilar(airDropHandler.getItemStack())) {
//            return;
//        }
//        final UUID findUUID = UUID.fromString(ChatColor.stripColor(itemStack.getItemMeta().getLore().get(3)).replace("Tracking ID: ", ""));
//        final AirdropTracker airdropTracker = airDropHandler.getTracker().get(findUUID);
//
//        if (airdropTracker == null) {
//            player.sendMessage(ChatColor.RED + "Failed to drop airdrop, please contact an administrator immediately!");
//            event.setCancelled(true);
//            return;
//        }
//
//        final Location location = event.getItem().getLocation();
//
//        airdropTracker.getActions().put(player.getName() + " has picked up " + itemStack.getAmount() + "x Airdrops at " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + " @ " + System.currentTimeMillis(), System.currentTimeMillis());
//    }
}
