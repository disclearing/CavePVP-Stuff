package cc.fyre.proton.hologram.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.hologram.construct.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class HologramDeleteCommand {

    @Command(
            names = {"hologram delete","holo delete"},
            permission = "proton.command.hologram.delete"
    )
    public static void execute(CommandSender sender,@Parameter(name = "hologram")Hologram hologram) {
        hologram.delete();

        sender.sendMessage(ChatColor.GOLD + "Deleted hologram with id " + ChatColor.WHITE + hologram.id() + ChatColor.GOLD + ".");
    }

}
