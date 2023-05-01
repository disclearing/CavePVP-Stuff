package net.frozenorb.foxtrot.gameplay.cavesays.type;

import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class GoEnd extends Task {
    @Override
    public String getTaskDisplayName() {
        return "Go through an End Portal";
    }

    @Override
    public String getTaskID() {
        return "GoEnd";
    }

    @Override
    public int getPointsToWin() {
        return 1;
    }

    @EventHandler
    private void onWorldChange(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();

        if (player.getWorld().getEnvironment() == World.Environment.THE_END) {
            this.addProgress(player);
        }
    }
}