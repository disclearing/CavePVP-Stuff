package cc.fyre.proton.autoreboot.command;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RebootScheduleCommand {

    @Command(
            names = {"reboot","reboot schedule"},
            permission = "proton.command.reboot.schedule"
    )
    public static void execute(CommandSender sender,@Parameter(name = "time")long time) {
        Proton.getInstance().getAutoRebootHandler().rebootServer(time);
        sender.sendMessage(ChatColor.GOLD + "Scheduled a reboot in " + TimeUtils.formatIntoDetailedString((int)(time / 1000)));

        Proton.getInstance().getServer().broadcastMessage("");
        Proton.getInstance().getServer().broadcastMessage(ChatColor.DARK_RED + "[Reboot] " + ChatColor.RED + "The server will be restarting in " + ChatColor.WHITE + TimeUtils.formatIntoDetailedString((int)time/1000) + ".");
        Proton.getInstance().getServer().broadcastMessage("");
    }

}
