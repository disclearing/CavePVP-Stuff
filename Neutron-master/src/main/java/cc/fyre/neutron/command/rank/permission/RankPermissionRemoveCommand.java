package cc.fyre.neutron.command.rank.permission;

import cc.fyre.neutron.rank.Rank;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RankPermissionRemoveCommand {

    @Command(
            names = {"rank permission remove"},
            permission = "neutron.command.rank.permission.remove"
    )
    public static void execute(CommandSender sender,@Parameter(name = "rank") Rank rank,@Parameter(name = "permission")String permission) {

        if (!rank.getPermissions().contains(permission)) {
            sender.sendMessage(rank.getFancyName() + ChatColor.RED + " does not have the permission node " + ChatColor.WHITE + permission + ChatColor.RED + ".");
            return;
        }

        rank.getPermissions().remove(permission);
        rank.save();

        sender.sendMessage(ChatColor.GOLD + "Removed permission node " + ChatColor.WHITE + permission + ChatColor.GOLD + " to " + rank.getFancyName() + ChatColor.GOLD + ".");
    }
}
