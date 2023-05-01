package net.frozenorb.foxtrot.gameplay.events.koth.minikoth.kothschedule;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MiniKOTHDisableSchedule {

    @Command(names = "KOTHSchedule Disable", permission = "foxtrot.koth.admin")
    public static void kothScheduleDisable(CommandSender sender) {
        Foxtrot.getInstance().getEventHandler().getMiniKOTHHandler().setScheduleEnabled(false);

        sender.sendMessage(ChatColor.YELLOW + "The KOTH schedule has been " + ChatColor.RED + "disabled" + ChatColor.YELLOW + ".");
    }

}
