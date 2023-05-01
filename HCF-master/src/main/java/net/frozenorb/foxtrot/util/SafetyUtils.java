package net.frozenorb.foxtrot.util;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SafetyUtils {
    private static ArrayList<String> passableBlocks = Lists.newArrayList(
            "AIR",
            "LONG_GRASS",
            "DOUBLE_PLANT"
    );

    private static boolean isSafe(Location location) {
        for (int i = 0; i <= 1; ) {
            if (!isPassable(location.clone().add(0, i, 0).getBlock())) {
                return false;
            }

            i++;
        }

        return true;
    }

    public static boolean isSafeInDirection(final Location currentLocation, int length, Player player) {
        final DirectionUtil direction = DirectionUtil.getDirection(player);
        Location location = currentLocation.clone();

        if (!isSafe(location) || direction == null) {
            return false;
        }

        for (int i = 0; i <= length; ) {
            if (!isSafe(getBlockInDirection(direction, location))) {
                return false;
            }
            i++;
        }

        return true;
    }

    public static Location getBlockInDirection(DirectionUtil directionUtil, Location location) {
        Location newLocation = location.clone();
        switch (directionUtil) {
            case EAST:
                newLocation.add(1, 0, 0);
                break;
            case WEST:
                newLocation.add(-1, 0, 0);
                break;
            case NORTH:
                newLocation.add(0, 0, -1);
                break;
            case SOUTH:
                newLocation.add(0, 0, 1);
                break;
        }

        return newLocation;
    }

    public static List<Location> getSurroundingBlocks(Location location) {

        return Lists.newArrayList(
                location.clone().add(0, 0, 1),
                location.clone().add(1, 0, 1),
                location.clone().add(-1, 0, 1),

                location.clone().add(1, 0, 0),
                location.clone().add(-1, 0, 0),

                location.clone().add(0, 0, -1),
                location.clone().add(1, 0, -1),
                location.clone().add(-1, 0, -1)
        );
    }

    /**
     * @param block Block you wish to check for.
     * @return Whether it can be passed.
     * <p>
     * Passable defined by:
     * AIR, LIQUID or Settings.
     */
    public static boolean isPassable(Block block) {
        return (block == null) || (block.isLiquid()) || ((block.getType()) == Material.AIR) || passableBlocks.contains(block.getType().name());
    }

}
