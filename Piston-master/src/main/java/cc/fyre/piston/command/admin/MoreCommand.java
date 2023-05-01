package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MoreCommand {

    @Command(
            names = {"more","stack"},
            permission = "command.more"
    )
    public static void execute(Player player,@Flag(value = {"b"})boolean bypass) {

        if (player.getItemInHand() == null) {
            player.sendMessage(ChatColor.RED + "You are not holding an item.");
            return;
        }

        player.getItemInHand().setAmount(bypass ? 64:player.getItemInHand().getMaxStackSize());
        player.sendMessage(ChatColor.GOLD + "There you go.");

    }

}
