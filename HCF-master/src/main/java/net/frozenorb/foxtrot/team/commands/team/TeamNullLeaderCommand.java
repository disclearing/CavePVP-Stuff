package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamNullLeaderCommand {

    @Command(names={ "team nullleader", "t nullleader", "f nullleader", "faction nullleader", "fac nullleader" }, permission="op")
    public static void teamNullLeader(Player sender) {
        int nullLeaders = 0;

        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            if (team.getOwner() == null) {
                nullLeaders++;
                sender.sendMessage(ChatColor.RED + "- " + team.getName());
            }
        }

        if (nullLeaders == 0) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "No null teams found.");
        } else {
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + nullLeaders + " null teams found.");
        }
    }

}