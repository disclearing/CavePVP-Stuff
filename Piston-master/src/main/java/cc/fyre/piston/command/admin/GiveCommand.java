package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand {

    @Command(
            names = {"item", "i", "get"},
            permission = "command.item"
    )
    public static void item(Player sender, @Parameter(name = "item") ItemStack item, @Parameter(name = "amount",defaultValue = "1") int amount) {
        if (amount < 1) {
            sender.sendMessage(ChatColor.RED + "The amount must be greater than zero.");
        } else {
            item.setAmount(amount);
            sender.getInventory().addItem(new ItemStack[]{item});
            sender.sendMessage(ChatColor.GOLD + "Giving " + ChatColor.WHITE + amount + ChatColor.GOLD + " of " + ChatColor.WHITE + ItemUtils.getName(item) + ChatColor.GOLD + ".");
        }
    }

    @Command(
            names = {"give"},
            permission = "command.give"
    )
    public static void give(CommandSender sender,@Parameter(name = "item") ItemStack item,@Parameter(name = "amount",defaultValue = "1") int amount,@Parameter(name = "player",defaultValue = "self") Player target) {
        if (amount < 1) {
            sender.sendMessage(ChatColor.RED + "The amount must be greater than zero.");
        } else {
            item.setAmount(amount);
            target.getInventory().addItem(new ItemStack[]{item});
            sender.sendMessage(ChatColor.GOLD + "Giving " + ChatColor.WHITE + target.getDisplayName() + ChatColor.WHITE + " " + amount + ChatColor.GOLD + " of " + ChatColor.WHITE + ItemUtils.getName(item) + ChatColor.GOLD + ".");
        }
    }
}
