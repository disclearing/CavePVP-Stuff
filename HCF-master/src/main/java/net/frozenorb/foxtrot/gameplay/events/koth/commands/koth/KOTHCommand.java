package net.frozenorb.foxtrot.gameplay.events.koth.commands.koth;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.UNDERLINE;
import static org.bukkit.ChatColor.YELLOW;

import java.util.Date;
import java.util.Map;

import org.bukkit.entity.Player;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventScheduledTime;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import cc.fyre.proton.command.Command;

public class KOTHCommand {

    // Make this pretty.
    @Command(names={ "Event", "Event Next", "Event Info", "Event", "koth", "koth next", "koth info" }, permission="")
    public static void koth(Player sender) {
        for (Event koth : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (!koth.isHidden() && koth.isActive()) {
                FancyMessage fm = new FancyMessage("[Events] ")
                        .color(GOLD)
                        .then(koth.getName())
                            .color(YELLOW) // koth name should be yellow
                            .style(UNDERLINE);
                            if (koth instanceof KOTH) {
                                fm.tooltip(YELLOW.toString() + ((KOTH) koth).getCapLocation().getBlockX() + ", " + ((KOTH) koth).getCapLocation().getBlockZ());
                            }
                            fm.color(YELLOW) // should color Event coords gray
                        .then(" can be contested now.")
                            .color(GOLD);
                        fm.send(sender);
                return;
            }
        }

        Date now = new Date();

        for (Map.Entry<EventScheduledTime, String> entry : Foxtrot.getInstance().getEventHandler().getEventSchedule().entrySet()) {
            if (entry.getKey().toDate().after(now)) {
                sender.sendMessage(GOLD + "[KingOfTheHill] " + YELLOW + entry.getValue() + GOLD + " can be captured at " + BLUE + KOTHScheduleCommand.KOTH_DATE_FORMAT.format(entry.getKey().toDate()) + GOLD + ".");
                sender.sendMessage(GOLD + "[KingOfTheHill] " + YELLOW + "It is currently " + BLUE + KOTHScheduleCommand.KOTH_DATE_FORMAT.format(now) + GOLD + ".");
                sender.sendMessage(YELLOW + "Type '/koth schedule' to see more upcoming Events.");
                return;
            }
        }

        sender.sendMessage(GOLD + "[KingOfTheHill] " + RED + "Next Event: " + YELLOW + "Undefined");
    }

}