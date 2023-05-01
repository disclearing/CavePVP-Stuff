package net.frozenorb.foxtrot.gameplay.loot.treasurecove.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.TreasureCoveHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TreasureRescanChestsCommand {

    @Command(names={"treasure rescanchests"}, permission="op")
    public static void treasureRescanChests(Player sender) {
        Foxtrot.getInstance().getTreasureCoveHandler().scanLoot();
        Foxtrot.getInstance().getTreasureCoveHandler().saveTreasureInfo();
        sender.sendMessage(TreasureCoveHandler.PREFIX + " " + ChatColor.YELLOW + "Rescanned all treasure chests.");
    }

}