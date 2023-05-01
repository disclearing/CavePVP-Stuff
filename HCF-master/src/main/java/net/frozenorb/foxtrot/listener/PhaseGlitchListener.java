package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PhaseGlitchListener
        implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerShovel(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Location location = player.getLocation();
        double yDiff;

        if (event.isCancelled() && block.getType().isSolid() && (yDiff = location.getY() - block.getY()) >= 0 && yDiff < 0.5) {

            int blockX = block.getX();
            int blockZ = block.getZ();

            if (blockX != location.getBlockX() || blockZ != location.getBlockZ()) {
                new BukkitRunnable() {
                    public void run() {
                        if (player.isOnline() && blockX == player.getLocation().getBlockX() && blockZ == player.getLocation().getBlockZ()) {
                            player.teleport(location.clone());
                        } else if (!player.isOnline() || player.getLocation().getBlockX() != location.getBlockX() || player.getLocation().getBlockZ() != location.getBlockZ() || player.getLocation().getBlockY() != location.getBlockY()) {
                            cancel();
                        }
                    }
                }.runTaskTimer(Foxtrot.getInstance(), 1, 1);
                if (blockX == player.getLocation().getBlockX() && blockZ == player.getLocation().getBlockZ()) {
                    player.teleport(location.clone());
                }
            }
        }
    }
}


