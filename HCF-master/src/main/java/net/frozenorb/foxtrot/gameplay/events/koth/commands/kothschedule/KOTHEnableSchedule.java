package net.frozenorb.foxtrot.gameplay.events.koth.commands.kothschedule;

import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KOTHEnableSchedule {

    @Command(names = "KOTHSchedule Enable", permission = "foxtrot.koth.admin")
    public static void kothScheduleEnable(CommandSender sender) {
        Foxtrot.getInstance().getEventHandler().setScheduleEnabled(true);

        sender.sendMessage(ChatColor.YELLOW + "The KOTH schedule has been " + ChatColor.GREEN + "enabled" + ChatColor.YELLOW + ".");
    }

}
