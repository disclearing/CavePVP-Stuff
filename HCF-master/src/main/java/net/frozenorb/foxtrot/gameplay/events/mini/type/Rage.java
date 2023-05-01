package net.frozenorb.foxtrot.gameplay.events.mini.type;

import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Arrays;
import java.util.List;

public class Rage extends MiniEvent {
    @Override
    public String getObjective() {
        return "Rage";
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("Kill the most players with", "a reduced partner item cooldown!");
    }

    @Override
    public String getEventID() {
        return "Rage";
    }

    @Override
    public int getSeconds() {
        return 60*60;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDeath(PlayerDeathEvent event) {
        final Player killer = event.getEntity().getKiller();

        if (killer == null) {
            return;
        }

        if (killer.getWorld().getName().equalsIgnoreCase("Deathban")) {
            return;
        }

        this.addProgress(killer, true);
    }
}