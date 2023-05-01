package net.frozenorb.foxtrot.gameplay.events.koth.commands.kothschedule;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.Command;

public class KOTHDisableSchedule {

    @Command(names = "KOTHSchedule Disable", permission = "foxtrot.koth.admin")
    public static void kothScheduleDisable(CommandSender sender) {
        Foxtrot.getInstance().getEventHandler().setScheduleEnabled(false);

        sender.sendMessage(ChatColor.YELLOW + "The KOTH schedule has been " + ChatColor.RED + "disabled" + ChatColor.YELLOW + ".");
    }

}
