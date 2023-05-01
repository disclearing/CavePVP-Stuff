package net.frozenorb.foxtrot.gameplay.events.mini.type;

import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;
import java.util.List;

public class MiningFrenzy extends MiniEvent {
    @Override
    public String getObjective() {
        return "Mining Frenzy";
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("Mine the most ores", "within the next 30 minutes!");
    }

    @Override
    public String getEventID() {
        return "MiningFrenzy";
    }

    @Override
    public int getSeconds() {
        return 60*30;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onBreak(BlockBreakEvent event) {
        if (event.isCancelled() || !event.getBlock().getType().name().contains("ORE")) {
            return;
        }

        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("Deathban")) {
            return;
        }

        this.addProgress(event.getPlayer(), true);
    }
}
