package net.frozenorb.foxtrot.server;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.Arrays;
import java.util.List;

public final class ServerListener implements Listener {

	private static final List<Material> MOB_DROPS = Arrays.asList(
			Material.SPIDER_EYE,
			Material.BONE,
			Material.ARROW,
			Material.ROTTEN_FLESH,
			Material.BOW,
			Material.STRING
	);

//	@EventHandler(priority = EventPriority.LOWEST) // auto inventory listener
//	private void onEntityDeath(EntityDeathEvent event) {
//		if (event.getEntityType() == EntityType.PLAYER) {
//			return;
//		}
//
//		Player killer = event.getEntity().getKiller();
//		if (killer != null && Foxtrot.getInstance().getMobDropsPickupMap().isMobPickup(killer.getUniqueId())) {
//			if (event.getDrops().removeIf(stack -> InventoryUtils.addAmountToInventory(killer.getInventory(), stack, stack.getAmount()))) {
//				killer.updateInventory();
//			}
//		}
//	}

//	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
//	private void onPickup(PlayerPickupItemEvent event) {
//		if (!Foxtrot.getInstance().getMobDropsPickupMap().isMobPickup(event.getPlayer().getUniqueId())
//				&& MOB_DROPS.contains(event.getItem().getItemStack().getType())) {
//
//			event.setCancelled(true);
//		}
//	}

}
