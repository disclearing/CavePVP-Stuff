package net.frozenorb.foxtrot.gameplay.armorclass.listener;

import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClassHandler;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import net.frozenorb.foxtrot.gameplay.armorclass.event.ArmorSetEquipEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class ArmorClassListener implements Listener {

    public final ArmorClassHandler armorClassHandler;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEquipmentSet(EquipmentSetEvent event) {
        final ItemStack itemStack = event.getNewItem();

        if (itemStack == null || itemStack.getType() == Material.AIR)
            return;

        Player player = (Player) event.getHumanEntity();
        ArmorClass armorClass = armorClassHandler.findWearing(player);

        if (armorClass == null) {
            return;
        }

        if (armorClassHandler.getActiveSets().put(player.getUniqueId(), armorClass) == null) {
            ArmorSetEquipEvent equipEvent = new ArmorSetEquipEvent(player, armorClass);
            equipEvent.call();

            if(equipEvent.isCancelled()) {
                armorClassHandler.getActiveSets().remove(player.getUniqueId());
                return;
            }

            armorClass.apply(player);

            player.sendMessage(ChatColor.translate("&4&lArmor Classes &7┃ &fYou have equipped the " + armorClass.getDisplayName() + "&f armor set!"));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ArmorClass armorClass = armorClassHandler.findWearing(player);

        if (armorClass == null) {
            return;
        }

        if (armorClassHandler.getActiveSets().put(player.getUniqueId(), armorClass) == null) {
            armorClass.apply(player);

            player.sendMessage(ChatColor.translate("&4&lArmor Classes &7┃ &fYou have equipped the " + armorClass.getDisplayName() + "&f armor set!"));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEquipmentUnset(EquipmentSetEvent event) {
        final Player player = (Player) event.getHumanEntity();
        final ArmorClass armorClass = armorClassHandler.getActiveSets().remove(player.getUniqueId());

        if (armorClass == null) {
            return;
        }

        armorClass.unapply(player);
//        player.sendMessage(ChatColor.translate("&4&lArmor Classes &7┃ &fYou have un-equipped the " + armorClass.getDisplayName() + "&f armor set!"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        handleRemoval(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onKick(PlayerKickEvent event) {
        final Player player = event.getPlayer();
        handleRemoval(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        handleRemoval(player);
    }

    private void handleRemoval(Player player) {
        final ArmorClass armorClass = armorClassHandler.getActiveSets().remove(player.getUniqueId());

        if (armorClass != null) {
            armorClass.unapply(player);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getView().getType() == InventoryType.ANVIL) {
            if(event.getRawSlot() >= 0 && event.getRawSlot() <= 2) {
                ItemStack itemZero = event.getView().getItem(0), itemOne = event.getView().getItem(1);
                if((itemZero != null && itemZero.getType() != Material.AIR && armorClassHandler.findByPiece(itemZero) != null) ||
                        (itemOne != null && itemOne.getType() != Material.AIR && armorClassHandler.findByPiece(itemOne) != null)) {
//                if(armorSetSystem.getByPiece(event.getView().getItem(0)) != null || armorSetSystem.getByPiece(event.getView().getItem(1)) != null) {
                    ((Player)event.getWhoClicked()).sendMessage(ChatColor.translate("&cYou can't rename armor sets in anvils."));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        final Action action = event.getAction();

        if (action == Action.PHYSICAL) return;

        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();

        if (itemStack == null || !itemStack.getType().equals(Material.CHEST)) {
            return;
        }

        final ArmorClass armorClass = armorClassHandler.findByRedeemItem(itemStack);

        if (armorClass == null) {
            return;
        }

        event.setCancelled(true);

        if (itemStack.getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            itemStack.setAmount(itemStack.getAmount()-1);
        }

        player.getInventory().addItem(armorClass.createPiece(ArmorPiece.HELMET));
        player.getInventory().addItem(armorClass.createPiece(ArmorPiece.CHESTPLATE));
        player.getInventory().addItem(armorClass.createPiece(ArmorPiece.LEGGINGS));
        player.getInventory().addItem(armorClass.createPiece(ArmorPiece.BOOTS));
    }
}
