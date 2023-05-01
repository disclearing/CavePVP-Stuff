package net.frozenorb.foxtrot.gameplay.kitmap.game.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.kitmap.game.menu.HostMenu;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HostCommand {

    @Command(names = {"host", "game host"}, description = "Host a KitMap Event", permission = "", async = true)
    public static void execute(Player player) {
        if (CustomTimerCreateCommand.isSOTWTimer() && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "You can't host an event during SOTW!");
            return;
        }

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You can't host an event while spawn-tagged!");
            return;
        }

        new HostMenu().openMenu(player);
    }

}
