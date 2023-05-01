package cc.fyre.universe.command;

import cc.fyre.universe.Universe;
import cc.fyre.universe.server.Server;
import cc.fyre.universe.util.BungeeUtil;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class ServerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to run this command!");
            return true;
        }

        if (!commandSender.hasPermission("universe.command.server")) {
            commandSender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /server [name]");
            return false;
        }

        final Player player = (Player) commandSender;
        final Server server = Universe.getInstance().getUniverseHandler().serverFromName(args[0]);

        if (server == null) {
            player.sendMessage(ChatColor.RED + "That server doesn't exist!");
            return false;
        }

        BungeeUtil.sendToServer(player,server.getName());
        return false;
    }
}
