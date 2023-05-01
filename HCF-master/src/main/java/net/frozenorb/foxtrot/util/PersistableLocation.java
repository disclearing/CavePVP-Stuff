package net.frozenorb.foxtrot.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import net.minecraft.util.com.google.common.base.Preconditions;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Data
public class PersistableLocation implements ConfigurationSerializable, Cloneable {

    // Lazy loaded
    private Location location;
    private World world;
    private String worldName;

    @Setter
    private double x;

    @Setter
    private double y;

    @Setter
    private double z;

    @Setter
    private float yaw;

    @Setter
    private float pitch;

    public PersistableLocation(Location location) {
        Preconditions.checkNotNull(location, "Location cannot be null");
        Preconditions.checkNotNull(location.getWorld(), "Locations' world cannot be null");

        this.world = location.getWorld();
        this.worldName = world.getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public PersistableLocation(World world, double x, double y, double z) {
        this.worldName = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = this.yaw = 0.0F;
    }

    public PersistableLocation(String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = this.yaw = 0.0F;
    }

    public PersistableLocation(Map<String, Object> map) {
        this.worldName = (String) map.get("worldName");

        Object o = map.get("x");
        if (o instanceof String) {
            this.x = Double.parseDouble((String) o);
        } else this.x = (Double) o;

        o = map.get("y");
        if (o instanceof String) {
            this.y = Double.parseDouble((String) o);
        } else this.y = (Double) o;

        o = map.get("z");
        if (o instanceof String) {
            this.z = Double.parseDouble((String) o);
        } else this.z = (Double) o;

        this.yaw = Float.parseFloat((String) map.get("yaw"));
        this.pitch = Float.parseFloat((String) map.get("pitch"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("worldName", worldName);
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        map.put("yaw", Float.toString(yaw));
        map.put("pitch", Float.toString(pitch));
        return map;
    }

    /**
     * Gets the {@link World} this is in.
     *
     * @return the containing world
     */
    public World getWorld() {
        Preconditions.checkNotNull(this.worldName, "World name cannot be null");

        world = Bukkit.getWorld(this.worldName);
        return world;
    }

    /**
     * Sets the {@link World} this is in.
     *
     * @param world the world to set
     */
    public void setWorld(World world) {
        this.worldName = world.getName();
        this.world = world;
    }

    /**
     * Converts this to a {@link Location}.
     *
     * @return the location instance
     */
    public Location getLocation() {
        this.location = new Location(getWorld(), this.x, this.y, this.z, this.yaw, this.pitch);

        return this.location;
    }

    @Override
    public PersistableLocation clone() throws CloneNotSupportedException {
        try {
            return (PersistableLocation) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }
}

