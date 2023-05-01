package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.brewer.FancyBrewerHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BrewerCommand {

    @Command(names = {"brewer give", "fancybrewer give"}, permission = "op")
    public static void execute(CommandSender sender, @Parameter(name = "player")Player target, @Parameter(name = "amount")int amount) {
        target.getInventory().addItem(FancyBrewerHandler.INSTANCE.createItemStack(amount));
        sender.sendMessage(ChatColor.translate("&aGave &f" + target.getName() + " &a" + amount + "x Fancy Brewers"));
    }

}
