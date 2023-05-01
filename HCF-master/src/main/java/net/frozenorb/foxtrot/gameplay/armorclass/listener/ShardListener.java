package net.frozenorb.foxtrot.gameplay.armorclass.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
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

public class ShardListener implements Listener {

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

        final String fullEnchantLore = ChatColor.stripColor(bookLore.get(1)).replace("❙ Armor Class: ", "");
        final ArmorClass armorClass = Foxtrot.getInstance().getArmorClassHandler().findArmorClass(fullEnchantLore.replace(" ", ""));

        if (armorClass == null) {
            player.sendMessage(ChatColor.RED + "That enchant could not be found! Contact an admin! The lore is " + fullEnchantLore);
            return;
        }

        if (Foxtrot.getInstance().getArmorClassHandler().findByPiece(applyToItem) != null) {
            player.sendMessage(ChatColor.RED + "There is already an Armor Class on this piece!");
            return;
        }

        if (!armorClass.findApplicableItems().contains(material)) {
            player.sendMessage(ChatColor.RED + "This armor class can't be applied to this item!");
            return;
        }

        final List<String> applyToLore = applyToMeta.hasLore() ? applyToMeta.getLore() : new ArrayList<>();

        applyToLore.add("");
        applyToLore.add(armorClass.getChatColor() + "Armor Class: " + ChatColor.WHITE + ChatColor.stripColor(armorClass.getDisplayName()));
        applyToLore.add(armorClass.getChatColor() + "Perks:");
        for (String perk : armorClass.getPerks()) {
            applyToLore.add(ChatColor.translate(armorClass.getChatColor() + "❙ &f" + perk));
        }
        applyToLore.add("");

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

        if (itemStack.getType() != Material.FLINT) {
            return false;
        }

        if (!itemStack.getItemMeta().getDisplayName().endsWith(" Shard")) {
            return false;
        }

        final List<String> lore = itemStack.getItemMeta().getLore();

        return lore.contains(CC.translate("&aDouble Click this shard on the armor you want to put it on!"));
    }
}
