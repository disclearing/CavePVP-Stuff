package org.cavepvp.suge.kit.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cavepvp.suge.kit.data.Kit;

public class KitSetIconCommand {

    @Command(names = {"kit seticon"}, permission = "op")
    public static void execute(Player sender, @Parameter(name = "kit") Kit kit) {
        if (sender.getItemInHand() == null) {
            sender.sendMessage(ChatColor.RED + "You must be holding an item!");
            return;
        }

        kit.setMaterial(sender.getItemInHand().getType());
        kit.setDamage(sender.getItemInHand().getDurability());

        sender.sendMessage(ChatColor.GREEN + "Set material of " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN +  ".");
    }
}
