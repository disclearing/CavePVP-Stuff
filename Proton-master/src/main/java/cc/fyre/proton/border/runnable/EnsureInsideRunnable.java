package cc.fyre.proton.border.runnable;

import cc.fyre.proton.Proton;
import cc.fyre.proton.border.Border;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitRunnable;


public class EnsureInsideRunnable extends BukkitRunnable {

    public void run() {

        for (Player loopPlayer : Proton.getInstance().getServer().getOnlinePlayers()) {

            final Border border = Proton.getInstance().getBorderHandler().getBorderForWorld(loopPlayer.getWorld());

            if (border != null && loopPlayer.getWorld() == border.getOrigin().getWorld() && this.shouldEnsure(loopPlayer) && !border.contains(loopPlayer.getLocation().getBlockX(),loopPlayer.getLocation().getBlockZ())) {

                final Location location = border.correctLocation(loopPlayer.getLocation());

                if (loopPlayer.getVehicle() != null) {

                    final Entity vehicle = loopPlayer.getVehicle();

                    loopPlayer.leaveVehicle();

                    vehicle.teleport(location);
                    vehicle.setPassenger(loopPlayer);
                }

                loopPlayer.teleport(location);
            }
        }

    }

    private boolean isSafe(Location location) {
        return location.getBlock().getRelative(BlockFace.DOWN).getType().isSolid();
    }

    private boolean shouldEnsure(Player player) {

        if (!player.hasMetadata("Border-Pushback")) {
            return true;
        }

        try {
            final long pushed = (player.getMetadata("Border-Pushback").get(0)).asLong();
            final long delta = System.currentTimeMillis() - pushed;

            return delta >= 500L;
        } catch (Exception ex) {
            return true;
        }

    }
}
