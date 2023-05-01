package cc.fyre.proton.border.runnable;

import cc.fyre.proton.Proton;
import cc.fyre.proton.cuboid.Cuboid;
import cc.fyre.proton.border.Border;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleRunnable extends BukkitRunnable {

    private static final int RADIUS = 15;

    public void run() {

        for (Player loopPlayer : Proton.getInstance().getServer().getOnlinePlayers()) {

            final Border border = Proton.getInstance().getBorderHandler().getBorderForWorld(loopPlayer.getWorld());

            if (border == null || border.getParticle() == null || !this.shouldCheck(loopPlayer,border)) {
                continue;
            }

            for(int x = loopPlayer.getLocation().getBlockX() - 15; x < loopPlayer.getLocation().getBlockX() + 15; ++x) {

                for(int y = loopPlayer.getLocation().getBlockY() - 5; y < loopPlayer.getLocation().getBlockY() + 5; ++y) {

                    for(int z = loopPlayer.getLocation().getBlockZ() - 15; z < loopPlayer.getLocation().getBlockZ() + 15; ++z) {

                        final Cuboid cuboid = border.getPhysicalBounds();

                        float finalX = (float)x;
                        float finalZ = (float)z;

                        if (x < 0) {
                            ++finalX;
                        }

                        if (z < 0) {
                            ++finalZ;
                        }

                        Location location = null;

                        if ((x > 0 && x == cuboid.getUpperX() || x < 0 && x == cuboid.getLowerX()) && (z > 0 && z <= cuboid.getUpperZ() || z < 0 && z >= cuboid.getLowerZ())) {
                            location = new Location(loopPlayer.getWorld(), (double)(finalX + (x < 0 ? 0.1F : -0.1F)), (double)((float)y + 0.5F), (double)finalZ + 0.5D);
                        }

                        if ((z > 0 && z == cuboid.getUpperZ() || z < 0 && z == cuboid.getLowerZ()) && (x > 0 && x <= cuboid.getUpperX() || x < 0 && x >= cuboid.getLowerX())) {
                            location = new Location(loopPlayer.getWorld(), (double)finalX + 0.5D, (double)((float)y + 0.5F), (double)(finalZ + (z < 0 ? 0.1F : -0.1F)));
                        }

                        if (location != null) {
                            loopPlayer.spigot().playEffect(location, border.getParticle(), border.getMaterial().getId(), 0, 0.0F, 0.0F, 0.0F, 0.0F, 1, 15);
                        }
                    }
                }
            }
        }
    }

    private boolean shouldCheck(Player player,Border border) {
        Cuboid cuboid = border.getPhysicalBounds().clone().inset(Cuboid.CuboidDirection.HORIZONTAL, 15);
        return !this.contains(player.getLocation().getBlockX(), player.getLocation().getBlockZ(), cuboid);
    }

    public boolean contains(int x, int z, Cuboid cuboid) {
        return x >= cuboid.getLowerX() && x <= cuboid.getUpperX() && z >= cuboid.getLowerZ() && z <= cuboid.getUpperZ();
    }
}