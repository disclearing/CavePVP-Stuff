package net.frozenorb.foxtrot.gameplay.events.citadel.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.citadel.CitadelHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CitadelRespawnChestsCommand {

    @Command(names={"citadel respawnchests"}, permission="op")
    public static void citadelRespawnChests(Player sender) {

        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This command may only be used on Kitmap!");
            return;
        }

        int i = Foxtrot.getInstance().getCitadelHandler().respawnCitadelChests();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Respawned all " + i + " Citadel chests.");
    }

}