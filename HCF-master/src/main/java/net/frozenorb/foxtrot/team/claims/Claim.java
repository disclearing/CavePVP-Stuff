package net.frozenorb.foxtrot.team.claims;

import com.mongodb.BasicDBObject;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.serialization.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class Claim implements Iterable<Coordinate> {

    @Getter private String world;
    @Getter private int x1;
    @Getter private int y1;
    @Getter private int z1;
    @Getter private int x2;
    @Getter private int y2;
    @Getter private int z2;
    @Getter private String name;

    public static Claim fromJson(BasicDBObject obj) {
        Claim c = new Claim(LocationSerializer.deserialize((BasicDBObject) obj.get("Location1")), LocationSerializer.deserialize((BasicDBObject) obj.get("Location2")));
        c.setName(obj.getString("Name"));
        return c;
    }

    public Claim(Location corner1, Location corner2) {
        this(corner1.getWorld().getName(), corner1.getBlockX(), corner1.getBlockY(), corner1.getBlockZ(), corner2.getBlockX(), corner2.getBlockY(), corner2.getBlockZ());
    }

    public Claim(Claim copyFrom) {
        this.world = copyFrom.world;
        this.x1 = copyFrom.x1;
        this.y1 = copyFrom.y1;
        this.z1 = copyFrom.z1;
        this.x2 = copyFrom.x2;
        this.y2 = copyFrom.y2;
        this.z2 = copyFrom.z2;
        this.name = copyFrom.name;
    }

    public Claim(String world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.world = world;
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    public BasicDBObject json() {
        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("Name", name);

        World world = Foxtrot.getInstance().getServer().getWorld(getWorld());
        dbObject.put("Location1", LocationSerializer.serialize(new Location(world, x1, y1, z1)));
        dbObject.put("Location2", LocationSerializer.serialize(new Location(world, x2, y2, z2)));

        return (dbObject);
    }

    public static int getPrice(Claim claim, Team team, boolean buying) {
        int x = Math.abs(claim.x1 - claim.x2);
        int z = Math.abs(claim.z1 - claim.z2);
        int blocks = x * z;
        int done = 0;
        double mod = 0.4D;
        double curPrice = 0D;

        while (blocks > 0) {
            blocks--;
            done++;

            curPrice += mod;

            if (done == 250) {
                done = 0;
                mod += 0.4D;
            }
        }

        // Multiple price by 0.8 (requested by @itsjhalt)
        curPrice *= 0.8F;

        if (buying && team != null) {
            curPrice += 500 * team.getClaims().size();
        }

        return ((int) curPrice);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Claim)) {
            return (false);
        }

        Claim claim = (Claim) object;
        return (claim.getMaximumPoint().equals(getMaximumPoint()) && claim.getMinimumPoint().equals(getMinimumPoint()));
    }

    public Location getMinimumPoint() {
        return (new Location(Foxtrot.getInstance().getServer().getWorld(world), Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2)));
    }

    public Location getMaximumPoint() {
        return (new Location(Foxtrot.getInstance().getServer().getWorld(world), Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2)));
    }

    public boolean contains(int x, int y, int z, String world) {
        return (y >= y1 && y <= y2 && contains(x, z, world));
    }

    public boolean contains(int x, int z, String world) {
        if (world != null && !world.equalsIgnoreCase(this.world)) {
            return (false);
        }

        return (x >= x1 && x <= x2 && z >= z1 && z <= z2);
    }

    public boolean contains(Location location) {
        return (contains(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName()));
    }

    public boolean contains(Block block) {
        return (contains(block.getLocation()));
    }

    public boolean contains(Player player) {
        return (contains(player.getLocation()));
    }

    public Set<Player> getPlayers() {
        Set<Player> players = new HashSet<>();

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (contains(player)) {
                players.add(player);
            }
        }

        return (players);
    }

    @Override
    public int hashCode() {
        return (getMaximumPoint().hashCode() + getMinimumPoint().hashCode());
    }

    @Override
    public String toString() {
        Location corner1 = getMinimumPoint();
        Location corner2 = getMaximumPoint();

        return (corner1.getBlockX() + ":" + corner1.getBlockY() + ":" + corner1.getBlockZ() + ":" + corner2.getBlockX() + ":" + corner2.getBlockY() + ":" + corner2.getBlockZ() + ":" + name + ":" + world);
    }

    public String getFriendlyName() {
        return ("(" + world + ", " + x1 + ", " + y1 + ", " + z1 + ") - (" + world + ", " + x2 + ", " + y2 + ", " + z2 + ")");
    }

    public Claim expand(CuboidDirection dir, int amount) {
        switch (dir) {
            case North:
                return (new Claim(world, this.x1 - amount, this.y1, this.z1, this.x2, this.y2, this.z2));
            case South:
                return (new Claim(world, this.x1, this.y1, this.z1, this.x2 + amount, this.y2, this.z2));
            case East:
                return (new Claim(world, this.x1, this.y1, this.z1 - amount, this.x2, this.y2, this.z2));
            case West:
                return (new Claim(world, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2 + amount));
            case Down:
                return (new Claim(world, this.x1, this.y1 - amount, this.z1, this.x2, this.y2, this.z2));
            case Up:
                return (new Claim(world, this.x1, this.y1, this.z1, this.x2, this.y2 + amount, this.z2));
            default:
                throw (new IllegalArgumentException("Invalid direction " + dir));
        }
    }

    public Claim outset(CuboidDirection dir, int amount) {
        Claim claim;

        switch (dir) {
            case Horizontal:
                claim = expand(CuboidDirection.North, amount).expand(CuboidDirection.South, amount).expand(CuboidDirection.East, amount).expand(CuboidDirection.West, amount);
                break;
            case Vertical:
                claim = expand(CuboidDirection.Down, amount).expand(CuboidDirection.Up, amount);
                break;
            case Both:
                claim = outset(CuboidDirection.Horizontal, amount).outset(CuboidDirection.Vertical, amount);
                break;
            default:
                throw new IllegalArgumentException("Invalid direction " + dir);
        }

        return (claim);
    }

    public boolean isWithin(int x, int z, int radius, String world) {
        return (outset(CuboidDirection.Both, radius).contains(x, z, world));
    }

    public void setLocations(Location loc1, Location loc2) {
        this.x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        this.x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        this.y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        this.y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        this.z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        this.z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
    }

    public Location[] getCornerLocations() {
        World world = Foxtrot.getInstance().getServer().getWorld(this.world);

        return new Location[] {
                new Location(world, x1, y1, z1),
                new Location(world, x2, y1, z2),
                new Location(world, x1, y1, z2),
                new Location(world, x2, y1, z1) };
    }

    @Override
    public Iterator<Coordinate> iterator() {
        return new BorderIterator(x1, z1, x2, z2);
    }

    public static enum BorderDirection {
        POS_X,
        POS_Z,
        NEG_X,
        NEG_Z

    }

    public class BorderIterator implements Iterator<Coordinate> {
        private int x, z;
        private boolean next = true;
        private BorderDirection dir = BorderDirection.POS_Z;

        int maxX = getMaximumPoint().getBlockX(),
                maxZ = getMaximumPoint().getBlockZ();
        int minX = getMinimumPoint().getBlockX(),
                minZ = getMinimumPoint().getBlockZ();

        public BorderIterator(int x1, int z1, int x2, int z2) {
            x = Math.min(x1, x2);
            z = Math.min(z1, z2);
        }

        @Override
        public boolean hasNext() {
            return next;
        }

        @Override
        public Coordinate next() {
            if (dir == BorderDirection.POS_Z) {
                if (++z == maxZ) {
                    dir = BorderDirection.POS_X;
                }
            } else if (dir == BorderDirection.POS_X) {
                if (++x == maxX) {
                    dir = BorderDirection.NEG_Z;
                }
            } else if (dir == BorderDirection.NEG_Z) {
                if (--z == minZ) {
                    dir = BorderDirection.NEG_X;
                }
            } else if (dir == BorderDirection.NEG_X) {
                if (--x == minX) {
                    next = false;
                }
            }

            return new Coordinate(x, z);
        }

        @Override
        public void remove() {}

    }

    public enum CuboidDirection {
        North,
        East,
        South,
        West,
        Up,
        Down,
        Horizontal,
        Vertical,
        Both,
        Unknown

    }

}