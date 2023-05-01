package net.frozenorb.foxtrot.gameplay.loot.treasurecove.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.TreasureCoveHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TreasureRespawnChestsCommand {

    @Command(names={"treasure respawnchests"}, permission="op")
    public static void treasureRespawnChests(Player sender) {
        sender.sendMessage(TreasureCoveHandler.PREFIX + " " + ChatColor.YELLOW + "Respawned " + Foxtrot.getInstance().getTreasureCoveHandler().respawnTreasureChests() + " treasure chests.");
    }

}