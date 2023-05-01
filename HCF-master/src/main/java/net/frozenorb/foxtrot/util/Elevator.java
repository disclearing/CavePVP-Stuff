package net.frozenorb.foxtrot.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;

public enum Elevator {
    UP,
    DOWN;

    public Location getCalculatedLocation(final Location location, Type type) {

        if (this == UP) {

            for (int yLevel = location.getBlockY(); yLevel < 256; yLevel++) {

                if (location.getBlockY() == yLevel) {
                    continue;
                }

                final Location first = new Location(location.getWorld(),location.getX(),yLevel,location.getBlockZ());
                final Location second = new Location(location.getWorld(),location.getX(),(yLevel+1),location.getBlockZ());

                if (type == Type.MINE_CART && this.isValidForMineCart(first,second)) {
                    return new Location(location.getWorld(),location.getBlockX(),yLevel,location.getBlockZ());
                } else if (type == Type.CARPET && this.isValidForCarpet(this,first,second) == Cause.ALLOWED) {
                    return new Location(location.getWorld(),location.getBlockX(),yLevel,location.getBlockZ());
                } else if (type == Type.SIGN && this.isValidForSign(this,first,second) == Cause.ALLOWED) {
                    return new Location(location.getWorld(),location.getBlockX(),yLevel,location.getBlockZ());
                }

            }

            return null;
        } else {

            for (int yLevel = location.getBlockY(); yLevel > 0; yLevel--) {

                if (location.getBlockY() == yLevel) {
                    continue;
                }


                final Location first = new Location(location.getWorld(),location.getX(),yLevel,location.getBlockZ());
                final Location second = new Location(location.getWorld(),location.getX(),(yLevel+1),location.getBlockZ());

                if (type == Type.MINE_CART && this.isValidForMineCart(first,second)) {
                    return first;
                } else if (type == Type.CARPET && this.isValidForCarpet(DOWN,first,second) == Cause.ALLOWED) {
                    return first;
                }  else if (type == Type.SIGN && this.isValidForSign(DOWN,first,second) == Cause.ALLOWED) {
                    return first;
                }


            }

            return null;
        }
    }

    private boolean isValidForMineCart(Location first,Location second) {
        return (first.getBlock().getType() == Material.AIR && second.getBlock().getType() == Material.AIR) || (first.getBlock().getType() == Material.FENCE_GATE && second.getBlock().getType() == Material.FENCE_GATE);
    }

    private Cause isValidForCarpet(Elevator direction,Location first,Location second) {

        if (second.getBlock().getType() != null && second.getBlock().getType() != Material.AIR) {
            return Cause.SECOND_BLOCK_NOT_AIR;
        }

        if (first.getBlock().getType() != Material.CARPET && first.getBlock().getType() == Material.AIR) {
            return Cause.ALLOWED;
        }

        if (first.getBlock().getType() != Material.CARPET) {
            return Cause.FIRST_BLOCK_NOT_REQUIRED_ITEM;
        }

        byte data = 0;

        if (direction == UP) {
            data = 14;
        } else if (direction == DOWN) {
            data = 13;
        }

        if (first.getBlock().getData() != data) {
            return Cause.INVALID_DIRECTION;
        }

        return Cause.ALLOWED;
    }

    private Cause isValidForSign(Elevator elevator,Location first,Location second) {

        if (second.getBlock().getType() != null && second.getBlock().getType() != Material.AIR) {
            return Cause.SECOND_BLOCK_NOT_AIR;
        }
        if (!first.getBlock().getType().name().contains("SIGN") && first.getBlock().getType() == Material.AIR) {
            return Cause.ALLOWED;
        }

        if (!first.getBlock().getType().name().contains("SIGN")) {
            return Cause.FIRST_BLOCK_NOT_REQUIRED_ITEM;
        }

        final Sign sign = (Sign)first.getBlock().getState();

        if (!sign.getLine(0).contains("Elevator")) {
            return Cause.NOT_ELEVATOR;
        }

        if (elevator == UP ? !sign.getLine(1).equalsIgnoreCase("Down"):!sign.getLine(1).equalsIgnoreCase("Up")) {
            return Cause.INVALID_DIRECTION;
        }

        return Cause.ALLOWED;
    }

    private enum Cause {
        FIRST_BLOCK_NOT_REQUIRED_ITEM,
        SECOND_BLOCK_NOT_AIR,
        NOT_ELEVATOR,
        INVALID_DIRECTION,
        ALLOWED
    }

    public enum Type {

        SIGN,
        CARPET,
        MINE_CART

    }

}
