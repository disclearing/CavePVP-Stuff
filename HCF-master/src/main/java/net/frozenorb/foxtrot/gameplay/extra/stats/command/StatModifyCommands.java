package net.frozenorb.foxtrot.gameplay.extra.stats.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsEntry;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatModifyCommands {

    @Command(names = "sm setkills", permission = "foxtrot.command.stats.setkills")
    public static void setKills(CommandSender sender, @Parameter(name = "target")UUID target, @Parameter(name = "kills") int kills) {
        StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(target);
        stats.setKills(kills);

        Foxtrot.getInstance().getKillsMap().setKills(target, kills);

        sender.sendMessage(ChatColor.GREEN + "You've set " + UUIDUtils.name(target) + " kills to: " + kills);
    }

    @Command(names = "sm setkillstreak", permission = "foxtrot.command.stats.setkillstreak")
    public static void setkillstreak(CommandSender sender, @Parameter(name = "target")UUID target, @Parameter(name = "kills") int kills) {
        StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(target);
        stats.setKillstreak(kills);

        Foxtrot.getInstance().getKillstreakMap().setKillstreak(target, kills);

        sender.sendMessage(ChatColor.GREEN + "You've set " + UUIDUtils.name(target) + " kill streak to: " + kills);
    }

    @Command(names = "sm setdeaths", permission = "foxtrot.command.stats.setdeaths")
    public static void setDeaths(CommandSender sender, @Parameter(name = "target")UUID target, @Parameter(name = "deaths") int deaths) {
        StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(target);
        stats.setDeaths(deaths);

        Foxtrot.getInstance().getDeathsMap().setDeaths(target, deaths);

        sender.sendMessage(ChatColor.GREEN + "You've set " + UUIDUtils.name(target) + " deaths to: " + deaths);
    }

    @Command(names = "sm setteamkills", permission = "foxtrot.command.stats.setteamkills")
    public static void setTeamKills(Player player, @Parameter(name = "kills") int kills) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team != null) {
            team.setKills(kills);
            player.sendMessage(ChatColor.GREEN + "You've set your team's kills to: " + kills);
        }
    }

    @Command(names = "sm setteamdeaths", permission = "foxtrot.command.stats.setteamdeaths")
    public static void setTeamDeaths(Player player, @Parameter(name = "deaths") int deaths) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team != null) {
            team.setDeaths(deaths);
            player.sendMessage(ChatColor.GREEN + "You've set your team's deaths to: " + deaths);
        }
    }

    @Command(names = "sm setkothcaptures", permission = "foxtrot.command.stats.setkothcaptures")
    public static void setKothCaptures(Player player, @Parameter(name = "captures") int captures) {
        StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(player);
        stats.setKothCaptures(captures);
        player.sendMessage(ChatColor.GREEN + "You've set your own KoTH captures to: " + captures);
    }
}
