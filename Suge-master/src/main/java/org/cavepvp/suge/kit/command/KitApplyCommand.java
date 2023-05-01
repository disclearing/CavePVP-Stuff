package org.cavepvp.suge.kit.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavepvp.suge.kit.data.Kit;

public class KitApplyCommand {

    @Command(names = {"kit apply"}, permission = "op")
    public static void apply(CommandSender sender, @Parameter(name = "kit") Kit kit, @Parameter(name = "target")Player target) {
        kit.apply(target);

        sender.sendMessage(ChatColor.GREEN + "Applied the contents of kit " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + " to " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + ".");
    }
}
