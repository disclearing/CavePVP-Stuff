package cc.fyre.proton.border;

import cc.fyre.proton.Proton;
import cc.fyre.proton.border.event.border.BorderChangeEvent;
import cc.fyre.proton.border.runnable.BorderTask;
import cc.fyre.proton.cuboid.Cuboid;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

public class  Border {

    @Getter private final Location origin;
    @Getter private Material material;
    @Getter private int size;
    @Getter private int height;
    @Getter private boolean wrapTerrain;
    @Getter private BorderConfiguration borderConfiguration;
    @Getter private Effect particle;
    @Getter private BorderTask borderTask;
    @Getter private Cuboid physicalBounds;

    @Getter private static final boolean[] airBlocks = new boolean[256];

    public Border(Location origin,Material material,int size,int height) {
        this.material = Material.BEDROCK;
        this.wrapTerrain = false;
        this.borderConfiguration = BorderConfiguration.DEFAULT_CONFIGURATION;
        this.origin = origin;
        this.size = size;
        this.height = height;
        this.material = material == null ? Material.BEDROCK : material;
        this.physicalBounds = new Cuboid(origin.clone().add((double)(size + 1), (double)origin.getWorld().getMaxHeight() - origin.getY(), (double)(size + 1)), origin.clone().subtract((double)(size + 1), origin.getY(), (double)(size + 1)));
        this.borderTask = new BorderTask(this);

        Proton.getInstance().getBorderHandler().addBorder(this);
    }

    public Cuboid contract(int amount) {
        this.size -= amount;

        final Cuboid prev = this.physicalBounds.clone();

        this.physicalBounds = this.physicalBounds.inset(Cuboid.CuboidDirection.HORIZONTAL, amount);

        return prev;
    }

    public Cuboid expand(int amount) {

        this.size += amount;

        final Cuboid prev = this.physicalBounds.clone();

        this.physicalBounds = this.physicalBounds.expand(Cuboid.CuboidDirection.NORTH, amount).expand(Cuboid.CuboidDirection.SOUTH, amount).expand(Cuboid.CuboidDirection.EAST, amount).expand(Cuboid.CuboidDirection.WEST, amount);

        return prev;
    }

    public Cuboid setSize(int size) {
        return this.setSize(size, true);
    }

    public Cuboid setSize(int size, boolean callEvent) {
        return this.setSize(size, this.height, callEvent);
    }

    public Cuboid setSize(int size, int height, boolean callEvent) {
        int previousSize = this.size;
        this.size = size;
        this.height = height;
        Cuboid prev = this.physicalBounds.clone();

        this.physicalBounds = new Cuboid(this.origin.clone().add((double)(size + 1), (double)this.origin.getWorld().getMaxHeight() - this.origin.getY(), (double)(size + 1)), this.origin.clone().subtract((double)(size + 1), this.origin.getY(), (double)(size + 1)));

        if (callEvent) {
            Proton.getInstance().getServer().getPluginManager().callEvent(new BorderChangeEvent(this,previousSize,prev,BorderTask.BorderAction.SET));
        }

        return prev;
    }

    public boolean contains(Block block) {
        return this.contains(block.getX(), block.getZ());
    }

    public boolean contains(Entity entity) {
        return this.contains(entity.getLocation());
    }

    public boolean contains(Location location) {
        return this.contains(location.getBlockX(), location.getBlockZ());
    }

    public boolean contains(int x, int z) {
        return x > this.physicalBounds.getLowerX() && x < this.physicalBounds.getUpperX() && z > this.physicalBounds.getLowerZ() && z < this.physicalBounds.getUpperZ();
    }

    public void fill() {
        World world = this.origin.getWorld();
        int xMin = this.physicalBounds.getLowerX();
        int xMax = this.physicalBounds.getUpperX();
        int zMin = this.physicalBounds.getLowerZ();
        int zMax = this.physicalBounds.getUpperZ();
        int tick = 0;
        int chunksPerTick = 20;

        int chunkX;
        for(chunkX = zMin >> 4; chunkX <= zMax >> 4; ++chunkX) {

            final int finalChunkX = chunkX;

            Proton.getInstance().getServer().getScheduler().runTaskLater(Proton.getInstance(), () -> {
                Chunk chunk = world.getChunkAt(xMin >> 4, finalChunkX);

                for(int z = Math.max(zMin, finalChunkX << 4); z < Math.min(zMax, finalChunkX + 1 << 4); ++z) {
                    this.fillAtXZ(world, chunk, xMin, z);
                }

            }, (long)(tick++ / chunksPerTick));
            Proton.getInstance().getServer().getScheduler().runTaskLater(Proton.getInstance(), () -> {
                Chunk chunk = world.getChunkAt(xMax >> 4, finalChunkX);

                for(int z = Math.max(zMin + 1, finalChunkX << 4); z < Math.min(zMax + 1, finalChunkX + 1 << 4); ++z) {
                    this.fillAtXZ(world, chunk, xMax, z);
                }

            }, (long)(tick++ / chunksPerTick));
        }

        for(chunkX = xMin >> 4; chunkX <= xMax >> 4; ++chunkX) {

            final int finalChunkX = chunkX;

            Proton.getInstance().getServer().getScheduler().runTaskLater(Proton.getInstance(), () -> {
                Chunk chunk = world.getChunkAt(finalChunkX, zMin >> 4);

                for(int x = Math.max(xMin + 1, finalChunkX << 4); x < Math.min(xMax + 1, finalChunkX + 1 << 4); ++x) {
                    this.fillAtXZ(world, chunk, x, zMin);
                }

            }, (long)(tick++ / chunksPerTick));
            Proton.getInstance().getServer().getScheduler().runTaskLater(Proton.getInstance(), () -> {
                Chunk chunk = world.getChunkAt(finalChunkX, zMax >> 4);

                for(int x = Math.max(xMin, finalChunkX << 4); x < Math.min(xMax, finalChunkX + 1 << 4); ++x) {
                    this.fillAtXZ(world, chunk, x, zMax);
                }

            }, (long)(tick++ / chunksPerTick));
        }

    }

    private void fillAtXZ(World world, Chunk chunk, int x, int z) {
        int y;
        if (this.wrapTerrain) {
            for(y = world.getHighestBlockYAt(x, z); airBlocks[chunk.getBlock(x, y, z).getType().getId()] && y > 0; --y) {
                ;
            }

            for(y += this.height; y >= 0; --y) {
                chunk.getBlock(x, y, z).setTypeIdAndData(this.material.getId(), (byte)0, false);
            }
        } else {
            for(y = 0; y <= this.origin.getBlockY() + this.height; ++y) {
                chunk.getBlock(x, y, z).setTypeIdAndData(this.material.getId(), (byte)0, false);
            }
        }

    }

    public Location correctLocation(Location location) {
        Cuboid cuboid = this.getPhysicalBounds();
        int validX = location.getBlockX();
        int validZ = location.getBlockZ();
        EnsureAction xAction = null;
        EnsureAction zAction = null;
        if (location.getBlockX() + 2 > cuboid.getUpperX()) {
            xAction = EnsureAction.DECREASE;
            validX = xAction.apply(cuboid.getUpperX(), 4);
        } else if (location.getBlockX() - 2 < cuboid.getLowerX()) {
            xAction = EnsureAction.INCREASE;
            validX = xAction.apply(cuboid.getLowerX(), 4);
        }

        if (location.getBlockZ() + 2 > cuboid.getUpperZ()) {
            zAction = EnsureAction.DECREASE;
            validZ = zAction.apply(cuboid.getUpperZ(), 4);
        } else if (location.getBlockZ() - 2 < cuboid.getLowerZ()) {
            zAction = EnsureAction.INCREASE;
            validZ = zAction.apply(cuboid.getLowerZ(), 4);
        }

        int validY = location.getWorld().getHighestBlockYAt(validX, validZ);
        Location validLoc = new Location(location.getWorld(), (double)validX + 0.5D, (double)validY + 0.5D, (double)validZ + 0.5D);

        for(int var9 = 0; !isSafe(validLoc) && var9++ < 30; validLoc = new Location(location.getWorld(), (double)validX + 0.5D, (double)validY + 0.5D, (double)validZ + 0.5D)) {
            if (xAction != null) {
                validX = xAction.apply(validX, 1);
            }

            if (zAction != null) {
                validZ = zAction.apply(validZ, 1);
            }

            validY = location.getWorld().getHighestBlockYAt(validX, validZ);
        }

        validLoc.setPitch(location.getPitch());
        validLoc.setYaw(location.getYaw());
        return validLoc;
    }

    private static boolean isSafe(Location location) {
        return location.getBlock().getRelative(BlockFace.DOWN).getType().isSolid() && location.getBlock().isEmpty() && location.getBlock().getRelative(BlockFace.UP).isEmpty();
    }

    public Cuboid getPhysicalBounds() {
        return this.physicalBounds.clone();
    }

    public Border setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public Border setHeight(int height) {
        this.height = height;
        return this;
    }

    public Border setWrapTerrain(boolean wrapTerrain) {
        this.wrapTerrain = wrapTerrain;
        return this;
    }

    public Border setBorderConfiguration(BorderConfiguration borderConfiguration) {
        this.borderConfiguration = borderConfiguration;
        return this;
    }

    public Border setParticle(Effect particle) {
        this.particle = particle;
        return this;
    }

    static {
        airBlocks[Material.LOG.getId()] = true;
        airBlocks[Material.LOG_2.getId()] = true;
        airBlocks[Material.LEAVES.getId()] = true;
        airBlocks[Material.LEAVES_2.getId()] = true;
        airBlocks[Material.HUGE_MUSHROOM_1.getId()] = true;
        airBlocks[Material.HUGE_MUSHROOM_2.getId()] = true;
        airBlocks[Material.SNOW.getId()] = true;

        for (int i = 0; i < Material.values().length; i++) {

            final Material material = Material.values()[i];

            if (material.isBlock() && !material.isSolid()) {
                airBlocks[material.getId()] = true;
            }

        }

        airBlocks[Material.WATER.getId()] = false;
        airBlocks[Material.STATIONARY_WATER.getId()] = false;
        airBlocks[Material.LAVA.getId()] = false;
        airBlocks[Material.STATIONARY_LAVA.getId()] = false;
    }

    public enum EnsureAction {

        INCREASE,
        DECREASE;

        public int apply(int previous, int amount) {
            return this == INCREASE ? previous + amount : previous - amount;
        }

    }
}
