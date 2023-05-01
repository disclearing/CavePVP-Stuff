package net.frozenorb.foxtrot.gameplay.events.koth.minikoth.kothschedule;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MiniKOTHEnableSchedule {

    @Command(names = "KOTHSchedule Enable", permission = "foxtrot.koth.admin")
    public static void kothScheduleEnable(CommandSender sender) {
        Foxtrot.getInstance().getEventHandler().getMiniKOTHHandler().setScheduleEnabled(true);

        sender.sendMessage(ChatColor.YELLOW + "The KOTH schedule has been " + ChatColor.GREEN + "enabled" + ChatColor.YELLOW + ".");
    }

}
