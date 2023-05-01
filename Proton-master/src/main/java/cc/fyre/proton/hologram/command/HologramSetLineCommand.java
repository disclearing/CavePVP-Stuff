package cc.fyre.proton.hologram.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.hologram.construct.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class HologramSetLineCommand {

    @Command(
            names = {"hologram setline","holo setline"},
            permission = "proton.command.hologram.setline"
    )
    public static void execute(CommandSender sender,@Parameter(name = "hologram")Hologram hologram,@Parameter(name = "number")int number,@Parameter(name = "text",wildcard = true)String text) {

        if (number > hologram.getLines().size()) {
            sender.sendMessage(ChatColor.RED + "This hologram does not have that many lines!");
            return;
        }

        hologram.setLine(number,text);
    }


}
