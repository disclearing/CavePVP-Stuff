package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class TheSimplyTrashModeCommand {

    @Command(names = {"simplytrashmode"}, permission = "op")
    public static void execute(Player player) {
        if (player.hasMetadata("RIG")) {
            player.sendMessage(ChatColor.RED + "You already are in simplytrash mode!");
            return;
        }

        player.setMetadata("RIG", new FixedMetadataValue(Foxtrot.getInstance(), true));
        player.sendMessage(ChatColor.GREEN + "ACtivated simplytrash mode.");
    }

}
