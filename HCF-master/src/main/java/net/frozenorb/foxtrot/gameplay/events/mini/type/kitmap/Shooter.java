package net.frozenorb.foxtrot.gameplay.events.mini.type.kitmap;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.events.EventCapturedEvent;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;
import java.util.List;

public class Shooter extends MiniEvent {
    @Override
    public String getObjective() {
        return "Shooter";
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("Archer Tag the most people", "within the next hour!");
    }

    @Override
    public String getEventID() {
        return "Shooter";
    }

    @Override
    public int getSeconds() {
        return 60*60;
    }

    public static void archerTag(Player player) {
        if (Foxtrot.getInstance().getMiniEventsHandler().getActiveEvent() == null) {
            return;
        }

        if (!Foxtrot.getInstance().getMiniEventsHandler().getActiveEvent().getEventID().equalsIgnoreCase("Shooter")) {
            return;
        }

        Foxtrot.getInstance().getMiniEventsHandler().getActiveEvent().addProgress(player, false);
    }
}