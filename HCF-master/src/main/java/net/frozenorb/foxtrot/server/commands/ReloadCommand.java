package net.frozenorb.foxtrot.server.commands;

import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand {

    @Command(names = { "foxtrot reload" }, description = "Reload the Foxtrot configuration", permission = "op", hidden = true)
    public static void execute(CommandSender sender) {
        Foxtrot.getInstance().reloadConfig();
        sender.sendMessage(ChatColor.GREEN.toString() + "Reloaded Foxtrot!");
    }

}
