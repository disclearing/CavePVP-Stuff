package org.cavepvp.suge.kit.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.cavepvp.suge.kit.data.Kit;

public class KitAddLoreCommand {

    @Command(names = {"kit addlore"}, permission = "op")
    public static void execute(CommandSender sender, @Parameter(name = "kit") Kit kit, @Parameter(name = "line")String line) {
        kit.getLore().add(line);

        sender.sendMessage(ChatColor.GREEN + "Added " + ChatColor.WHITE + line + ChatColor.GREEN + " to " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + ".");
    }
}
