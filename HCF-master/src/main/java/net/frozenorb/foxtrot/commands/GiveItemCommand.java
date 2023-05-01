package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveItemCommand {

    @Command(names = { "giveitem" }, permission = "op")
    public static void execute(CommandSender sender, @Parameter(name = "player") Player player, @Parameter(name = "item") ItemStack item, @Parameter(name = "amount") int amount) {
        if (amount < 1) {
            sender.sendMessage(ChatColor.RED + "The amount must be greater than zero.");
            return;
        }

        item.setAmount(amount);

        player.getInventory().addItem(item);

    }
    
}

