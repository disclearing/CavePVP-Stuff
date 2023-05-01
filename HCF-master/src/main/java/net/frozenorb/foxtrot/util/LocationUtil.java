package net.frozenorb.foxtrot.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

public class LocationUtil {

    public static Location fromVector(World world, BlockVector vector) {
        if (vector == null) return null;
        return new Location(world, vector.getX(), vector.getY(), vector.getZ());
    }

}
