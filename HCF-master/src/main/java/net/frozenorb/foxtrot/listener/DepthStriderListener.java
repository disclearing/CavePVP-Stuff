package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.cavepvp.suge.enchant.data.CustomEnchant;

import java.util.ArrayList;
import java.util.List;

public class DepthStriderListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    private void onClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack bookItem = event.getCursor();
        final ItemStack applyToItem = event.getCurrentItem();

        if (applyToItem == null || bookItem == null) {
            return;
        }

        if (!this.isSimilar(bookItem)) {
            return;
        }

        final Material material = applyToItem.getType();
        final List<String> bookLore = bookItem.getItemMeta().getLore();

        final ItemMeta applyToMeta = applyToItem.getItemMeta();

        if (applyToMeta == null || material == null) {
            player.sendMessage(ChatColor.RED + "Can't use a book on that item!");
            return;
        }

        final List<String> applyToLore = applyToMeta.hasLore() ? applyToMeta.getLore() : new ArrayList<>();

//        if (applyToLore.stream().anyMatch(it -> it.contains())) {
//            player.sendMessage(ChatColor.RED + "You already have that custom enchant on that piece!");
//            return;
//        }
//
//        applyToLore.add(ChatColor.RED + fullEnchantLore);
        applyToMeta.setLore(applyToLore);
        applyToItem.setItemMeta(applyToMeta);
        event.setCurrentItem(applyToItem.clone());
        event.setCancelled(true);

        player.setItemOnCursor(null);

        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        player.sendMessage(ChatColor.GREEN + "Applied custom enchant!");
    }

    public boolean isSimilar(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getDisplayName() == null) {
            return false;
        }

        if (itemStack.getType() != Material.BOOK) {
            return false;
        }

        if (!itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Depth Strider Book")) {
            return false;
        }

        final List<String> lore = itemStack.getItemMeta().getLore();

        return lore.contains(CC.translate("&cDouble Click this book on the boots you want to put it on!"));
    }
}
