package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageRawCommand {

    @Command(
            names = {"messageraw","msgraw"},
            permission = "command.messageraw"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") Player player,@Parameter(name = "message",wildcard = true) String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
    }

}
