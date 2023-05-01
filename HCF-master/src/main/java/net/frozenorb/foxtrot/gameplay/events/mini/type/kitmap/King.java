package net.frozenorb.foxtrot.gameplay.events.mini.type.kitmap;

import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.events.EventCapturedEvent;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Arrays;
import java.util.List;

public class King extends MiniEvent {
    @Override
    public String getObjective() {
        return "King";
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("Capture the most KOTHs", "within the next hour!");
    }

    @Override
    public String getEventID() {
        return "King";
    }

    @Override
    public int getSeconds() {
        return 60*120;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDeath(EventCapturedEvent event) {
        if (event.getEvent().isHidden()) {
            return;
        }

        if (!event.getEvent().getType().equals(EventType.KOTH)) {
            return;
        }

        this.addProgress(event.getPlayer(), true);
    }
}