package net.frozenorb.foxtrot.gameplay.loot.crate.listener;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.gameplay.loot.crate.Crate;
import net.frozenorb.foxtrot.gameplay.loot.crate.CrateHandler;
import net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class CrateListener implements Listener {
    private CrateHandler crateHandler;

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();

        final Optional<Crate> optionalCrate = crateHandler.findByItemStack(itemStack);

        if (itemStack == null || !optionalCrate.isPresent()) {
            return;
        }

        if (player.isSneaking()) {
            new Menu() {

                @Override
                public String getTitle(Player player) {
                    return optionalCrate.get().getId() + " Loot";
                }

                @Override
                public Map<Integer, Button> getButtons(Player player) {
                    final Map<Integer, Button> toReturn = new HashMap<>();

                    for (CrateItem item : optionalCrate.get().getItems()) {

                        if (item.getItemStack() == null || item.getItemStack().getType().equals(Material.AIR)) {
                            continue;
                        }

                        toReturn.put(toReturn.size(), Button.fromItem(item.getItemStack().clone()));
                    }

                    return toReturn;
                }
            }.openMenu(player);
            return;
        }

        if (itemStack.getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            itemStack.setAmount(itemStack.getAmount()-1);
        }

        event.setCancelled(true);

        crateHandler.openCrate(player, optionalCrate.get());
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItemInHand();

        final Optional<Crate> optionalCrate = crateHandler.findByItemStack(itemStack);

        if (itemStack == null || !optionalCrate.isPresent()) {
            return;
        }

        if (itemStack.getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            itemStack.setAmount(itemStack.getAmount()-1);
        }

        crateHandler.openCrate(player, optionalCrate.get());
    }
}
