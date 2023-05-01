package net.frozenorb.foxtrot.team.commands.team;

import net.minecraft.util.com.google.common.collect.ImmutableMap;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TeamUninviteCommand {

    @Command(names={ "team uninvite", "t uninvite", "f uninvite", "faction uninvite", "fac uninvite", "team revoke", "t revoke", "f revoke", "faction revoke", "fac revoke" }, permission="")
    public static void teamUninvite(final Player sender, @Parameter(name="all | player") final String allPlayer) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            if (allPlayer.equalsIgnoreCase("all")) {
                team.getInvitations().clear();
                sender.sendMessage(ChatColor.GRAY + "You have cleared all pending invitations.");
            } else {
                new BukkitRunnable() {

                    public void run() {
                        final UUID nameUUID = UUIDUtils.uuid(allPlayer);

                        new BukkitRunnable() {

                            public void run() {
                                if (team.getInvitations().remove(nameUUID)) {
                                    TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_INVITE_REVOKED, ImmutableMap.of(
                                            "playerId", allPlayer,
                                            "uninvitedById", sender.getUniqueId(),
                                            "uninvitedByName", sender.getName()
                                    ));

                                    team.getInvitations().remove(nameUUID);
                                    team.flagForSave();
                                    sender.sendMessage(ChatColor.GREEN + "Cancelled pending invitation for " + allPlayer + "!");
                                } else {
                                    sender.sendMessage(ChatColor.RED + "No pending invitation for '" + allPlayer + "'!");
                                }
                            }

                        }.runTask(Foxtrot.getInstance());
                    }

                }.runTaskAsynchronously(Foxtrot.getInstance());
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only faction captains can do this.");
        }
    }

}