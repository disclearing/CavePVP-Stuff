package net.frozenorb.foxtrot.gameplay.events.outposts.command.road;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OutpostRescanChestsCommand {

    @Command(names={"roadoutpost rescanchests"}, permission="op")
    public static void citadelRescanChests(Player sender) {
        sender.sendMessage(ChatColor.YELLOW + "Rescanned " + Foxtrot.getInstance().getOutpostHandler().findRoadOutpost().scanLoot() + " Outpost chests.");
        Foxtrot.getInstance().getOutpostHandler().findRoadOutpost().saveOutpostLoot();
    }

}