package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.util.InventoryUtils;
import cc.fyre.proton.command.Command;

public class CrowbarCommand {

    @Command(names={ "crowbar" }, permission="op")
    public static void crowbar(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        sender.setItemInHand(InventoryUtils.CROWBAR);
        sender.sendMessage(ChatColor.YELLOW + "Gave you a crowbar.");
    }

    @Command(names={ "crowbar" }, permission="op")
    public static void crowbar(CommandSender sender, @Parameter(name = "player") Player target) {
        target.getInventory().addItem(InventoryUtils.CROWBAR);
        target.sendMessage(ChatColor.YELLOW + "You received a crowbar from " + sender.getName() + ".");
        sender.sendMessage(ChatColor.YELLOW + "You gave a crowbar to " + target.getName() + ".");
    }

}