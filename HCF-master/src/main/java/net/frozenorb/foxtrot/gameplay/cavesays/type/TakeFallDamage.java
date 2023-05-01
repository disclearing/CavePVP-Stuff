package net.frozenorb.foxtrot.gameplay.cavesays.type;

import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import net.frozenorb.foxtrot.team.event.TeamRaidableEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

public class TakeFallDamage extends Task {
    @Override
    public String getTaskDisplayName() {
        return "Take Fall Damage";
    }

    @Override
    public String getTaskID() {
        return "FallDamage";
    }

    @Override
    public int getPointsToWin() {
        return 1;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageEvent event) {
        final Player player = (Player) event.getEntity();

        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        this.addProgress(player);
    }
}
