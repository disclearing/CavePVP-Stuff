package cc.fyre.neutron.command.prefix;

import cc.fyre.neutron.prefix.Prefix;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PrefixRenameCommand {

    @Command(
            names = {"prefix rename","prefix setname","prefix changename","prefix modifyname"},
            permission = "neutron.command.prefix.rename"
    )
    public static void execute(CommandSender sender,@Parameter(name = "prefix") Prefix prefix,@Parameter(name = "name")String name) {

        if (prefix.getName().equalsIgnoreCase(name)) {
            sender.sendMessage(prefix.getName() + ChatColor.RED + "'s name is already " + ChatColor.WHITE + prefix.getName() + ChatColor.RED + ".");
            return;
        }

        final String oldName = prefix.getName();

        prefix.setName(name);
        prefix.save();

        sender.sendMessage(ChatColor.GOLD + "Renamed " + oldName + " to" + ChatColor.GRAY + " -> " + ChatColor.WHITE + name + ChatColor.GOLD + ".");
    }


}
