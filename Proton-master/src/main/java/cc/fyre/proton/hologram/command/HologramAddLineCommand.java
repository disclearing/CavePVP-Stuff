package cc.fyre.proton.hologram.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.hologram.construct.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class HologramAddLineCommand {

    @Command(
            names = {"hologram addline","holo addline"},
            permission = "proton.command.hologram.addline"
    )
    public static void execute(CommandSender sender,@Parameter(name = "hologram")Hologram hologram,@Parameter(name = "text",wildcard = true)String text) {
        hologram.addLines(ChatColor.translateAlternateColorCodes('&',text));
    }

}
