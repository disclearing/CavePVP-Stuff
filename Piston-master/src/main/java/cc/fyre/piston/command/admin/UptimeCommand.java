package cc.fyre.piston.command.admin;

import cc.fyre.neutron.util.FormatUtil;
import cc.fyre.piston.Piston;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

public class UptimeCommand {

    @Command(
            names = {"uptime"},
            permission = "command.uptime"
    )
    public static void execute(CommandSender sender) {

        final long uptime = System.currentTimeMillis() - Piston.getInstance().getStartupTime();

        ChatColor color = ChatColor.GREEN;

        if (uptime > TimeUnit.HOURS.toMillis(16L)) {
            color = ChatColor.YELLOW;
        } else if (uptime > TimeUnit.HOURS.toMillis(24L)) {
            color = ChatColor.RED;
        } else if (uptime > TimeUnit.HOURS.toMillis(48L)) {
            color = ChatColor.DARK_RED;
        }

        sender.sendMessage(ChatColor.GOLD + "The server has been running for " + color + TimeUtils.formatIntoDetailedString((int)(uptime/1000)) + ChatColor.GOLD + ".");
    }

}

