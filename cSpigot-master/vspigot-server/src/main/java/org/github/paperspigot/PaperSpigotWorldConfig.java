package org.github.paperspigot;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class PaperSpigotWorldConfig
{

    private final String worldName;
    private final YamlConfiguration config;
    private boolean verbose;

    public PaperSpigotWorldConfig(String worldName)
    {
        this.worldName = worldName;
        this.config = PaperSpigotConfig.config;
        init();
    }

    public void init()
    {
        this.verbose = getBoolean( "verbose", true );

        log( "-------- World Settings For [" + worldName + "] --------" );
        PaperSpigotConfig.readConfig( PaperSpigotWorldConfig.class, this );
    }

    private void log(String s)
    {
        if ( verbose )
        {
            Bukkit.getLogger().info( s );
        }
    }

    private void set(String path, Object val)
    {
        config.set( "world-settings.default." + path, val );
    }

    private boolean getBoolean(String path, boolean def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return config.getBoolean( "world-settings." + worldName + "." + path, config.getBoolean( "world-settings.default." + path ) );
    }

    private double getDouble(String path, double def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return config.getDouble( "world-settings." + worldName + "." + path, config.getDouble( "world-settings.default." + path ) );
    }

    private int getInt(String path, int def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return config.getInt( "world-settings." + worldName + "." + path, config.getInt( "world-settings.default." + path ) );
    }

    private float getFloat(String path, float def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return config.getFloat( "world-settings." + worldName + "." + path, config.getFloat( "world-settings.default." + path ) );
    }

    private <T> List getList(String path, T def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return (List<T>) config.getList( "world-settings." + worldName + "." + path, config.getList( "world-settings.default." + path ) );
    }

    private String getString(String path, String def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return config.getString( "world-settings." + worldName + "." + path, config.getString( "world-settings.default." + path ) );
    }

    public boolean allowUndeadHorseLeashing;
    private void allowUndeadHorseLeashing()
    {
        allowUndeadHorseLeashing = getBoolean( "allow-undead-horse-leashing", true );
        log( "Allow undead horse types to be leashed: " + allowUndeadHorseLeashing );
    }

    public double squidMinSpawnHeight;
    public double squidMaxSpawnHeight;
    private void squidSpawnHeight()
    {
        squidMinSpawnHeight = getDouble( "squid-spawn-height.minimum", 45.0D );
        squidMaxSpawnHeight = getDouble( "squid-spawn-height.maximum", 63.0D );
        log( "Squids will spawn between Y: " + squidMinSpawnHeight + " and Y: " + squidMaxSpawnHeight);
    }

    public float playerBlockingDamageMultiplier;
    private void playerBlockingDamageMultiplier()
    {
        playerBlockingDamageMultiplier = getFloat( "player-blocking-damage-multiplier", 0.75F );
        log( "Player blocking damage multiplier set to " + playerBlockingDamageMultiplier);
    }

    public int cactusMaxHeight;
    public int reedMaxHeight;
    private void blockGrowthHeight()
    {
        cactusMaxHeight = getInt( "max-growth-height.cactus", 3 );
        reedMaxHeight = getInt( "max-growth-height.reeds", 3 );
        log( "Max height for cactus growth " + cactusMaxHeight + ". Max height for reed growth " + reedMaxHeight);
    }

    public boolean invertedDaylightDetectors;
    private void invertedDaylightDetectors()
    {
        invertedDaylightDetectors = getBoolean( "inverted-daylight-detectors", false );
        log( "Inverted Redstone Lamps: " + invertedDaylightDetectors );
    }

    public int fishingMinTicks;
    public int fishingMaxTicks;
    private void fishingTickRange()
    {
        fishingMinTicks = getInt( "fishing-time-range.MinimumTicks", 100 );
        fishingMaxTicks = getInt( "fishing-time-range.MaximumTicks", 900 );
    }

    public float blockBreakExhaustion;
    public float playerSwimmingExhaustion;
    private void exhaustionValues ()
    {
        blockBreakExhaustion = getFloat( "player-exhaustion.block-break", 0.025F );
        playerSwimmingExhaustion = getFloat("player-exhaustion.swimming", 0.015F );
    }

    public Integer softDespawnDistance;
    public Integer hardDespawnDistance;
    private void despawnDistances()
    {
        softDespawnDistance = getInt( "despawn-ranges.soft", 32 ); // 32^2 = 1024, Minecraft Default
        hardDespawnDistance = getInt( "despawn-ranges.hard", 128 ); // 128^2 = 16384, Minecraft Default;
        
        if ( softDespawnDistance > hardDespawnDistance)
        {
            softDespawnDistance = hardDespawnDistance;
        }
        
        log( "Living Entity Despawn Ranges:  Soft: " + softDespawnDistance + " Hard: " + hardDespawnDistance );
        
        softDespawnDistance = softDespawnDistance*softDespawnDistance;
        hardDespawnDistance = hardDespawnDistance*hardDespawnDistance;
    }
    
    public boolean keepSpawnInMemory;
    private void keepSpawnInMemory()
    {
        keepSpawnInMemory = getBoolean( "keep-spawn-loaded", true );
        log( "Keep spawn chunk loaded: " + keepSpawnInMemory );
    }

    public double fallingBlockHeightNerf;
    private void fallingBlockheightNerf()
    {
        // Technically a little disingenuous as it applies to all falling blocks but alas, backwards compat prevails!
        fallingBlockHeightNerf = getDouble( "tnt-entity-height-nerf", 0 );
        if (fallingBlockHeightNerf != 0) {
            log( "TNT/Falling Block Height Limit set to Y: " + fallingBlockHeightNerf);
        }
    }

    public int waterOverLavaFlowSpeed;
    private void waterOverLavaFlowSpeed()
    {
        waterOverLavaFlowSpeed = getInt( "water-over-lava-flow-speed", 5 );
        log( "Water over lava flow speed: " + waterOverLavaFlowSpeed);
    }

    public boolean removeInvalidMobSpawnerTEs;
    private void removeInvalidMobSpawnerTEs()
    {
        removeInvalidMobSpawnerTEs = getBoolean( "remove-invalid-mob-spawner-tile-entities", true );
        log( "Remove invalid mob spawner tile entities: " + removeInvalidMobSpawnerTEs );
    }

    public boolean removeUnloadedEnderPearls;
    public boolean removeUnloadedTNTEntities;
    public boolean removeUnloadedFallingBlocks;
    private void removeUnloaded()
    {
        removeUnloadedEnderPearls = getBoolean( "remove-unloaded.enderpearls", true );
        removeUnloadedTNTEntities = getBoolean( "remove-unloaded.tnt-entities", true );
        removeUnloadedFallingBlocks = getBoolean( "remove-unloaded.falling-blocks", true );
    }

    public boolean boatsDropBoats;
    public boolean lessPickyTorches;
    public boolean disablePlayerCrits;
    private void mechanicsChanges()
    {
        boatsDropBoats = getBoolean( "game-mechanics.boats-drop-boats", false );
        lessPickyTorches = getBoolean( "game-mechanics.less-picky-torch-placement", false );
        disablePlayerCrits = getBoolean( "game-mechanics.disable-player-crits", false);
    }

    public int tickNextTickListCap;
    public boolean tickNextTickListCapIgnoresRedstone;
    private void tickNextTickListCap()
    {
        tickNextTickListCap = getInt( "tick-next-tick-list-cap", 10000 ); // Higher values will be friendlier to vanilla style mechanics (to a point) but may hurt performance
        tickNextTickListCapIgnoresRedstone = getBoolean("tick-next-tick-list-cap-ignores-redstone", false); // Redstone TickNextTicks will always bypass the preceding cap.
        log( "WorldServer TickNextTickList cap set at " + tickNextTickListCap );
        log( "WorldServer TickNextTickList cap always processes redstone: " + tickNextTickListCapIgnoresRedstone );
    }

    public boolean useAsyncLighting;
    private void useAsyncLighting()
    {
        useAsyncLighting = getBoolean( "use-async-lighting", false );
        log( "World async lighting: " + useAsyncLighting );
    }

    public boolean generateCanyon;
    public boolean generateCaves;
    public boolean generateDungeon;
    public boolean generateFortress;
    public boolean generateMineshaft;
    public boolean generateStronghold;
    public boolean generateTemple;
    public boolean generateVillage;
    public boolean generateFlatBedrock;
    private void generatorSettings()
    {
        generateCanyon = getBoolean( "generator-settings.canyon", true );
        generateCaves = getBoolean( "generator-settings.caves", true );
        generateDungeon = getBoolean( "generator-settings.dungeon", true );
        generateFortress = getBoolean( "generator-settings.fortress", true );
        generateMineshaft = getBoolean( "generator-settings.mineshaft", true );
        generateStronghold = getBoolean( "generator-settings.stronghold", true );
        generateTemple = getBoolean( "generator-settings.temple", true );
        generateVillage = getBoolean( "generator-settings.village", true );
        generateFlatBedrock = getBoolean( "generator-settings.flat-bedrock", false );
    }

    public boolean loadUnloadedEnderPearls;
    public boolean loadUnloadedTNTEntities;
    public boolean loadUnloadedFallingBlocks;
    private void loadUnloaded()
    {
        loadUnloadedEnderPearls = getBoolean( "load-chunks.enderpearls", false );
        loadUnloadedTNTEntities = getBoolean( "load-chunks.tnt-entities", false );
        loadUnloadedFallingBlocks = getBoolean( "load-chunks.falling-blocks", false );
    }

    public boolean fallingBlocksCollideWithSigns;
    private void fallingBlocksCollideWithSigns()
    {
        fallingBlocksCollideWithSigns = getBoolean( "falling-blocks-collide-with-signs", false );
    }

    public boolean disableEndCredits;
    private void disableEndCredits()
    {
        disableEndCredits = getBoolean( "game-mechanics.disable-end-credits", false );
    }

    public boolean optimizeExplosions;
    private void optimizeExplosions()
    {
        optimizeExplosions = getBoolean( "optimize-explosions", false );
    }

    public boolean fastDrainLava;
    public boolean fastDrainWater;
    private void fastDraining()
    {
        fastDrainLava = getBoolean( "fast-drain.lava", false );
        fastDrainWater = getBoolean( "fast-drain.water", false );
    }

    public int lavaFlowSpeedNormal;
    public int lavaFlowSpeedNether;
    private void lavaFlowSpeed()
    {
        lavaFlowSpeedNormal = getInt( "lava-flow-speed.normal", 30 );
        lavaFlowSpeedNether = getInt( "lava-flow-speed.nether", 10 );
    }
}
