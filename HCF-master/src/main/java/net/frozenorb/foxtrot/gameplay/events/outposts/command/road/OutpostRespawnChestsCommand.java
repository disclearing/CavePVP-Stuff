package net.frozenorb.foxtrot.gameplay.events.outposts.command.road;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OutpostRespawnChestsCommand {

    @Command(names={"roadoutpost respawnchests"}, permission="op")
    public static void citadelRespawnChests(Player sender) {
        Foxtrot.getInstance().getOutpostHandler().findRoadOutpost().respawnOutpostChests();
        sender.sendMessage(ChatColor.GREEN + "Respawned all " + Foxtrot.getInstance().getOutpostHandler().findRoadOutpost().respawnOutpostChests() + " Road Outpost chests.");
    }

}