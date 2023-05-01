package org.cavepvp.profiles.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.playerProfiles.PlayerProfileAPI;
import org.cavepvp.profiles.playerProfiles.impl.stats.StatisticServer;
import org.cavepvp.profiles.playerProfiles.impl.stats.StatisticType;

public class StatisticsAddCommand {
    @Command(names = {"addstatistic"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "target")Player target, @Parameter(name = "server")String server, @Parameter(name = "type")String type) {
        final StatisticServer statisticServer = StatisticServer.valueOf(server);
        final StatisticType statisticType = StatisticType.valueOf(type);

        if (statisticServer == null) {
            player.sendMessage(ChatColor.RED + "That statistic server doesn't exist!");
            return;
        }

        if (statisticType == null) {
            player.sendMessage(ChatColor.RED + "That statistic server doesn't exist!");
            return;
        }

        PlayerProfileAPI.addStatistic(target.getUniqueId(), statisticServer, statisticType, 1);
        player.sendMessage(ChatColor.translate("&6Added statistic &f" + statisticType.name() + " &6to " + "&f" + target.getName() + " &6on server &f" + server));
    }
}
