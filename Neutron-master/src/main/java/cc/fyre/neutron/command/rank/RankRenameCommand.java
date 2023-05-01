package cc.fyre.neutron.command.rank;

import cc.fyre.neutron.rank.Rank;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RankRenameCommand {

    @Command(
            names = {"rank setname"},
            permission = "neutron.command.rank.setname"
    )
    public static void execute(CommandSender sender,@Parameter(name = "rank") Rank rank,@Parameter(name = "name")String name) {

        if (rank.getName().equalsIgnoreCase(name)) {
            sender.sendMessage(rank.getFancyName() + ChatColor.RED + "'s name is already " + ChatColor.WHITE + rank.getName() + ChatColor.RED + ".");
            return;
        }

        final String oldName = rank.getName();

        rank.setName(name);
        rank.save();

        sender.sendMessage(ChatColor.GOLD + "Renamed " + oldName + " to" + ChatColor.GRAY + " -> " + ChatColor.WHITE + name + ChatColor.GOLD + ".");
    }

}
