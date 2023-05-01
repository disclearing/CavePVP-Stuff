package net.frozenorb.foxtrot.gameplay.cavesays.type;

import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public class MineGlowstone extends Task {
    @Override
    public String getTaskDisplayName() {
        return "Mine 16x Glowstone at Glowstone Mountain";
    }

    @Override
    public String getTaskID() {
        return "Glowstone";
    }

    @Override
    public int getPointsToWin() {
        return 16;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBreak(BlockBreakEvent event) {
        if (event.isCancelled() || event.getBlock().getType() != Material.GLOWSTONE) {
            return;
        }

        final Player player = event.getPlayer();

        if (!LandBoard.getInstance().getTeam(player.getLocation()).getName().equalsIgnoreCase("Glowstone")) {
            return;
        }

        this.addProgress(player);
    }
}
