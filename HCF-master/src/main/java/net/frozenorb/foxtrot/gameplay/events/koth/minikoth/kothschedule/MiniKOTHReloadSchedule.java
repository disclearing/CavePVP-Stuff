package net.frozenorb.foxtrot.gameplay.events.koth.minikoth.kothschedule;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MiniKOTHReloadSchedule {

    @Command(names={ "KOTHSchedule Reload" }, permission="foxtrot.koth.admin")
    public static void kothScheduleReload(Player sender) {
        Foxtrot.getInstance().getEventHandler().getMiniKOTHHandler().loadSchedules();
        sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "Reloaded the KOTH schedule.");
    }

}