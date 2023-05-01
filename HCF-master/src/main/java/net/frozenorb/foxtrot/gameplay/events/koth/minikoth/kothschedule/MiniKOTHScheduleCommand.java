package net.frozenorb.foxtrot.gameplay.events.koth.minikoth.kothschedule;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventScheduledTime;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class MiniKOTHScheduleCommand {

    public static final DateFormat KOTH_DATE_FORMAT = new SimpleDateFormat("EEE h:mm a");

    // Make this pretty.
    @Command(names={ "MiniKoth Schedule", "minikoth"}, permission="")
    public static void kothSchedule(Player sender) {
        int sent = 0;
        Date now = new Date();

        for (Map.Entry<EventScheduledTime, String> entry : Foxtrot.getInstance().getEventHandler().getMiniKOTHHandler().getEventSchedule().entrySet()) {
            Event resolved = Foxtrot.getInstance().getEventHandler().getEvent(entry.getValue());

            if (resolved == null || resolved.isHidden() || !entry.getKey().toDate().after(now) || resolved.getType() != EventType.KOTH) {
                continue;
            }

            if (sent > 5) {
                break;
            }

            sent++;
            sender.sendMessage(ChatColor.GOLD + "[Mini KOTH] " + ChatColor.YELLOW + entry.getValue() + ChatColor.GOLD + " can be captured at " + ChatColor.BLUE + KOTH_DATE_FORMAT.format(entry.getKey().toDate()) + ChatColor.GOLD + ".");
        }

        if (sent == 0) {
            sender.sendMessage(ChatColor.GOLD + "[Mini KOTH] " + ChatColor.RED + "KOTH Schedule: " + ChatColor.YELLOW + "Undefined");
        } else {
            sender.sendMessage(ChatColor.GOLD + "[Mini KOTH] " + ChatColor.YELLOW + "It is currently " + ChatColor.BLUE + KOTH_DATE_FORMAT.format(new Date()) + ChatColor.GOLD + ".");
        }
    }

}