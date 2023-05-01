package net.frozenorb.foxtrot.util;


import org.bukkit.entity.Player;

public class PlayerDirection {

    public static String getCardinalDirection(Player player) {
        double rot = (player.getLocation().getYaw() - 90) % 360;
        if (rot < 0) {
            rot += 360.0;
        }
        return getDirection(rot);
    }

    /**
     * Converts a rotation to a cardinal direction name.
     *
     * @param rot
     * @return
     */
    public static String getDirection(double rot) {
        if (0 <= rot && rot < 22.5) {
            return "W";
        } else if (22.5 <= rot && rot < 67.5) {
            return "NW";
        } else if (67.5 <= rot && rot < 112.5) {
            return "N";
        } else if (112.5 <= rot && rot < 157.5) {
            return "NE";
        } else if (157.5 <= rot && rot < 202.5) {
            return "E";
        } else if (202.5 <= rot && rot < 247.5) {
            return "SE";
        } else if (247.5 <= rot && rot < 292.5) {
            return "S";
        } else if (292.5 <= rot && rot < 337.5) {
            return "SW";
        } else if (337.5 <= rot && rot < 360.0) {
            return "W";
        } else {
            return null;
        }
    }


}
