package net.frozenorb.foxtrot.util;

import org.bukkit.entity.Player;

public enum DirectionUtil {
    NORTH(0), SOUTH(180), EAST(90), WEST(270);

    private int degrees;

    DirectionUtil(int degrees) {
        this.degrees = degrees;
    }

    public int getDegrees() {
        return degrees;
    }

    public static DirectionUtil getDirection(Player player) {
        float yaw = player.getLocation().getYaw();
        if (yaw < 0) {
            yaw += 360;
        }

        if (yaw >= 315 || yaw < 45) {
            return DirectionUtil.SOUTH;
        } else if (yaw < 135) {
            return DirectionUtil.WEST;
        } else if (yaw < 225) {
            return DirectionUtil.NORTH;
        } else if (yaw < 315) {
            return DirectionUtil.EAST;
        }

        return DirectionUtil.SOUTH;
    }
}
