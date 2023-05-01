package net.frozenorb.foxtrot.gameplay.blockshop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.ItemUtils;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.blockshop.ShopItem;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class ItemButton extends Button {

    private final ShopItem item;

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;

        double balance = Foxtrot.getInstance().getEconomyHandler().getBalance(player.getUniqueId());
        int price = item.getPrice();

        if (balance < price) {
            player.sendMessage(ChatColor.RED + "You do not have enough money to purchase this.");
            return;
        }

        if (!InventoryUtils.addAmountToInventory(player.getInventory(), item.getItemStack().clone(), item.getItemStack().getAmount())) {
            player.sendMessage(ChatColor.RED + "Your inventory is full.");
            return;
        }

        Foxtrot.getInstance().getEconomyHandler().setBalance(player.getUniqueId(), balance - price);
        player.sendMessage(ChatColor.GREEN + "You have purchased " + ItemUtils.getName(item.getItemStack()) + ChatColor.GREEN + " for $" + price + ".");
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return ItemBuilder.copyOf(item.getItemStack().clone())
                .name(CC.GREEN + CC.BOLD + ItemUtils.getName(item.getItemStack()))
                .addToLore(CC.GRAY + "Price: $" + item.getPrice())
                .build();
    }

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }
}