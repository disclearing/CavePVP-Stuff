package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.FoxConstants;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TellLocationCommand {

    @Command(names = {"telllocation", "tl"}, permission = "")
    public static void execute(Player sender, @Parameter(name="type", defaultValue="self") String place) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        Location location = sender.getLocation();

        if ((place.equalsIgnoreCase("focused") || place.equalsIgnoreCase("focus")) && team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
            location = team.getFocusedTeam().getHQ();
        } else if ((place.equalsIgnoreCase("home") || place.equalsIgnoreCase("hq") || place.equalsIgnoreCase("h")) && team.getHQ() != null) {
            location = team.getHQ();
        }

        team.sendMessage(FoxConstants.teamChatFormat(sender, String.format("[%.1f, %.1f, %.1f]", location.getX(), location.getY(), location.getZ())));
    }

    @Command(names = {"tellhomelocation", "thl", "tlhome", "tlh", "tch", "thc", "thq", "tlhq"}, permission = "")
    public static void tellHomeLocation(Player sender, @Parameter(name = "team", defaultValue = "self")Team chosenTeam) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (chosenTeam.getHQ() == null) {
            sender.sendMessage(ChatColor.RED + "That faction doesn't have a home set!");
            return;
        }

        final Location location = chosenTeam.getHQ();
        team.sendMessage(FoxConstants.teamChatFormat(sender, String.format("[%.1f, %.1f, %.1f]", location.getX(), location.getY(), location.getZ())));
    }
}
