package net.frozenorb.foxtrot.util;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitTask;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

@RequiredArgsConstructor
public class BaseGenerationTask {

    @Getter
    private PhaseType phase = PhaseType.EMPTY;

    private BukkitTask task;

    private long start;

    private final Team team;

    private final Claim claim;

    private final Material baseMaterial;

    private final byte baseId;
    
    private int highest;

    private FastBlockUpdate fastBlockUpdate = new FastBlockUpdate(Foxtrot.getInstance(), 1);

    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), this::onTimer, 0, 20);
    }

    public void cancel() {
        task.cancel();
    }

    private void onTimer() {
        if (phase.equals(PhaseType.EMPTY)) {
            if (fastBlockUpdate.isComplete()) {
                fastBlockUpdate.cancel();

                team.sendMessage(ChatColor.GREEN + "Base phase " + ChatColor.WHITE + phase.getName() + ChatColor.GREEN + " complete!");

                phase = PhaseType.CELLING;
                return;
            }

            if (fastBlockUpdate.isRunning()) {
                double percentage = 100.0 - ((double) fastBlockUpdate.getFastBlockUpdateTask().getBlocksRemaining().size()) / ((double) fastBlockUpdate.getBlocks().size()) * 100.0;
                if (percentage % 5.0 == 0) {
                    team.sendMessage(ChatColor.GREEN + "Base generation is at " + ChatColor.WHITE + "(" + (int) percentage + "%)" + ChatColor.GREEN + ".");
                    team.sendMessage(ChatColor.GREEN + "  Phase: " + ChatColor.WHITE + phase.getName());
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
                for (int y = 100; y > 60; y--) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Location location = new Location(world, x, y, z);
                        if (Arrays.asList(Material.LOG, Material.LONG_GRASS, Material.RED_ROSE, Material.YELLOW_FLOWER, Material.DOUBLE_PLANT, Material.DEAD_BUSH, Material.CACTUS, Material.LEAVES, Material.LEAVES_2, Material.FENCE).contains(location.getBlock().getType())) {
                            fastBlockUpdate.addBlock(location, Material.AIR, (byte) 0);
                        }
                    }
                }
            }

            fastBlockUpdate.start();

            start = System.currentTimeMillis();

            team.sendMessage(ChatColor.GREEN + "Starting Base generation around your selection.");
            team.sendMessage(ChatColor.GREEN + "Base phase " + ChatColor.WHITE + phase.getName() + ChatColor.GREEN + " started!");
            return;
        }

        if (phase.equals(PhaseType.CELLING)) {
            if (fastBlockUpdate.isComplete()) {
                fastBlockUpdate.cancel();

                team.sendMessage(ChatColor.GREEN + "Base phase " + ChatColor.LIGHT_PURPLE + phase.getName() + ChatColor.GREEN + " complete!");

                phase = PhaseType.WALLS;
                return;
            }

            if (fastBlockUpdate.isRunning()) {
                double percentage = 100.0 - ((double) fastBlockUpdate.getFastBlockUpdateTask().getBlocksRemaining().size()) / ((double) fastBlockUpdate.getBlocks().size()) * 100.0;
                if (percentage % 5.0 == 0) {
                    team.sendMessage(ChatColor.GREEN + "Base generation is at " + ChatColor.WHITE + "(" + (int) percentage + "%)" + ChatColor.GREEN + ".");
                    team.sendMessage(ChatColor.GREEN + "  Phase: " + ChatColor.WHITE + phase.getName());
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

            highest = world.getHighestBlockYAt(max) + 11;
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, highest, z);
                    fastBlockUpdate.addBlock(location, baseMaterial, baseId);
                }
            }

            fastBlockUpdate.start();
            team.sendMessage(ChatColor.GREEN + "Base phase " + ChatColor.WHITE + phase.getName() + ChatColor.GREEN + " started!");
            return;
        }

        if (phase.equals(PhaseType.WALLS)) {
            if (fastBlockUpdate.isComplete()) {
                fastBlockUpdate.cancel();

                cancel();

                team.sendMessage(ChatColor.GREEN + "Finished Base generation around your selection. It took " + ChatColor.WHITE + DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - start, true, true) + ChatColor.GREEN + ".");
                return;
            }

            if (fastBlockUpdate.isRunning()) {
                double percentage = 100.0 - ((double) fastBlockUpdate.getFastBlockUpdateTask().getBlocksRemaining().size()) / ((double) fastBlockUpdate.getBlocks().size()) * 100.0;
                if (percentage % 5.0 == 0) {
                    team.sendMessage(ChatColor.GREEN + "Base generation is at " + ChatColor.WHITE + "(" + (int) percentage + "%)" + ChatColor.GREEN + ".");
                    team.sendMessage(ChatColor.GREEN + "  Phase: " + ChatColor.WHITE + phase.getName());
                }
                return;
            }

            Location min = claim.getMinimumPoint();
            Location max = claim.getMaximumPoint();
            World world = min.getWorld();
            
            Cuboid cuboid = new Cuboid(min, max);

            final List<Material> override = Arrays.asList(Material.AIR, Material.LOG, Material.LONG_GRASS, Material.RED_ROSE, Material.YELLOW_FLOWER, Material.DOUBLE_PLANT, Material.DEAD_BUSH, Material.CACTUS, Material.LEAVES, Material.LEAVES_2, Material.FENCE);

            for (Cuboid.CuboidDirection direction : Arrays.asList(Cuboid.CuboidDirection.North, Cuboid.CuboidDirection.East, Cuboid.CuboidDirection.South, Cuboid.CuboidDirection.West)) {
                for (Block block : cuboid.getFace(direction)) {
                    for (int i = highest; i > 0; i--) {
                        Location location = new Location(world, block.getX(), i, block.getZ());
                        if (i >= 63 && (override.contains(location.getBlock().getType()))) {
                            fastBlockUpdate.addBlock(location, baseMaterial, baseId);

                            final Block downBlock = location.getBlock().getRelative(BlockFace.DOWN);

                            if (!override.contains(downBlock.getType())) {
                                fastBlockUpdate.addBlock(downBlock.getLocation(), Material.WOOL, baseId);
                            }
                        }
                    }
                }
            }

            fastBlockUpdate.start();
            team.sendMessage(ChatColor.GREEN + "Base phase " + ChatColor.WHITE + phase.getName() + ChatColor.GREEN + " started!");
        }
    }

    public enum PhaseType {

        EMPTY("Clear Blocks"), CELLING("Ceiling Blocks"), WALLS("Wall Blocks");

        @Getter
        private String name;

        PhaseType(String name) {
            this.name = name;
        }
    }
}
