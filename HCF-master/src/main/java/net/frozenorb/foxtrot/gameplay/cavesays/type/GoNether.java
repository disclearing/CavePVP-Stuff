package net.frozenorb.foxtrot.gameplay.cavesays.type;

import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class GoNether extends Task {
    @Override
    public String getTaskDisplayName() {
        return "Go through a Nether Portal";
    }

    @Override
    public String getTaskID() {
        return "GoNether";
    }

    @Override
    public int getPointsToWin() {
        return 1;
    }

    @EventHandler
    private void onWorldChange(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();

        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
            this.addProgress(player);
        }
    }
}