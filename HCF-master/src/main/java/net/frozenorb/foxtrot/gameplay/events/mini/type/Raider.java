package net.frozenorb.foxtrot.gameplay.events.mini.type;

import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import net.frozenorb.foxtrot.team.event.TeamRaidableEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;
import java.util.List;

public class Raider extends MiniEvent {
    @Override
    public String getObjective() {
        return "Raider";
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("Make the most amount of", "factions raiddable in the hour!");
    }

    @Override
    public String getEventID() {
        return "Raider";
    }

    @Override
    public int getSeconds() {
        return 60*60;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onRaidable(TeamRaidableEvent event) {
        this.addProgress(event.getKiller(), true);
    }
}
