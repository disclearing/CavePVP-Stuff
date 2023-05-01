package cc.fyre.neutron.security.listener;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.security.AlertType;
import cc.fyre.neutron.security.SecurityHandler;
import cc.fyre.proton.util.ItemUtils;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class SecurityListener implements Listener {
    private final SecurityHandler securityHandler;
    private final Neutron instance;
    private final List<String> op_items = Arrays.asList("airdrop", "air drop", "key", "lootbox", "mystery box", "mysterybox");

    public SecurityListener(Neutron instance, SecurityHandler securityHandler) {
        this.instance = instance;
        this.securityHandler = securityHandler;
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();

        if (player.hasPermission("neutron.staff")) {
            event.getItemDrop().setMetadata("DROPPED_BY", new FixedMetadataValue(this.instance, player.getUniqueId().toString()));

            final ItemStack itemStack = event.getItemDrop().getItemStack();

            if (itemStack != null) {
                final Location location = event.getItemDrop().getLocation();
                final List<String> description = new ArrayList<>();
                description.add("Dropped At: " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
                description.add("Dropped: " + itemStack.getAmount() + "x " + ItemUtils.getName(itemStack));
                description.add("Item Type: " + itemStack.getType().name());
                for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
                    description.add("Enchant: " + entry.getKey().getName() + " " + entry.getValue());
                }

                if (itemStack.getItemMeta() != null && itemStack.getItemMeta().getLore() != null) {
                    description.add("");
                    description.add("Lore:");
                    for (String s : itemStack.getItemMeta().getLore()) {
                        description.add("- " + ChatColor.translate(s));
                    }
                }

                this.securityHandler.addSecurityAlert(player.getUniqueId(), null, AlertType.DROPPED_ITEMS, this.isUrgent(itemStack), description);
            }
        }
    }

    @EventHandler
    private void onPickup(PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();

        if (event.getItem().hasMetadata("DROPPED_BY")) {
            final UUID droppedBy = UUID.fromString(event.getItem().getMetadata("DROPPED_BY").get(0).asString());
            final ItemStack itemStack = event.getItem().getItemStack();
            final Location location = event.getItem().getLocation();
            final List<String> description = new ArrayList<>();
            boolean urgent = this.isUrgent(itemStack);
            description.add("Picked Up At: " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
            description.add("Picked Up: " + itemStack.getAmount() + "x " + ItemUtils.getName(itemStack));
            description.add("Item Type: " + itemStack.getType().name());
            for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
                description.add("Enchant: " + entry.getKey().getName() + " " + entry.getValue());
            }

            if (itemStack.getItemMeta() != null && itemStack.getItemMeta().getLore() != null) {
                description.add("");
                description.add("Lore:");
                for (String s : itemStack.getItemMeta().getLore()) {
                    description.add("- " + ChatColor.translate(s));
                }
            }

            if (player.getName().equalsIgnoreCase("z5") || player.getName().equalsIgnoreCase("ran")) {
                urgent = true;
            }

            this.securityHandler.addSecurityAlert(droppedBy, player.getUniqueId(), AlertType.PICKED_UP_ITEMS, urgent, description);
        }
    }

    @EventHandler
    private void onMove(InventoryPickupItemEvent event) {
        if (!event.getInventory().getType().equals(InventoryType.HOPPER)) {
            return;
        }

        if (event.getItem().hasMetadata("DROPPED_BY")) {
            final UUID droppedBy = UUID.fromString(event.getItem().getMetadata("DROPPED_BY").get(0).asString());
            final ItemStack itemStack = event.getItem().getItemStack();
            final Location location = event.getItem().getLocation();
            final List<String> description = new ArrayList<>();
            boolean urgent = this.isUrgent(itemStack);
            description.add("IT'S A HOPPER!");
            description.add("Picked Up At: " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
            description.add("Picked Up: " + itemStack.getAmount() + "x " + ItemUtils.getName(itemStack));
            description.add("Item Type: " + itemStack.getType().name());
            for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
                description.add("Enchant: " + entry.getKey().getName() + " " + entry.getValue());
            }

            if (itemStack.getItemMeta() != null && itemStack.getItemMeta().getLore() != null) {
                description.add("");
                description.add("Lore:");
                for (String s : itemStack.getItemMeta().getLore()) {
                    description.add("- " + ChatColor.translate(s));
                }
            }

            this.securityHandler.addSecurityAlert(droppedBy, null, AlertType.CHESTS, urgent, description);
        }
    }

    @EventHandler
    private void onCreative(InventoryCreativeEvent event) {
        final Player player = (Player) event.getWhoClicked();

        if (!event.getClick().equals(ClickType.MIDDLE)) {
            return;
        }

        final ItemStack cursor = event.getCursor();
        final ItemStack currentItem = event.getCurrentItem();

        if (cursor != null && !cursor.getType().equals(Material.AIR)) {
            final List<String> description = new ArrayList<>();
            boolean urgent = this.isUrgent(cursor);
            description.add("THIS WAS CURSOR ITEM!");
            description.add("Middle Clicked: " + ItemUtils.getName(cursor));
            description.add("Item Type: " + cursor.getType().name());
            for (Map.Entry<Enchantment, Integer> entry : cursor.getEnchantments().entrySet()) {
                description.add("Enchant: " + entry.getKey().getName() + " " + entry.getValue());
            }

            if (cursor.getItemMeta() != null && cursor.getItemMeta().getLore() != null) {
                description.add("");
                description.add("Lore:");
                for (String s : cursor.getItemMeta().getLore()) {
                    description.add("- " + ChatColor.translate(s));
                }
            }

            this.securityHandler.addSecurityAlert(player.getUniqueId(), null, AlertType.MIDDLE_CLICK, urgent, description);
        }

        if (currentItem != null && !currentItem.getType().equals(Material.AIR)) {
            final List<String> description = new ArrayList<>();
            boolean urgent = this.isUrgent(currentItem);
            description.add("THIS WAS THE CLICKED ITEM!");
            description.add("Middle Clicked: " + ItemUtils.getName(currentItem));
            description.add("Item Type: " + currentItem.getType().name());
            for (Map.Entry<Enchantment, Integer> entry : currentItem.getEnchantments().entrySet()) {
                description.add("Enchant: " + entry.getKey().getName() + " " + entry.getValue());
            }

            if (currentItem.getItemMeta() != null && currentItem.getItemMeta().getLore() != null) {
                description.add("");
                description.add("Lore:");
                for (String s : currentItem.getItemMeta().getLore()) {
                    description.add("- " + ChatColor.translate(s));
                }
            }

            this.securityHandler.addSecurityAlert(player.getUniqueId(), null, AlertType.MIDDLE_CLICK, urgent, description);
        }
    }


    public boolean isUrgent(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null || itemStack.getType().equals(Material.AIR)) {
            return false;
        }

        if (itemStack.getItemMeta().getDisplayName() == null) {
            return false;
        }

        if (itemStack.getItemMeta().getLore() == null) {
            return false;
        }

        String displayName = itemStack.getItemMeta().getDisplayName();

        if (op_items.stream().anyMatch(it -> displayName.toLowerCase().contains(it))) {
            return true;
        }

        for (String s : itemStack.getItemMeta().getLore()) {
            if (op_items.stream().anyMatch(it -> s.toLowerCase().contains(it))) {
                return true;
            }
        }

        return false;
    }

}
