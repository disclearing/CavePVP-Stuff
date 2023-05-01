package net.frozenorb.foxtrot.gameplay.cavesays.type;

import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillCreepers extends Task {
    @Override
    public String getTaskDisplayName() {
        return "Kill 25x Creepers";
    }

    @Override
    public String getTaskID() {
        return "KillCreepers";
    }

    @Override
    public int getPointsToWin() {
        return 25;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDeath(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.CREEPER) {
            return;
        }

        final Player killer = event.getEntity().getKiller();

        if (killer == null) {
            return;
        }

        this.addProgress(killer);
    }
}
