package cc.fyre.proton.util;

import org.bukkit.block.BlockFace;

public class DirectionUtils {

    public static float directionToYaw(BlockFace direction) {
        if (direction == null) {
            return 0.0F;
        } else {
            switch(direction.getOppositeFace().ordinal()) {
                case 1:
                    return 0.0F;
                case 2:
                    return 22.5F;
                case 3:
                    return 45.0F;
                case 4:
                    return 67.5F;
                case 5:
                    return 90.0F;
                case 6:
                    return 112.5F;
                case 7:
                    return 135.0F;
                case 8:
                    return 157.5F;
                case 9:
                    return 180.0F;
                case 10:
                    return -157.5F;
                case 11:
                    return -135.0F;
                case 12:
                    return -112.5F;
                case 13:
                    return -90.0F;
                case 14:
                    return -67.5F;
                case 15:
                    return -45.0F;
                case 16:
                    return -22.5F;
                default:
                    return 0.0F;
            }
        }
    }

    public static BlockFace yawToDirection(float yaw) {
        while(yaw > 180.0F) {
            yaw -= 360.0F;
        }

        while(yaw <= -180.0F) {
            yaw += 360.0F;
        }

        if ((double)yaw < -168.75D) {
            return BlockFace.NORTH;
        } else if ((double)yaw < -146.25D) {
            return BlockFace.NORTH_NORTH_EAST;
        } else if ((double)yaw < -123.75D) {
            return BlockFace.NORTH_EAST;
        } else if ((double)yaw < -101.25D) {
            return BlockFace.EAST_NORTH_EAST;
        } else if ((double)yaw < -78.75D) {
            return BlockFace.EAST;
        } else if ((double)yaw < -56.25D) {
            return BlockFace.EAST_SOUTH_EAST;
        } else if ((double)yaw < -33.75D) {
            return BlockFace.SOUTH_EAST;
        } else if ((double)yaw < -11.25D) {
            return BlockFace.SOUTH_SOUTH_EAST;
        } else if ((double)yaw < 11.25D) {
            return BlockFace.SOUTH;
        } else if ((double)yaw < 33.75D) {
            return BlockFace.SOUTH_SOUTH_WEST;
        } else if ((double)yaw < 56.25D) {
            return BlockFace.SOUTH_WEST;
        } else if ((double)yaw < 78.75D) {
            return BlockFace.WEST_SOUTH_WEST;
        } else if ((double)yaw < 101.25D) {
            return BlockFace.WEST;
        } else if ((double)yaw < 123.75D) {
            return BlockFace.WEST_NORTH_WEST;
        } else if ((double)yaw < 146.25D) {
            return BlockFace.NORTH_WEST;
        } else if ((double)yaw < 168.75D) {
            return BlockFace.NORTH_NORTH_WEST;
        } else {
            return BlockFace.NORTH;
        }
    }


}
