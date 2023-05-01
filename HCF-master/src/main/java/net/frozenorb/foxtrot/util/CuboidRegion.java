package net.frozenorb.foxtrot.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class CuboidRegion implements Iterable<Location> {
    private int x1, x2, z1, z2, y1, y2;
    private String world;
    private String name;
    private ArrayList<String> tags = new ArrayList<String>();

    public CuboidRegion(String name, Location corner1, Location corner2) {
        this(name, corner1.getWorld().getName(), corner1.getBlockX(), corner1.getBlockY(), corner1.getBlockZ(), corner2.getBlockX(), corner2.getBlockY(), corner2.getBlockZ());
    }

    public CuboidRegion(String name, String world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.world = world;
        this.name = name;
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    /**
     * Gets the name of the region
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of tags the rg has
     *
     * @return tags
     */
    public ArrayList<String> getTags() {
        return tags;
    }

    /**
     * Adds a tag to the region
     *
     * @param tag
     *            the tag to add
     */
    public void addTag(String tag) {
        tags.add(tag.toLowerCase());
    }

    /**
     * Checks if the region has a tag
     *
     * @param name
     *            the tag name
     */
    public boolean hasTag(String name) {
        return tags.contains(name.toLowerCase());
    }

    /**
     * Removes a tag from the region
     *
     * @param tag
     *            the tag to remove
     */
    public void removeTag(String tag) {
        tags.remove(tag.toLowerCase());

    }

    /**
     * Gets the minimum point of the region
     *
     * @return minimum point
     */
    public Location getMinimumPoint() {
        return new Location(Bukkit.getWorld(world), Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
    }

    /**
     * Gets the maximum point of the region
     *
     * @return maximum point
     */
    public Location getMaximumPoint() {
        return new Location(Bukkit.getWorld(world), Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
    }

    /**
     * Sets the name of the region
     *
     * @param name
     *            name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return true if the point at (x,y,z) is contained within this region.
     *
     * @param x
     *            the X coordinate
     * @param y
     *            the Y coordinate
     * @param z
     *            the Z coordinate
     * @return true if the given point is within this region, false otherwise
     */
    public boolean contains(int x, int y, int z) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

    /**
     * Check if the given location is contained within this region.
     *
     * @param l
     *            the location to check for
     * @return true if the location is within this region, false otherwise
     */
    public boolean contains(Location l) {
        if (!world.equals(l.getWorld().getName())) {
            return false;
        }
        return contains(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    /**
     * Checks if the given Block is contained within the region
     *
     * @param b
     *            the block to check for
     * @return true if the Location is within this Region, false otherwise
     */
    public boolean contains(Block b) {
        return contains(b.getLocation());
    }

    /**
     * Checks if the given Player's location is contained within the region
     *
     * @param p
     *            the player to check for
     * @return true if the Location is within this region, false otherwise
     */
    public boolean contains(Player p) {
        return contains(p.getLocation());
    }

    /**
     * Gets a set of players that is in the region
     *
     * @return set of players in the region
     */
    public HashSet<Player> getPlayers() {
        HashSet<Player> players = new HashSet<Player>();
        for (Player p : Bukkit.getWorld(world).getPlayers()) {
            if (contains(p))
                players.add(p);
        }
        return players;
    }

    /**
     * Sets the location of the region
     * <p>
     * Handles min and max corner sorting automatically
     *
     * @param loc1
     *            first corner
     * @param loc2
     *            second corner
     */
    public void setLocation(Location loc1, Location loc2) {
        setLocation(loc1.getWorld().getName(), loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ(), loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());
    }

    /**
     * Sets the location of the region
     * <p>
     * Handles min and max corner sorting automatically
     *
     * @param world
     *            world name
     * @param x1
     *            first x coordinate
     * @param y1
     *            first y coordinate
     * @param z1
     *            first z coordinate
     * @param x2
     *            second x coordinate
     * @param y2
     *            second y coordinate
     * @param z2
     *            second z coordinate
     */
    public void setLocation(String world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.world = world;
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CuboidRegion) {
            return ((CuboidRegion) obj).getName().equals(getName());
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Iterator<Location> iterator() {
        return new CuboidIterator(world, x1, y1, z1, x2, y2, z2);
    }

    /**
     * Class to handle location iteration for CuboidRegions
     *
     * @author Kerem Celik
     *
     */
    public class CuboidIterator implements Iterator<Location> {
        private int baseX, baseY, baseZ;
        private int x, y, z;
        private int sizeX, sizeY, sizeZ;
        private String world;

        public CuboidIterator(String world, int x1, int y1, int z1, int x2, int y2, int z2) {
            this.world = world;
            baseX = x1;
            baseY = y1;
            baseZ = z1;
            sizeX = Math.abs(x2 - x1) + 1;
            sizeY = Math.abs(y2 - y1) + 1;
            sizeZ = Math.abs(z2 - z1) + 1;
            x = y = z = 0;
        }

        @Override
        public boolean hasNext() {
            return x < sizeX && y < sizeY && z < sizeZ;
        }

        @Override
        public Location next() {
            Location b = new Location(Bukkit.getWorld(world), baseX + x, baseY + y, baseZ + z);
            if (++x >= sizeX) {
                x = 0;
                if (++y >= sizeY) {
                    y = 0;
                    ++z;
                }
            }
            return b;
        }

        @Override
        public void remove() {}
    }

}