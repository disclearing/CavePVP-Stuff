package net.frozenorb.foxtrot.gameplay.loot.treasurecove.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TreasureRegenCentralChest {

    @Command(names={"treasure generatecentralchest"}, permission="op")
    public static void execute(Player sender) {
        if (!Foxtrot.getInstance().getTreasureCoveHandler().generateCentralChest()) {
            sender.sendMessage(ChatColor.RED + "Failed to regenerate central chest!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Successfully regenerated central chest!");
        }
    }
}