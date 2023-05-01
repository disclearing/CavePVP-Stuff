package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class InvseeCommand {

    @Command(
            names = {"invsee", "invadmin"},
            permission = "command.invadmin"
    )
    public static void execute(Player player, @Parameter(name = "target") Player target) {
        if (player == target) {
            player.sendMessage(ChatColor.RED + "Just press E.");
            return;
        }

        Foxtrot.getInstance().getInventorySeeHandler().openInventory(player, target);
    }
}
