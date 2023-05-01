package net.frozenorb.foxtrot.team.dtr;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.event.TeamRegenerateEvent;

import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DTRHandler extends BukkitRunnable {

    private static double[] BASE_DTR_INCREMENT = { 1.5, .5, .45, .4, .36,
            .33, .3, .27, .24, .22, .21, .2, .19, .18, .175, .17, .168, .166,
            .164, .162, .16, .158, .156, .154, .152, .15, .148, .146, .144,
            .142, .142, .142, .142, .142, .142,
            .142, .142, .142, .142, .142 };
    private static double[] MAX_DTR = { 1.01, 2.02, 3.03, 4.4, 5.5, // 1 to 5
            5.5, 5.75, 5.75, 6.25, // 6 to 10
            6.25, 6.5, 6.5, 6.5, 6.5, // 11 to 15
            6.5, 6.5, 6.5, 6.5, 6.5, // 16 to 20

            6.25, 6.25, 6.25, 6.25, 6.5, // 21 to 25
            6.5, 6.5, 6.5, 6.75, 6.75, // 26 to 30
            7.0, 7.0, 7.0, 7.0, 7.0, // 31 to 35
            9, 9, 9, 9, 9 }; // Padding

    public static Set<ObjectId> wasOnCooldown = new HashSet<>();

    // * 4.5 is to 'speed up' DTR regen while keeping the ratios the same.
    // We're using this instead of changing the array incase we need to change this value
    // In the future.
    public static double getBaseDTRIncrement(int teamsize) {
        return (teamsize == 0 ? 0 : BASE_DTR_INCREMENT[teamsize - 1] * Foxtrot.getInstance().getMapHandler().getDtrIncrementMultiplier());
    }

    public static double getMaxDTR(int teamsize) {
        return (teamsize == 0 ? 100D : MAX_DTR[teamsize - 1]);
    }

    public static boolean isOnCooldown(Team team) {
        return (team.getDTRCooldown() > System.currentTimeMillis());
    }

    public static boolean isRegenerating(Team team) {
        return (!isOnCooldown(team) && team.getDTR() != team.getMaxDTR());
    }

    @Override
    public void run() {
        Map<Team, Integer> playerOnlineMap = new HashMap<>();

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (ModHandler.INSTANCE.isInVanish(player.getUniqueId())) {
                continue;
            }

            Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

            if (playerTeam != null && playerTeam.getOwner() != null) {
                playerOnlineMap.put(playerTeam, playerOnlineMap.getOrDefault(playerTeam, 0) + 1);
            }
        }

        playerOnlineMap.forEach((team, onlineCount) -> {
            try {
                // make sure (I guess?)
                if (isOnCooldown(team)) {
                    wasOnCooldown.add(team.getUniqueId());
                    return;
                }

                if (team.getDTR() == team.getMaxDTR()) {
                    return;
                }

                if (wasOnCooldown.remove(team.getUniqueId())) {
                    team.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Your team is now regenerating DTR!");
                }

                if (Foxtrot.getInstance().getServerHandler().isTeams()) {
                    team.setDTR(Math.min(team.getDTR() + team.getDTRIncrement(onlineCount), team.getMaxDTR()));
                } else {
                    boolean wasRaidable = team.isRaidable();
                    double oldDTR = team.getDTR();

                    team.setDTR(team.getMaxDTR());

                    final TeamRegenerateEvent teamRegenerateEvent = new TeamRegenerateEvent(team, oldDTR, team.getDTR(), wasRaidable);
                    Foxtrot.getInstance().getServer().getPluginManager().callEvent(teamRegenerateEvent);
                }
            } catch (Exception ex) {
                Foxtrot.getInstance().getLogger().warning("Error regenerating DTR for team " + team.getName() + ".");
                ex.printStackTrace();
            }
        });
    }

}
