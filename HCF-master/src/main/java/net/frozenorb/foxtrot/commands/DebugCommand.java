package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class DebugCommand {

    @Command(names = {"debug", "pearldebug", "debugpearl", "toggledebug"}, permission = "op", hidden = true)
    public static void execute(Player player) {
        if (player.hasMetadata("DEBUG")) {
            player.removeMetadata("DEBUG", Foxtrot.getInstance());
            player.sendMessage(ChatColor.RED + "Toggled off Debug Mode.");
        } else {
            player.setMetadata("DEBUG", new FixedMetadataValue(Foxtrot.getInstance(), true));
            player.sendMessage(ChatColor.GREEN + "Toggled on Debug Mode.");
        }
    }

    @Command(names = {"showmenus"}, permission = "op", hidden = true)
    public static void sender(Player sender) {
        for (Map.Entry<UUID, Menu> uuidMenuEntry : Menu.getCurrentlyOpenedMenus().entrySet()) {
            final Player player = Foxtrot.getInstance().getServer().getPlayer(uuidMenuEntry.getKey());

            if (player == null) {
                sender.sendMessage(ChatColor.RED + uuidMenuEntry.getKey().toString() + " --> " + uuidMenuEntry.getValue().getTitle(sender));
                continue;
            }

            sender.sendMessage(ChatColor.GREEN + player.getName() + " --> " + uuidMenuEntry.getValue().getTitle(sender));
        }
    }
}
