package net.frozenorb.foxtrot.gameplay.cavesays.type;

import cc.fyre.piston.Piston;
import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class KillPlayer extends Task {
    @Override
    public String getTaskDisplayName() {
        return "Kill a Player";
    }

    @Override
    public String getTaskID() {
        return "KillPlayer";
    }

    @Override
    public int getPointsToWin() {
        return 1;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer = player.getKiller();

        if (killer == null) {
            return;
        }

        this.addProgress(killer);
    }
}