package cc.fyre.universe.command;

import cc.fyre.universe.Universe;
import cc.fyre.universe.UniverseAPI;
import cc.fyre.universe.server.Server;
import cc.fyre.universe.server.fetch.ServerGroup;
import cc.fyre.universe.server.fetch.ServerStatus;
import cc.fyre.universe.util.BungeeUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class HubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to run this command!");
            return true;
        }

        final Player player = (Player) commandSender;

        if (Universe.getInstance().getServerName().equalsIgnoreCase("Prison")) {

            final Server hub = Universe.getInstance().getUniverseHandler().serverFromName("Crypto-Hub");

            if (!hub.canJoin(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "Unable to find a suitable hub, please try again later!");
                return false;
            }

            BungeeUtil.sendToServer(player, hub.getName());
            return false;
        }

        final List<Server> availableHubs = Universe.getInstance().getUniverseHandler().getAvailableServers(player.getUniqueId(),ServerGroup.HUB).stream().filter(it -> !it.getName().equalsIgnoreCase(Universe.getInstance().getServerName())).collect(Collectors.toList());

        if (availableHubs.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Unable to find a suitable hub, please try again later!");
            return false;
        }

        BungeeUtil.sendToServer(player,availableHubs.get(ThreadLocalRandom.current().nextInt(availableHubs.size())).getName());
        return false;
    }
}