package net.frozenorb.foxtrot.gameplay.extra.stats.command;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsEntry;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class StatsTopCommand {

    @Command(names = {"statstop", "leaderboards", "lb"}, permission = "")
    public static void statstop(CommandSender sender, @Parameter(name = "objective", defaultValue = "kills") StatsObjective objective) {
        sender.sendMessage(Team.DARK_GRAY_LINE);
        sender.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + objective.getName() + " Leaderboard");
        sender.sendMessage("");

        int index = 0;

        for (Map.Entry<StatsEntry, String> entry : Foxtrot.getInstance().getMapHandler().getStatsHandler().getLeaderboards(objective, 10).entrySet()) {
            index++;
            sender.sendMessage((index == 1 ? ChatColor.RED + "1. " : ChatColor.GRAY.toString() + index + ". ") + ChatColor.RED + Proton.getInstance().getUuidCache().name(entry.getKey().getOwner()) + ": " + ChatColor.WHITE + entry.getValue());
        }

        sender.sendMessage(Team.DARK_GRAY_LINE);
    }

    @Getter
    public enum StatsObjective {

        KILLS("Kills", "k"),
        DEATHS("Deaths", "d"),
        KD("KD", "kdr"),
        HIGHEST_KILLSTREAK("Highest Killstreak", "killstreak", "highestkillstreak", "ks", "highestks", "hks"),
        CAVE_SAYS_COMPLETED("Cave Says Completed", "cavesays", "tasks", "cavesayscompleted", "csc"),
        KOTH_CAPTURES("KoTH Captures", "captures", "koth", "kothcaptures");

        private final String name;
        private final String[] aliases;

        StatsObjective(String name, String... aliases) {
            this.name = name;
            this.aliases = aliases;
        }
    }
}
