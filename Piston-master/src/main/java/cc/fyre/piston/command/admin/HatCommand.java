package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import org.bukkit.Color;
import org.bukkit.entity.Player;

public class HatCommand {

    @Command(
            names = {"hat"},
            permission = "command.hat"
    )
    public static void execute(Player player) {

        if (player.getItemInHand() == null) {
            player.sendMessage(Color.RED + "You do not have anything in your hand.");
            return;
        }

        player.getInventory().setHelmet(player.getItemInHand());

    }
}
