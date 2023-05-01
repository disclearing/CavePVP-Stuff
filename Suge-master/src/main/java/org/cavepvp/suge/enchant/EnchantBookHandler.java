package org.cavepvp.suge.enchant;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.ItemUtils;
import net.frozenorb.foxtrot.util.CC;
import org.apache.commons.lang.WordUtils;
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
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.enchant.data.CustomEnchant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnchantBookHandler implements Listener {

    private Suge instance;

    public EnchantBookHandler(Suge instance) {
        this.instance = instance;

        this.instance.getServer().getPluginManager().registerEvents(this, this.instance);
    }

    public ItemStack getBook(CustomEnchant customEnchant, String romanNumber) {
        final StringBuilder stringBuilder = new StringBuilder();
        final List<String> toReturn = new ArrayList<>();

        for (String s : customEnchant.getDescription()) {
            toReturn.add(ChatColor.GRAY + ChatColor.translate(s));
        }

        for (Material material : customEnchant.getApplicableItems()) {
            stringBuilder.append(stringBuilder.length() == 0 ? "" : ", ").append(ItemUtils.getName(new ItemStack(material, 1)));
        }

        toReturn.add("");
        toReturn.add(ChatColor.translate("&4&l┃ &fEnchant: &c" + customEnchant.getName() + " " + romanNumber));
        toReturn.add(ChatColor.translate("&4&l┃ &fLevel: &c" + customEnchant.getLevel().getDisplayName()));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&cDouble Click this book on the armor you want to put it on!"));

        return ItemBuilder.of(Material.BOOK).name(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Enchant Book")
                .setLore(toReturn).build().clone();
    }

    public boolean isSimilar(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getDisplayName() == null) {
            return false;
        }

        if (itemStack.getType() != Material.BOOK) {
            return false;
        }

        if (!itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Enchant Book")) {
            return false;
        }

        final List<String> lore = itemStack.getItemMeta().getLore();

        return lore.contains(CC.translate("&cDouble Click this book on the armor you want to put it on!"));
    }

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

        final String fullEnchantLore = ChatColor.stripColor(bookLore.get(2)).replace("┃ Enchant: ", "");
        final CustomEnchant customEnchant = this.instance.getEnchantHandler().findCustomEnchant(fullEnchantLore);

        if (customEnchant == null) {
            player.sendMessage(ChatColor.RED + "That enchant could not be found! Contact an admin! The lore is " + fullEnchantLore);
            return;
        }

        if (!customEnchant.getApplicableItems().contains(material)) {
            player.sendMessage(ChatColor.RED + "That enchant could not be applied to your armor part.");
            return;
        }

        final List<String> applyToLore = applyToMeta.hasLore() ? applyToMeta.getLore() : new ArrayList<>();

        if (applyToLore.stream().anyMatch(it -> it.contains(customEnchant.getName()))) {
            player.sendMessage(ChatColor.RED + "You already have that custom enchant on that piece!");
            return;
        }

        applyToLore.add(ChatColor.RED + fullEnchantLore);
        applyToMeta.setLore(applyToLore);
        applyToItem.setItemMeta(applyToMeta);
        event.setCurrentItem(applyToItem.clone());
        event.setCancelled(true);

        player.setItemOnCursor(null);

        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        player.sendMessage(ChatColor.GREEN + "Applied custom enchant!");
    }
}
