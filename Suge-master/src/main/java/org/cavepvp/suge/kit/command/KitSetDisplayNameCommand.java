package org.cavepvp.suge.kit.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.cavepvp.suge.kit.data.Kit;

public class KitSetDisplayNameCommand {

    @Command(names = {"kit setdisplayname"}, permission = "op")
    public static void execute(CommandSender sender, @Parameter(name = "kit") Kit kit, @Parameter(name = "displayName", wildcard = true)String displayName) {
        kit.setDisplayName(displayName);

        sender.sendMessage(ChatColor.GREEN + "Set display name of " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + " to " + ChatColor.WHITE + CC.translate(displayName) + ChatColor.GREEN + ".");
    }
}
