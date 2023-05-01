package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class StaffBoardCommand {

    @Command(
            names = {"staffboard"},
            permission = "hcf.command.staffboard"
    )
    public static void execute(Player player) {
        if (player.hasMetadata("STAFF_BOARD")) {
            player.removeMetadata("STAFF_BOARD", Foxtrot.getInstance());
            player.sendMessage(ChatColor.RED + "You have disabled your staff board.");
        } else {
            player.sendMessage(ChatColor.GREEN + "You have enabled your staff board.");
            player.setMetadata("STAFF_BOARD", new FixedMetadataValue(Foxtrot.getInstance(), true));
        }
    }

}
