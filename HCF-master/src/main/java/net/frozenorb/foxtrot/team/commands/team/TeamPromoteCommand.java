package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamPromoteCommand {

    @Command(names={ "team promote", "t promote", "f promote", "faction promote", "fac promote", "team captain" }, permission="")
    public static void teamPromote(Player sender, @Parameter(name="player") UUID player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only faction co-leaders (and above) can do this.");
            return;
        }

        if (!team.isMember(player)) {
            sender.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " is not on your faction.");
            return;
        }

        if (team.isOwner(player)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is already a leader.");
        } else if (team.isCoLeader(player)) {
            if (team.isOwner(sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is already a co-leader! To make them a leader, use /f leader");
            } else {
                sender.sendMessage(ChatColor.RED + "Only the faction leader can promote new leaders.");
            }
        } else if (team.isCaptain(player)) {
            if (team.isOwner(sender.getUniqueId())) {
                team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " has been promoted to Co-Leader!");
                team.addCoLeader(player);
                team.removeCaptain(player);
            } else {
                sender.sendMessage(ChatColor.RED + "Only the faction leader can promote new Co-Leaders.");
            }
        } else {
            team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " has been promoted to Captain!");
            team.addCaptain(player);
        }
    }

}