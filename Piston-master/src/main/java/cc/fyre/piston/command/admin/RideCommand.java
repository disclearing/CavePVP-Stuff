package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RideCommand {

    @Command(
            names = {"ride"},
            permission = "command.ride"
    )
    public static void execute(Player player,@Parameter(name = "player") Player target) {

        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage("Really?");
            return;
        }

        target.setPassenger(player);
        player.sendMessage(ChatColor.GOLD + "Now riding: " + target.getDisplayName());

    }

}
