package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.ServerOperator;

public class MassSayCommand {

    @Command(names = {"masssay", "massay", "sayall"}, permission = "op")
    public static void execute(CommandSender sender, @Parameter(name = "message", wildcard = true)String message) {
        Foxtrot.getInstance().getServer().getOnlinePlayers().forEach(it -> it.chat(message));
        Foxtrot.getInstance().getServer().getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(it -> it.sendMessage(sender.getName() + ChatColor.RED + " has used Mass Say!"));
    }

}
