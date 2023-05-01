package org.cavepvp.suge.kit.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.cavepvp.suge.kit.data.Kit;

public class KitSetSlotCommand {

    @Command(names = {"kit setslot"}, permission = "op")
    public static void execute(CommandSender sender, @Parameter(name = "kit") Kit kit, @Parameter(name = "slot")int slot) {
        kit.setSlot(slot);

        sender.sendMessage(ChatColor.GREEN + "Set slot of " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + " to " + ChatColor.WHITE + slot + ChatColor.GREEN + ".");
    }
}
