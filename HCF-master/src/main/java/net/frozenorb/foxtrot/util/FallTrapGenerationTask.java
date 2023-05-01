package net.frozenorb.foxtrot.util;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

@RequiredArgsConstructor
public class FallTrapGenerationTask {

    @Getter
    private PhaseType phase = PhaseType.WALLS;

    private BukkitTask task;

    private long start;

    private final Team team;

    private final Claim claim;
    
    private final Material material;
    private final byte data;

    private FastBlockUpdate fastBlockUpdate = new FastBlockUpdate(Foxtrot.getInstance(), 1);

    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), this::onTimer, 0, 20);
    }

    public void cancel() {
        task.cancel();
    }

    private void onTimer() {
        if (phase.equals(PhaseType.WALLS)) {
            if (fastBlockUpdate.isComplete()) {
                fastBlockUpdate.cancel();

                team.sendMessage(ChatColor.GREEN + "Faller phase " + ChatColor.WHITE + phase.getName() + ChatColor.GREEN + " complete!");

                phase = PhaseType.EMPTY;
                return;
            }

            if (fastBlockUpdate.isRunning()) {
                double percentage = 100.0 - ((double) fastBlockUpdate.getFastBlockUpdateTask().getBlocksRemaining().size()) / ((double) fastBlockUpdate.getBlocks().size()) * 100.0;
                if (percentage % 5.0 == 0) {
                    team.sendMessage(ChatColor.GREEN + "FallTrap generation is at " + ChatColor.WHITE + "(" + (int) percentage + "%)" + ChatColor.GREEN + ".");
                    team.sendMessage(ChatColor.GREEN + "  Phase: " + ChatColor.WHITE + phase.getName());
                }
                return;
            }

            Location min = claim.getMinimumPoint();
            Location max = claim.getMaximumPoint();
            World world = min.getWorld();

            Cuboid cuboid = new Cuboid(min, max);
            for (Cuboid.CuboidDirection direction : Arrays.asList(Cuboid.CuboidDirection.North, Cuboid.CuboidDirection.East, Cuboid.CuboidDirection.South, Cuboid.CuboidDirection.West)) {
                for (Block block : cuboid.getFace(direction)) {
                    for (int i = world.getHighestBlockYAt(max) + 11; i > 0; i--) {
                        Location location = new Location(world, block.getX(), i, block.getZ());

                        if (i >= 63 && (Arrays.asList(Material.AIR).contains(location.getBlock().getType()))) {
                            continue;
                        }

                        fastBlockUpdate.addBlock(location, material, data);
                    }
                }
            }

            fastBlockUpdate.start();

            start = System.currentTimeMillis();

            team.sendMessage(ChatColor.GREEN + "Starting Faller generation around your selection.");
            team.sendMessage(ChatColor.GREEN + "Faller phase " + ChatColor.GREEN + phase.getName() + ChatColor.GREEN + " started!");
            return;
        }

        if (phase.equals(PhaseType.EMPTY)) {
            if (fastBlockUpdate.isComplete()) {
                fastBlockUpdate.cancel();

                cancel();

                team.sendMessage(ChatColor.GREEN + "Finished Faller generation around your selection. It took " + ChatColor.WHITE + DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - start, true, true) + ChatColor.GREEN + " to make it.");
                return;
            }

            if (fastBlockUpdate.isRunning()) {
                double percentage = 100.0 - ((double) fastBlockUpdate.getFastBlockUpdateTask().getBlocksRemaining().size()) / ((double) fastBlockUpdate.getBlocks().size()) * 100.0;
                if (percentage % 5.0 == 0) {
                    team.sendMessage(ChatColor.GREEN + "Faller generation is at " + ChatColor.GREEN + "(" + (int) percentage + "%)" + ChatColor.GREEN + ".");
                    team.sendMessage(ChatColor.GREEN + "  Phase: " + ChatColor.GREEN + phase.getName());
                }
                return;
            }

            Location min = claim.getMinimumPoint();
            Location max = claim.getMaximumPoint();
            World world = min.getWorld();

            int minX = min.getBlockX();
            int minZ = min.getBlockZ();

            int maxX = max.getBlockX();
            int maxZ = max.getBlockZ();

            for (int x = minX; x <= maxX; x++) {
                for (int y = world.getHighestBlockYAt(max) + 11; y > 0; y--) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Location location = new Location(world, x, y, z);

                        if (Arrays.asList(Material.AIR, material).contains(location.getBlock().getType())) {
                            continue;
                        }

                        fastBlockUpdate.addBlock(location, Material.AIR, (byte) 0);
                    }
                }
            }

            fastBlockUpdate.start();
            team.sendMessage(ChatColor.GREEN + "Faller phase " + ChatColor.WHITE + phase.getName() + ChatColor.GREEN + " started!");
        }
    }

    public enum PhaseType {

        EMPTY("Clear Blocks"), WALLS("Wall Blocks");

        @Getter
        private String name;

        PhaseType(String name) {
            this.name = name;
        }
    }
}
