package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.EnchantmentWrapper;
import cc.fyre.proton.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantCommand {

    @Command(
            names = {"enchant"},
            permission = "command.enchant"
    )
    public static void execute(Player player,@Parameter(name = "enchantment") Enchantment enchantment,@Parameter(name = "level") int level,@Flag(value = "h")boolean hotbar) {

        if (hotbar) {

            final EnchantmentWrapper wrapper = EnchantmentWrapper.parse(enchantment);

            if (level > wrapper.getMaxLevel() && !player.isOp()) {
                player.sendMessage(ChatColor.RED + "The maximum enchanting level for " + ChatColor.WHITE + wrapper.getFriendlyName() + " is " + ChatColor.WHITE + level + ChatColor.RED + ". You provided " + ChatColor.WHITE + level + ChatColor.RED + ".");
                return;
            }

            int enchanted = 0;

            for(int slot = 0; slot < 9; ++slot) {

                final ItemStack item = player.getInventory().getItem(slot);

                if (item == null || !wrapper.canEnchantItem(item)) {
                    continue;
                }

                wrapper.enchant(item, level);
                ++enchanted;
            }

            if (enchanted == 0) {
                player.sendMessage(ChatColor.RED + "No items in your hotbar can be enchanted with " + wrapper.getFriendlyName() + ".");
                return;
            }

            if (level > wrapper.getMaxLevel()) {
                player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "WARNING: " + ChatColor.YELLOW + "You added " + ChatColor.RED + wrapper.getFriendlyName() + " " + level + ChatColor.YELLOW + " to this item. The default maximum value is " + ChatColor.RED + wrapper.getMaxLevel() + ChatColor.YELLOW + ".");
            }

            player.sendMessage(ChatColor.GOLD + "Enchanted " + ChatColor.WHITE + enchanted + ChatColor.GOLD + " items with " + ChatColor.WHITE + wrapper.getFriendlyName() + ChatColor.GOLD + " level " + ChatColor.WHITE + level + ChatColor.GOLD + ".");
            player.updateInventory();
            return;
        }

        if (player.getItemInHand() == null) {
            player.sendMessage(ChatColor.RED + "You are not holding an item.");
            return;
        }

        if (level == 0) {
            player.getItemInHand().removeEnchantment(enchantment);
            player.sendMessage(ChatColor.RED + "Removed enchant \"" + ChatColor.WHITE + enchantment.getName() + ChatColor.RED + "\" from " + ChatColor.WHITE + ItemUtils.getName(player.getItemInHand()));
            return;
        }

        if (level < 0) {
            player.sendMessage(ChatColor.RED + "Level must be positive.");
            return;
        }

        final EnchantmentWrapper wrapper = EnchantmentWrapper.parse(enchantment);

        if (level > enchantment.getMaxLevel()) {

            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "The maximum enchanting level for " + ChatColor.WHITE + wrapper.getFriendlyName() + " is " + ChatColor.WHITE + level + ChatColor.RED + ". You provided " + ChatColor.WHITE + level + ChatColor.RED + ".");
                return;
            }

            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "WARNING: " + ChatColor.YELLOW + "You added " + ChatColor.RED + wrapper.getFriendlyName() + " " + level + ChatColor.YELLOW + " to this item. The default maximum value is " + ChatColor.RED + wrapper.getMaxLevel() + ChatColor.YELLOW + ".");
        }

        wrapper.enchant(player.getItemInHand(), level);
        player.sendMessage(ChatColor.GOLD + "Enchanted your " + ChatColor.WHITE + ItemUtils.getName(player.getItemInHand()) + ChatColor.GOLD + " with " + ChatColor.WHITE + wrapper.getFriendlyName() + ChatColor.GOLD + " level " + ChatColor.WHITE + level + ChatColor.GOLD + ".");        player.updateInventory();
    }
}
