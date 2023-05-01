package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamInvitesCommand {

    @Command(names={ "team invites", "t invites", "f invites", "faction invites", "fac invites" }, permission="")
    public static void teamInvites(Player sender) {
        StringBuilder yourInvites = new StringBuilder();

        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            if (team.getInvitations().contains(sender.getUniqueId())) {
                yourInvites.append(ChatColor.GRAY).append(team.getName()).append(ChatColor.YELLOW).append(", ");
            }
        }

        if (yourInvites.length() > 2) {
            yourInvites.setLength(yourInvites.length() - 2);
        } else {
            yourInvites.append(ChatColor.GRAY).append("No pending invites.");
        }

        sender.sendMessage(ChatColor.YELLOW + "Your Invites: " + yourInvites.toString());

        Team current = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (current != null) {
            StringBuilder invitedToYourTeam = new StringBuilder();

            for (UUID invited : current.getInvitations()) {
                invitedToYourTeam.append(ChatColor.GRAY).append(UUIDUtils.name(invited)).append(ChatColor.YELLOW).append(", ");
            }

            if (invitedToYourTeam.length() > 2) {
                invitedToYourTeam.setLength(invitedToYourTeam.length() - 2);
            } else {
                invitedToYourTeam.append(ChatColor.GRAY).append("No pending invites.");
            }

            sender.sendMessage(ChatColor.YELLOW + "Invited to your Team: " + invitedToYourTeam.toString());
        }
    }

}