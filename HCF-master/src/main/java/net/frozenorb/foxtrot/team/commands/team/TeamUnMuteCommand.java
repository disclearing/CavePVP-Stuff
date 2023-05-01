package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamUnMuteCommand {

    @Command(
            names={ "team lunmute", "t lunmute", "f lunmute", "faction lunmute", "fac lunmute",
            "team localunmute", "t localunmute", "f localunmute", "faction localunmute", "fac localunmute" },
            permission ="foxtrot.command.unmute"
    )
    public static void teamUnLocalMute(Player sender, @Parameter(name = "team") Team team) {
        TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                "shadowMute", "false"
        ));

        TeamMuteCommand.getTeamMutes().entrySet().removeIf(mute -> mute.getValue().equalsIgnoreCase(team.getName()));

        sender.sendMessage(ChatColor.GRAY + "Unmuted the team " + team.getName() + ChatColor.GRAY  + ".");
    }

    @Command(names={ "team unmute", "t unmute", "f unmute", "faction unmute", "fac unmute" }, permission="foxtrot.command.faction.mute")
    public static void teamUnMute(Player sender, @Parameter(name="team") final Team team, @Parameter(name="reason", wildcard=true) String reason) {
        for (Player player : team.getOnlineMembers()) {
            sender.chat("/unmute " + player.getName() + " " + reason);
        }
    }

}