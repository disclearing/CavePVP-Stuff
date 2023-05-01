package net.frozenorb.foxtrot.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses.MinerClass;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemsEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class BlockConvenienceListener implements Listener {

    private static List<Integer> TOOL_IDS = Arrays.asList(271, 275, 286, 279, 270, 274, 257, 285, 278, 269, 273, 256, 284, 277, 290, 291, 292, 294, 293);

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockDropItemsEvent(BlockDropItemsEvent event) {
        if (event.getPlayer() == null) {
            return;
        }

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

//        if (!((event.getPlayer().getItemInHand() == null || event.getPlayer().getItemInHand().getType() == Material.AIR) || TOOL_IDS.contains(event.getPlayer().getItemInHand().getType().getId()))) {
//            return;
//        }

        if (event.getToDrop().isEmpty()) {
            return;
        }

        if (event.getBlock().getType().name().contains("SNOW")) {
            return;
        }

        boolean miner = PvPClassHandler.getPvPClass(event.getPlayer()) instanceof MinerClass;
        boolean pickupCobble = Foxtrot.getInstance().getCobblePickupMap().isCobblePickup(event.getPlayer().getUniqueId());

        List<ItemStack> drops = new ArrayList<>();
        for (Item item : event.getToDrop()) {
            if (miner && item.getItemStack().getType() == Material.COBBLESTONE && !pickupCobble) {
                continue;
            }

            drops.add(item.getItemStack());
        }

        event.getToDrop().clear();

        for (ItemStack drop : drops) {
            if (drop.getType() == Material.IRON_ORE) {
                drop.setType(Material.IRON_INGOT);
            }

            if (drop.getType() == Material.GOLD_ORE) {
                drop.setType(Material.GOLD_INGOT);
            }
        }

        List<ItemStack> remainingItems = new ArrayList<>(drops);

        int fallbackSlot = -1;
        boolean modified = false;

        dropLoop:
        for (ItemStack drop : drops) {
            for (int slot = 0; slot < 36; slot++) {
                ItemStack itemAt = event.getPlayer().getInventory().getItem(slot);

                if (itemAt != null && itemAt.getType() == drop.getType() && itemAt.getDurability() == drop.getDurability() && itemAt.getAmount() < itemAt.getType().getMaxStackSize()) {
                    if (!Bukkit.getItemFactory().equals(itemAt.getItemMeta(), drop.getItemMeta())) {
                        continue;
                    }

                    if (itemAt.getAmount() + drop.getAmount() <= itemAt.getType().getMaxStackSize()) {
                        itemAt.setAmount(itemAt.getAmount() + drop.getAmount());
                        remainingItems.remove(drop);
                    } else {
                        int amount = itemAt.getType().getMaxStackSize() - itemAt.getAmount();
                        itemAt.setAmount(itemAt.getType().getMaxStackSize());

                        ItemStack newItem = drop.clone();
                        newItem.setAmount(drop.getAmount() - amount);

                        remainingItems.add(newItem);
                    }

                    modified = true;
                    continue dropLoop;
                }

                if (itemAt == null || itemAt.getType() == Material.AIR) {
                    if (fallbackSlot == -1) {
                        fallbackSlot = slot;
                    }
                }
            }

            if (fallbackSlot != -1) {
                event.getPlayer().getInventory().setItem(fallbackSlot, drop);
                remainingItems.remove(drop);
                modified = true;
                fallbackSlot = -1;
            }
        }

        if (modified) {
            event.getPlayer().updateInventory();
        }

        if (!remainingItems.isEmpty()) {
            for (ItemStack item : remainingItems) {
                if (SOTW_IGNORED_BLOCKS.contains(item.getType()) && CustomTimerCreateCommand.isSOTWTimer()) {
                    continue;
                }
                event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
            }
        }
    }

    private static final List<Material> SOTW_IGNORED_BLOCKS = Arrays.asList(
            Material.COBBLESTONE, Material.STONE,
            Material.GRAVEL, Material.SAND,
            Material.DIRT
    );

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExpBreak(BlockBreakEvent event) {
        event.getPlayer().giveExp(event.getExpToDrop());
        event.setExpToDrop(0);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null || event.getEntity() instanceof Player) {
            return;
        }

        killer.giveExp(event.getDroppedExp());
        event.setDroppedExp(0);
    }

}
