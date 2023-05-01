package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamAnnouncementCommand {

    // anouncement is here for those who can't spell.
    @Command(names={ "team announcement", "t announcement", "f announcement", "faction announcement", "fac announcement", "team anouncement", "t anouncement", "f anouncement", "faction anouncement", "fac anouncement" }, permission="")
    public static void teamAnnouncement(Player sender, @Parameter(name="new announcement", wildcard=true) String newAnnouncement) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only faction captains can do this.");
            return;
        }

        if (newAnnouncement.equalsIgnoreCase("clear")) {
            team.setAnnouncement(null);
            sender.sendMessage(ChatColor.YELLOW + "Faction announcement cleared.");
            return;
        }

        team.setAnnouncement(newAnnouncement);
        team.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW  + " changed the faction announcement to " + ChatColor.LIGHT_PURPLE + newAnnouncement);
    }

}