package cc.fyre.neutron.command.rank.metadata;

import cc.fyre.neutron.rank.Rank;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class RankMetaDataRemoveCommand {

    @Command(
            names = {"rank metadata remove"},
            permission = "neutron.command.rank.metadata.remove"
    )
    public static void execute(CommandSender sender,@Parameter(name = "rank") Rank rank,@Parameter(name = "metadata")String metaData) {

        if (!rank.hasMetaData(metaData)) {
            sender.sendMessage(rank.getFancyName() + ChatColor.RED + " does not have " + ChatColor.WHITE + metaData + ChatColor.RED + " metadata.");
            return;
        }

        rank.getMetaData().remove(metaData);
        rank.save();

        sender.sendMessage(ChatColor.GOLD + "Removed metadata " + ChatColor.WHITE + metaData + ChatColor.GOLD + " from " + rank.getFancyName() + ChatColor.GOLD + ".");
    }

}
