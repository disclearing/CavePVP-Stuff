package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.TeamHandler;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FreezeRostersCommand {

    @Command(names={ "freezerosters" }, permission="op")
    public static void freezeRosters(Player sender) {
        TeamHandler teamHandler = Foxtrot.getInstance().getTeamHandler();
        teamHandler.setRostersLocked(!teamHandler.isRostersLocked());

        sender.sendMessage(ChatColor.YELLOW + "Team rosters are now " + ChatColor.LIGHT_PURPLE + (teamHandler.isRostersLocked() ? "locked" : "unlocked") + ChatColor.YELLOW + ".");
    }

}