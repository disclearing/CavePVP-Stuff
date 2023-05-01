package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import com.mysql.jdbc.TimeUtil;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamMuteCommand {

    @Command(names={ "team mute", "t mute", "f mute", "faction mute", "fac mute" }, permission="foxtrot.command.faction.mute")
    public static void teamMute(Player sender, @Parameter(name="team") final Team team, @Parameter(name="reason", wildcard=true) String reason) {
        for (Player player : team.getOnlineMembers()) {
            sender.chat("/mute " + player.getName() + " " + reason + " -p");
        }
    }

    @Getter
    private static Map<UUID, String> teamMutes = new HashMap<>();

    @Command(names={ "team lmute", "t lmute", "f lmute", "faction lmute", "fac lmute" , "team localmute", "t localmute", "f localmute", "faction localmute", "fac localmute" }, permission="foxtrot.mutefaction")
    public static void teamLocalMute(Player sender, @Parameter(name="team") final Team team, @Parameter(name="time")long time, @Parameter(name="reason", wildcard=true) String reason) {

        for (UUID player : team.getMembers()) {
            teamMutes.put(player, team.getName());

            Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player);

            if (bukkitPlayer != null) {
                bukkitPlayer.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your team has been muted for " + TimeUtils.formatIntoMMSS((int) (time/1000)) + " for " + reason + ".");
            }
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_CREATED, ImmutableMap.of(
                "shadowMute", "false",
                "mutedById", sender.getUniqueId(),
                "mutedByName", sender.getName(),
                "duration", time
        ));

        new BukkitRunnable() {

            public void run() {
                TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                        "shadowMute", "false"
                ));

                teamMutes.entrySet().removeIf(mute -> mute.getValue().equalsIgnoreCase(team.getName()));
            }

        }.runTaskLater(Foxtrot.getInstance(), (time/1000L) * 20L);

        sender.sendMessage(ChatColor.YELLOW + "Muted the team " + team.getName() + ChatColor.GRAY + " for " + TimeUtils.formatIntoMMSS((int) (time/1000)) + " for " + reason + ".");
    }

}