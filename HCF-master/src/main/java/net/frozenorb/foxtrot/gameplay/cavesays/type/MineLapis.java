package net.frozenorb.foxtrot.gameplay.cavesays.type;

import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public class MineLapis extends Task {
    @Override
    public String getTaskDisplayName() {
        return "Mine 16x Lapis Ore";
    }

    @Override
    public String getTaskID() {
        return "MineLapis";
    }

    @Override
    public int getPointsToWin() {
        return 16;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onBreak(BlockBreakEvent event) {
        if (event.isCancelled() || event.getBlock().getType() != Material.LAPIS_ORE) {
            return;
        }

        this.addProgress(event.getPlayer());
    }
}
