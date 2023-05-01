package net.frozenorb.foxtrot.gameplay.events.koth.minikoth.kothschedule;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;

public class MiniKOTHRegenerateSchedule {

    @Command(names = {"MiniKOTHSchedule Regenerate", "MiniKOTHSchedule Regen"}, permission = "foxtrot.koth.admin", async = true)
    public static void kothScheduleEnable(CommandSender sender) {
        File kothSchedule = new File(Foxtrot.getInstance().getDataFolder(), "minieventSchedule.json");

        if (kothSchedule.delete()) {
            Foxtrot.getInstance().getEventHandler().getMiniKOTHHandler().loadSchedules();

            sender.sendMessage(ChatColor.YELLOW + "The event schedule has been regenerated.");
        } else {
            sender.sendMessage(ChatColor.RED + "Couldn't delete event schedule file.");
        }
    }
}
