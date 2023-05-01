package net.minecraft.server;

import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.player.PlayerPearlRefundEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.material.Gate;
import org.bukkit.material.Openable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;

// CraftBukkit start

public class EntityEnderPearl extends EntityProjectile {

    private Location lastValidTeleport;

    private Item toRefundPearl = null;
    private EntityLiving c;
    private Float angle;

    public EntityEnderPearl(World world) {
        super(world);
        this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls; // PaperSpigot
    }

    public EntityEnderPearl(World world, EntityLiving entityliving) {
        super(world, entityliving);
        this.c = entityliving;
        this.angle = this.c.pitch;
        this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls; // PaperSpigot
    }

    protected void a(MovingObjectPosition movingobjectposition) {

        Block block = this.world.getType(movingobjectposition.b, movingobjectposition.c, movingobjectposition.d);

        /*if (PASS_THROUGH_BLOCKS.contains(block)) {
            this.hasPassThroughBlock = true;

            BlockIterator bi = null;

            try {
                Vector l = new Vector(this.locX, this.locY, this.locZ);
                Vector l2 = new Vector(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
                Vector dir = new Vector(l2.getX() - l.getX(), l2.getY() - l.getY(), l2.getZ() - l.getZ()).normalize();
                bi = new BlockIterator(this.world.getWorld(), l, dir, 0, 1);
            } catch (IllegalStateException ex) {
                // ignore
            }

            if (bi != null) {

                while (bi.hasNext()) {
                    org.bukkit.block.Block b = bi.next();

                    if (b.getType() != Material.AIR && this.hasPassThroughBlock && !PASS_THROUGH_BLOCKS.contains(this.world.getType(b.getX(),b.getY(),b.getZ()))) {
                        this.hasPassThroughBlock = false;
                    }

                }

                if (this.hasPassThroughBlock) {
                    return;
                }

            }

        } else*/

        if (block == Blocks.FENCE_GATE) {
            BlockIterator bi = null;

            try {
                Vector l = new Vector(this.locX, this.locY, this.locZ);
                Vector l2 = new Vector(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
                Vector dir = new Vector(l2.getX() - l.getX(), l2.getY() - l.getY(), l2.getZ() - l.getZ()).normalize();
                bi = new BlockIterator(this.world.getWorld(), l, dir, 0, 1);
            } catch (IllegalStateException ex) {
                // ignore
            }

            if (bi != null) {
                boolean open = true;
                boolean hasSolidBlock = false;

                while (bi.hasNext()) {
                    org.bukkit.block.Block b = bi.next();

                    // Fyre remove "&& b.getType().isOccluding()" check, stuff like glass dont block vision but are still full blocks.

                    if (b.getType() != Material.FENCE_GATE && b.getType().isSolid()) {
                        hasSolidBlock = true;
                    }

                    // Fyre end

                    if (b.getState().getData() instanceof Gate && !((Gate) b.getState().getData()).isOpen()) {
                        open = false;
                        break;
                    }

                }

                if (open && !hasSolidBlock) {
                    return;
                }

            }
        }

        if (movingobjectposition.entity != null) {

            if (movingobjectposition.entity == this.c) {
                return;
            }

            org.bukkit.block.Block bukkit = this.world.getWorld().getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));

            if (bukkit.getState().getData() instanceof Openable) {

                if (((Openable) bukkit.getState().getData()).isOpen()) {
                    this.lastValidTeleport = movingobjectposition.entity.bukkitEntity.getLocation();
                }

            } else {

                if (this.yaw < 80) {
                    movingobjectposition.entity.damageEntity(DamageSource.projectile(this,this.getShooter()),0.0F);

                    BlockIterator bi = null;

                    try {
                        Vector l = new Vector(this.locX, this.locY, this.locZ);
                        Vector l2 = new Vector(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
                        Vector dir = new Vector(l2.getX() - l.getX(), l2.getY() - l.getY(), l2.getZ() - l.getZ()).normalize();
                        bi = new BlockIterator(this.world.getWorld(), l, dir, 0, 1);
                    } catch (IllegalStateException ex) {
                        // ignore
                    }

                    if (bi != null) {

                        boolean hasSolidBlock = false;

                        while (bi.hasNext()) {
                            org.bukkit.block.Block b = bi.next();

                            if (b.getType().isSolid() && !PASS_THROUGH_BLOCKS.contains(Block.getById(b.getType().getId()))) {
                                hasSolidBlock = true;
                                break;
                            }

                        }

                        if (!hasSolidBlock) {
                            this.lastValidTeleport = movingobjectposition.entity.getBukkitEntity().getLocation();
                        }

                    }
                }

            }

            movingobjectposition.entity.damageEntity(DamageSource.projectile(this,this.getShooter()),0.0F);
        }

        // PaperSpigot start - Remove entities in unloaded chunks
        if (inUnloadedChunk && world.paperSpigotConfig.removeUnloadedEnderPearls) {
            die();
        }
        // PaperSpigot end

        if (!this.world.isStatic) {
            if (this.getShooter() != null && this.getShooter() instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) this.getShooter();


                if (entityplayer.playerConnection.b().isConnected() && entityplayer.world == this.world) { // MineHQ
                    // CraftBukkit start - Fire PlayerTeleportEvent

                    org.bukkit.Location location = this.lastValidTeleport;

                    if (location != null) { // Fyre

                        org.bukkit.craftbukkit.entity.CraftPlayer player = entityplayer.getBukkitEntity();
                        location.setPitch(player.getLocation().getPitch());
                        location.setYaw(player.getLocation().getYaw());

                        PlayerTeleportEvent teleEvent = new PlayerTeleportEvent(player, player.getLocation(), location, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
                        Bukkit.getPluginManager().callEvent(teleEvent);

                        for (int i = 0; i < 32; ++i) {
                            this.world.addParticle("portal", this.locX, this.locY + this.random.nextDouble() * 2.0D, this.locZ, this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
                        }

                        if (!teleEvent.isCancelled() && !entityplayer.playerConnection.isDisconnected()) {
                            if (this.getShooter().am()) {
                                this.getShooter().mount(null);
                            }

                            entityplayer.playerConnection.teleport(teleEvent.getTo());
                            this.getShooter().fallDistance = 0.0F;
                            CraftEventFactory.entityDamage = this;
                            this.getShooter().damageEntity(DamageSource.FALL, 5.0F);
                            CraftEventFactory.entityDamage = null;
                        }
                        // CraftBukkit end
                    } else {
                        Bukkit.getPluginManager().callEvent(new PlayerPearlRefundEvent(entityplayer.getBukkitEntity()));
                    }
                }
            }

            this.die();
        }
    }


    @Override
    public void h() {
        EntityLiving shooter = this.getShooter();

        if (this.ticksLived > 320) {
            this.die();
            return;
        }

        if (shooter != null && !shooter.isAlive()) {
            this.die();
        } else {

            final int x = MathHelper.floor(this.locX);
            final int y = MathHelper.floor(this.locY);
            final int z = MathHelper.floor(this.locZ);

            Block block = this.world.getType(x,y,z);

            if (this.angle >= DOWN_GATE_ANGLE && block == Blocks.FENCE_GATE && BlockFenceGate.b(this.world.getData(x,y,z))) {

                final Location location = new Location(this.world.getWorld(),this.locX,this.locY,this.locZ);

                if (shooter != null) {
                    location.setYaw(shooter.yaw);
                    location.setPitch(shooter.pitch);
                }

                this.lastValidTeleport = location;
            } if (PASS_THROUGH_BLOCKS.contains(block)) {
                this.lastValidTeleport = this.getBukkitEntity().getLocation();
            } else {

                double boundingY = BOUNDING_BOX_Y;
                double boundingXZ = BOUNDING_BOX_X_Z;

                //Bukkit.broadcastMessage(block.getName());

                //if (PASS_THROUGH_BLOCKS.contains(block)) {
                //    boundingY /= 2;
                //    boundingXZ /= 2;
                //}

                final AxisAlignedBB box = AxisAlignedBB.a(
                        this.locX - boundingXZ,
                        this.locY - 0.05D,
                        this.locZ - boundingXZ,
                        this.locX + boundingXZ,
                        this.locY + boundingY,
                        this.locZ + boundingXZ
                );

                final List<AxisAlignedBB> cubes = this.world.getCubes(this,box,PASS_THROUGH_BLOCKS);

                final boolean valid = cubes.isEmpty();
                final boolean prohibited = this.world.boundingBoxContainsMaterials(this.boundingBox.grow(0.25D, 0D, 0.25D),PROHIBITED_PEARL_BLOCKS);

                //Bukkit.broadcastMessage("" + cubes.size() + " -> " + (!prohibited && valid) + "");

                if (!prohibited && valid) {
                    this.lastValidTeleport = this.getBukkitEntity().getLocation();
                }
            }

        }

        super.h();
    }

    public Item getToRefundPearl() {
        return this.toRefundPearl;
    }

    public void setToRefundPearl(Item pearl) {
        this.toRefundPearl = pearl;
    }

    private static final float DOWN_GATE_ANGLE = 79;

    private static final Set<Block> PASS_THROUGH_BLOCKS = Sets.newHashSet(
            Blocks.TRIPWIRE,
            Blocks.TRIPWIRE_SOURCE,

            Blocks.CHEST,
            Blocks.TRAPPED_CHEST,

            Blocks.STEP,
            Blocks.WOOD_STEP,

            Blocks.TORCH,
            Blocks.REDSTONE_TORCH_OFF,
            Blocks.REDSTONE_TORCH_ON,

            Blocks.LEVER,
            Blocks.TRAP_DOOR,
            Blocks.PISTON_EXTENSION,

            Blocks.WOOD_STAIRS,
            Blocks.BRICK_STAIRS,
            Blocks.STONE_STAIRS,
            Blocks.QUARTZ_STAIRS,
            Blocks.ACACIA_STAIRS,
            Blocks.DARK_OAK_STAIRS,
            Blocks.SANDSTONE_STAIRS,
            Blocks.BIRCH_WOOD_STAIRS,
            Blocks.JUNGLE_WOOD_STAIRS,
            Blocks.SPRUCE_WOOD_STAIRS,
            Blocks.COBBLESTONE_STAIRS,
            Blocks.NETHER_BRICK_STAIRS,

            Blocks.ENCHANTMENT_TABLE,
            Blocks.SNOW,
            Blocks.TRAP_DOOR,

            Blocks.SIGN_POST,
            Blocks.COBBLE_WALL,

            Blocks.WOOL_CARPET,
            Blocks.BREWING_STAND,
            Blocks.ENDER_PORTAL_FRAME
    );

    private static final Set<Block> PROHIBITED_PEARL_BLOCKS = Sets.newHashSet(
            Blocks.FENCE,
            //Blocks.FENCE_GATE,
            Blocks.NETHER_FENCE
    );

    //public static double BOUNDING_BOX_X_Z = 0.125D;
    public static double BOUNDING_BOX_Y = 0.5D;
    public static double BOUNDING_BOX_X_Z = 0.3D;
}