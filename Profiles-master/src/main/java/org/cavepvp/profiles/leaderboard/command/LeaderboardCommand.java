package org.cavepvp.profiles.leaderboard.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.util.UUIDUtils;
import cc.fyre.universe.UniverseAPI;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.Profiles;

import java.util.Map;
import java.util.UUID;

public class LeaderboardCommand {

    @Command(names = {"reputation leaderboard", "rep leaderboard", "leaderboard reputation", "lb reputation", "leaderboards reputation", "lb rp", "leaderboard rp", "lb rep", "leaderboard rep", "leaderboards rep", "leaderboards rp"}, permission = "")
    public static void execute(Player player) {
        if (UniverseAPI.getServerName().contains("AU")) {
            player.sendMessage(ChatColor.RED + "Disabled on this server!");
            return;
        }

        final Profiles instance = Profiles.getInstance();

        player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 53));
        player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Reputation Leaderboards");
        player.sendMessage("");

        int index = 0;

        for (Map.Entry<UUID, Double> entry : instance.getLeaderboardHandler().getSortedMap().entrySet()) {

            index++;

            if (11 <= index) {
                break;
            }

            double finalDouble = entry.getValue();
            finalDouble *= 100;
            finalDouble = Math.round(finalDouble);
            finalDouble /= 100;

            player.sendMessage((index == 1 ? ChatColor.RED + "1. " : ChatColor.GRAY.toString() + index + ". ") + ChatColor.RED + UUIDUtils.name(entry.getKey()) + ": " + ChatColor.WHITE + finalDouble + " RP");
        }

        player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 53));
    }
}
