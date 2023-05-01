package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RepairCommand {

    @Command(
            names = {"repair","fix"},
            permission = "command.repair"
    )
    public static void repair(Player sender,@Flag(value = {"h"})boolean hotbar) {

        if (hotbar) {

            final List<ItemStack> hotBarItems = new ArrayList<>();

            for (int slot = 0; slot < 9; ++slot) {

                final ItemStack hotBarItem = sender.getInventory().getItem(slot);

                if (hotBarItem != null && hotBarItem.getType() != Material.AIR && Enchantment.DURABILITY.canEnchantItem(hotBarItem)) {
                    hotBarItems.add(hotBarItem);
                }

            }

            if (hotBarItems.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "You have no items in your hotbar to be repaired.");
                return;
            }

            hotBarItems.forEach(hotBarItem -> hotBarItem.setDurability((short)0));
            sender.sendMessage(ChatColor.GOLD + "You have repaired " + ChatColor.WHITE + hotBarItems.size() + ChatColor.GOLD + " items.");

            return;
        }

        final ItemStack item = sender.getItemInHand();

        if (item == null) {
            sender.sendMessage(ChatColor.RED + "You are not holding an item.");
            return;
        }

        if (!Enchantment.DURABILITY.canEnchantItem(item)) {
            sender.sendMessage(ChatColor.RED + ItemUtils.getName(item) + " cannot be repaired.");
            return;
        }

        if (item.getDurability() == 0) {
            sender.sendMessage(ChatColor.RED + "That " + ChatColor.WHITE + ItemUtils.getName(item) + ChatColor.RED + " already has max durability.");
            return;
        }

        item.setDurability((short)0);
        sender.sendMessage(ChatColor.GOLD + "Your " + ChatColor.WHITE + ItemUtils.getName(item) + ChatColor.GOLD + " has been repaired.");

    }

}
