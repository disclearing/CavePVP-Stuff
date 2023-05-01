package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KillTheKingCommand {
    public static UUID king = null;

    @Command(names = {"ktk", "killtheking"}, permission = "op")
    public static void execute(Player player) {
        if (king != null) {
            king = null;
            player.sendMessage(ChatColor.RED + "Ended Kill The King!");
            return;
        }

        king = player.getUniqueId();

        player.sendMessage(ChatColor.RED + "Set yourself as the king!");
    }
}
