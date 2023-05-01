package cc.fyre.universe.command;

import cc.fyre.universe.Universe;
import cc.fyre.universe.server.Server;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class UniverseEnvironmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "Universe Environment Dump");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "Name: " + ChatColor.WHITE + Universe.getInstance().getServerName());
        sender.sendMessage(ChatColor.GOLD + "Group: " + ChatColor.WHITE + Universe.getInstance().getGroup().getName());

        for (Server server : Universe.getInstance().getUniverseHandler().getServers()) {
            if (server.getOnlinePlayers().get() > 0) {
                sender.sendMessage(server.getName() + " has players");
            }

//            sender.sendMessage(server.getName() + " <--- server");
        }
        return false;
    }
}
