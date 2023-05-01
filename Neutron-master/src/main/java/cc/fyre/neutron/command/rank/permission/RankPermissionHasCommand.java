package cc.fyre.neutron.command.rank.permission;

import cc.fyre.neutron.rank.Rank;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RankPermissionHasCommand {

    @Command(
            names = {"rank permission has"},
            permission = "neutron.command.rank.permission.has"
    )
    public static void execute(CommandSender sender, @Parameter(name = "rank") Rank rank, @Parameter(name = "permission")String permission) {

        if (!rank.getPermissions().contains(permission)) {
            sender.sendMessage(rank.getFancyName() + ChatColor.RED + " does not have the permission node " + ChatColor.WHITE + permission + ChatColor.RED + ".");
            return;
        } else {
            sender.sendMessage(rank.getFancyName() + ChatColor.GREEN + " has the permission node " + ChatColor.WHITE + permission + ChatColor.GREEN + ".");
        }


    }
}

