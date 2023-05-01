package org.spigotmc;

import net.minecraft.server.*;
import org.bukkit.craftbukkit.SpigotTimings;

import java.util.List;

public class ActivationRange
{

    static AxisAlignedBB maxBB = AxisAlignedBB.a( 0, 0, 0, 0, 0, 0 );
    static AxisAlignedBB miscBB = AxisAlignedBB.a( 0, 0, 0, 0, 0, 0 );
    static AxisAlignedBB animalBB = AxisAlignedBB.a( 0, 0, 0, 0, 0, 0 );
    static AxisAlignedBB monsterBB = AxisAlignedBB.a( 0, 0, 0, 0, 0, 0 );

    // Kohi - interval to update activation states
    public static int INTERVAL = 10;

    /**
     * Initializes an entities type on construction to specify what group this
     * entity is in for activation ranges.
     *
     * @param entity
     * @return group id
     */
    public static byte initializeEntityActivationType(Entity entity)
    {
        if ( entity instanceof EntityMonster || entity instanceof EntitySlime )
        {
            return 1; // Monster
        } else if ( entity instanceof EntityCreature || entity instanceof EntityAmbient )
        {
            return 2; // Animal
        } else
        {
            return 3; // Misc
        }
    }

    /**
     * These entities are excluded from Activation range checks.
     *
     * @param entity
     * @param world
     * @return boolean If it should always tick.
     */
    public static boolean initializeEntityActivationState(Entity entity, SpigotWorldConfig config)
    {
        // Kohi - add EntityArrow to this list of entity classes
        // We shouldn't need to, as we test for EntityProjectile, but kohi does so why not.

        if ( ( entity.activationType == 3 && config.miscActivationRange == 0 )
                || ( entity.activationType == 2 && config.animalActivationRange == 0 )
                || ( entity.activationType == 1 && config.monsterActivationRange == 0 )
                || entity instanceof EntityHuman
                || entity instanceof EntityProjectile
                || entity instanceof EntityArrow
                || entity instanceof EntityEnderDragon
                || entity instanceof EntityComplexPart
                || entity instanceof EntityWither
                || entity instanceof EntityFireball
                || entity instanceof EntityWeather
                || entity instanceof EntityTNTPrimed
                || entity instanceof EntityEnderCrystal
                || entity instanceof EntityFireworks )
        {
            return true;
        }

        return false;
    }

    /**
     * Utility method to grow an AABB without creating a new AABB or touching
     * the pool, so we can re-use ones we have.
     *
     * @param target
     * @param source
     * @param x
     * @param y
     * @param z
     */
    public static void growBB(AxisAlignedBB target, AxisAlignedBB source, int x, int y, int z)
    {
        target.a = source.a - x;
        target.b = source.b - y;
        target.c = source.c - z;
        target.d = source.d + x;
        target.e = source.e + y;
        target.f = source.f + z;
    }

    /**
     * Find what entities are in range of the players in the world and set
     * active if in range.
     *
     * @param world
     */
    public static void activateEntities(World world)
    {
        if (MinecraftServer.currentTick % INTERVAL != 0) return; // Kohi - only update on our interval

        SpigotTimings.entityActivationCheckTimer.startTiming();
        final int miscActivationRange = world.spigotConfig.miscActivationRange;
        final int animalActivationRange = world.spigotConfig.animalActivationRange;
        final int monsterActivationRange = world.spigotConfig.monsterActivationRange;

        int maxRange = Math.max(monsterActivationRange, animalActivationRange);
        maxRange = Math.max(maxRange, miscActivationRange);
        maxRange = Math.min((world.spigotConfig.viewDistance << 4) - 8, maxRange);

        for (Entity player : (List<Entity>) world.players) {
            player.activatedTick = MinecraftServer.currentTick;
            growBB( maxBB, player.boundingBox, maxRange, 256, maxRange );
            growBB( miscBB, player.boundingBox, miscActivationRange, 256, miscActivationRange );
            growBB( animalBB, player.boundingBox, animalActivationRange, 256, animalActivationRange );
            growBB( monsterBB, player.boundingBox, monsterActivationRange, 256, monsterActivationRange );

            int i = MathHelper.floor(maxBB.a / 16.0D);
            int j = MathHelper.floor(maxBB.d / 16.0D);
            int k = MathHelper.floor(maxBB.c / 16.0D);
            int l = MathHelper.floor(maxBB.f / 16.0D);

            Chunk chunk = null; // MineHQ
            for (int i1 = i; i1 <= j; ++i1) {
                for (int j1 = k; j1 <= l; ++j1) {
                    // MineHQ start
                    if ((chunk = world.getChunkIfLoaded(i1, j1)) != null) {
                        activateChunkEntities(chunk);
                    }
                    // MineHQ end
                }
            }
        }
    }

    /**
     * Checks for the activation state of all entities in this chunk.
     *
     * @param chunk
     */
    private static void activateChunkEntities(Chunk chunk)
    {
        for ( List<Entity> slice : chunk.entitySlices )
        {
            for ( Entity entity : slice )
            {
                if ( entity.activatedTick > MinecraftServer.currentTick + INTERVAL )
                {
                    continue;
                }
                if ( entity.defaultActivationState || checkEntityImmunities( entity ) )
                {
                    entity.activatedTick = MinecraftServer.currentTick + INTERVAL;
                    continue;
                }

                switch ( entity.activationType )
                {
                    case 1:
                        if ( monsterBB.b( entity.boundingBox ) )
                        {
                            entity.activatedTick = MinecraftServer.currentTick + INTERVAL;
                        }
                        break;
                    case 2:
                        if ( animalBB.b( entity.boundingBox ) )
                        {
                            entity.activatedTick = MinecraftServer.currentTick + INTERVAL;
                        }
                        break;
                    case 3:
                    default:
                        if ( miscBB.b( entity.boundingBox ) )
                        {
                            entity.activatedTick = MinecraftServer.currentTick + INTERVAL;
                        }
                }
            }
        }
    }

    /**
     * If an entity is not in range, do some more checks to see if we should
     * give it a shot.
     *
     * @param entity
     * @return
     */
    public static boolean checkEntityImmunities(Entity entity)
    {
        // quick checks.
        if ( entity.inWater /* isInWater */ || entity.fireTicks > 0 )
        {
            return true;
        }

        // Kohi - remove arrow checks, they are excluded already
        if ( !entity.onGround || entity.passenger != null || entity.vehicle != null )
        {
            return true;
        }
        // special cases.
        if ( entity instanceof EntityLiving )
        {
            EntityLiving living = (EntityLiving) entity;
            // Kohi -  remove hurtticks check, we will activate entities in their hurt routine
            if ( living.attackTicks > 0 || living.getEffects().size() > 0 )
            {
                return true;
            }
            if ( entity instanceof EntityCreature )
            {
                EntityCreature creature = (EntityCreature) entity;
                if ( creature.target != null )
                {
                    return true;
                }
                if ( creature.getLeashHolder() != null )
                {
                    return true;
                }
            }
            if ( entity instanceof EntityVillager && ( (EntityVillager) entity ).bY() /* Getter for first boolean */ )
            {
                return true;
            }
            if ( entity instanceof EntityAnimal )
            {
                EntityAnimal animal = (EntityAnimal) entity;
                if ( animal.ce() /*love*/ )
                {
                    return true;
                }
                if ( entity instanceof EntitySheep && ( (EntitySheep) entity ).isSheared() )
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the entity is active for this tick.
     *
     * @param entity
     * @return
     */
    public static boolean checkIfActive(Entity entity)
    {
        SpigotTimings.checkIfActiveTimer.startTiming();

        // PaperSpigot start - EAR backport
        // Never safe to skip fireworks or entities not yet added to chunk and we don't skip falling blocks
        if ( !entity.isAddedToChunk() || entity instanceof EntityFireworks || entity instanceof EntityFallingBlock || entity.loadChunks ) {
            SpigotTimings.checkIfActiveTimer.stopTiming();
            return true;
        }
        // PaperSpigot end

        boolean isActive = entity.activatedTick >= MinecraftServer.currentTick || entity.defaultActivationState;

        // Kohi - if tps is less than 17 don't activate entities 2/3 of the time
        if ( isActive && !entity.defaultActivationState && MinecraftServer.getServer().recentTps[0] < 17.0 && entity.ticksLived % 3 != 0 )
        {
            isActive = false;
        }

        // Kohi - activate entities with a 1 in 20 chance randomly
        if ( !isActive && entity.world.random.nextInt( 20 ) == 0 )
        {
            isActive = true;
            // and check immunities
            if ( checkEntityImmunities( entity ) )
            {
                entity.activatedTick = MinecraftServer.currentTick + 40;
            }
        }

        // Kohi - remove immunity checks and other things that were here

        int x = MathHelper.floor( entity.locX );
        int z = MathHelper.floor( entity.locZ );
        // Make sure not on edge of unloaded chunk
        Chunk chunk = entity.world.getChunkIfLoaded( x >> 4, z >> 4 );
        if ( isActive && !( chunk != null && chunk.areNeighborsLoaded( 1 ) ) )
        {
            isActive = false;
        }
        SpigotTimings.checkIfActiveTimer.stopTiming();
        return isActive;
    }
}
