package net.frozenorb.foxtrot.gameplay.events.koth.commands.kothschedule;

import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHReloadSchedule {

    @Command(names={ "KOTHSchedule Reload" }, permission="foxtrot.koth.admin")
    public static void kothScheduleReload(Player sender) {
        Foxtrot.getInstance().getEventHandler().loadSchedules();
        sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "Reloaded the KOTH schedule.");
    }

}