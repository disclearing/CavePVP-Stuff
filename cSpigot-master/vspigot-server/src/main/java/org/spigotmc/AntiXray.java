package org.spigotmc;

import net.minecraft.server.Block;
import net.minecraft.server.Blocks;
import net.minecraft.server.World;
import net.minecraft.util.gnu.trove.set.TByteSet;
import net.minecraft.util.gnu.trove.set.hash.TByteHashSet;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

public class AntiXray
{

    private static final CustomTimingsHandler update = new CustomTimingsHandler( "xray - update" );
    private static final CustomTimingsHandler obfuscate = new CustomTimingsHandler( "xray - obfuscate" );
    /*========================================================================*/
    // Used to keep track of which blocks to obfuscate
    private final boolean[] obfuscateBlocks = new boolean[ Short.MAX_VALUE ];
    // Used to select a random replacement ore
    private final byte[] replacementOres;

    public AntiXray(SpigotWorldConfig config)
    {
        // Set all listed blocks as true to be obfuscated
        for ( int id : ( config.engineMode == 1 ) ? config.hiddenBlocks : config.replaceBlocks )
        {
            obfuscateBlocks[id] = true;
        }

        // For every block
        TByteSet blocks = new TByteHashSet();
        for ( Integer i : config.hiddenBlocks )
        {
            Block block = Block.getById( i );
            // Check it exists and is not a tile entity
            if ( block != null && !block.isTileEntity() )
            {
                // Add it to the set of replacement blocks
                blocks.add( (byte) (int) i );
            }
        }
        // Bake it to a flat array of replacements
        replacementOres = blocks.toArray();
    }

    /**
     * Starts the timings handler, then updates all blocks within the set radius
     * of the given coordinate, revealing them if they are hidden ores.
     */
    public void updateNearbyBlocks(World world, int x, int y, int z)
    {
        if ( world.spigotConfig.antiXray )
        {
            update.startTiming();
            updateNearbyBlocks( world, x, y, z, 2, false ); // 2 is the radius, we shouldn't change it as that would make it exponentially slower
            update.stopTiming();
        }
    }

    /**
     * Starts the timings handler, and then removes all non exposed ores from
     * the chunk buffer.
     */
    public void obfuscateSync(int chunkX, int chunkY, int bitmask, byte[] buffer, World world)
    {
        if ( world.spigotConfig.antiXray )
        {
            obfuscate.startTiming();
            obfuscate( chunkX, chunkY, bitmask, buffer, world, false );
            obfuscate.stopTiming();
        }
    }

    /**
     * Removes all non exposed ores from the chunk buffer.
     */
    public void obfuscate(int chunkX, int chunkY, int bitmask, byte[] buffer, World world, boolean newFormat)
    {
        // If the world is marked as obfuscated
        if ( world.spigotConfig.antiXray )
        {
            // Initial radius to search around for air
            int initialRadius = 1;
            // Which block in the buffer we are looking at, anywhere from 0 to 16^4
            int index = 0;
            // The iterator marking which random ore we should use next
            int randomOre = 0;

            // Chunk corner X and Z blocks
            int startX = chunkX << 4;
            int startZ = chunkY << 4;

            byte replaceWithTypeId;
            switch ( world.getWorld().getEnvironment() )
            {
                case NETHER:
                    replaceWithTypeId = (byte) CraftMagicNumbers.getId(Blocks.NETHERRACK);
                    break;
                case THE_END:
                    replaceWithTypeId = (byte) CraftMagicNumbers.getId(Blocks.WHITESTONE);
                    break;
                default:
                    replaceWithTypeId = (byte) CraftMagicNumbers.getId(Blocks.STONE);
                    break;
            }

            // Chunks can have up to 16 sections
            for ( int i = 0; i < 16; i++ )
            {
                // If the bitmask indicates this chunk is sent...
                if ( ( bitmask & 1 << i ) != 0 )
                {
                    // Work through all blocks in the chunk, y,z,x
                    for ( int y = 0; y < 16; y++ )
                    {
                        for ( int z = 0; z < 16; z++ )
                        {
                            for ( int x = 0; x < 16; x++ )
                            {
                                // For some reason we can get too far ahead of ourselves (concurrent modification on bulk chunks?) so if we do, just abort and move on
                                if ( index >= buffer.length )
                                {
                                    index++;
                                    if ( newFormat ) index++;
                                    continue;
                                }
                                // Grab the block ID in the buffer.
                                // TODO: extended IDs are not yet supported
                                int blockId;
                                int data = 0;
                                if ( newFormat )
                                {
                                    blockId = (buffer[ index ] & 0xFF) | ( ( buffer[ index + 1 ] & 0xFF ) << 8 );
                                    data = blockId & 0xF;
                                    blockId >>>= 4; // Remove data value
                                } else
                                {
                                    blockId = buffer[ index ] & 0xFF;
                                }
                                // Check if the block should be obfuscated
                                if ( obfuscateBlocks[blockId] )
                                {
                                    // The world isn't loaded, bail out
                                    if ( !isLoaded( world, startX + x, ( i << 4 ) + y, startZ + z, initialRadius ) )
                                    {
                                        index++;
                                        if ( newFormat ) index++;
                                        continue;
                                    }
                                    // On the otherhand, if radius is 0, or the nearby blocks are all non air, we can obfuscate
                                    if ( !hasTransparentBlockAdjacent( world, startX + x, ( i << 4 ) + y, startZ + z, initialRadius ) )
                                    {
                                        switch ( world.spigotConfig.engineMode )
                                        {
                                            case 1:
                                                // Replace with replacement material
                                                if ( newFormat )
                                                {
                                                    char replace = (char) ((replaceWithTypeId << 4) | data);
                                                    buffer[ index ] = (byte) ( replace & 0xFF );
                                                    buffer[ index + 1 ] = (byte) ( ( replace >> 8 ) & 0xFF );
                                                } else
                                                {
                                                    buffer[ index ] = replaceWithTypeId;
                                                }
                                                break;
                                            case 2:
                                                // Replace with random ore.
                                                if ( randomOre >= replacementOres.length )
                                                {
                                                    randomOre = 0;
                                                }
                                                if ( newFormat )
                                                {
                                                    char replace = (char) (replacementOres[ randomOre++ ] & 0xFF);
                                                    replace = (char) ((replace << 4) | data);
                                                    buffer[ index ] = (byte) ( replace & 0xFF );
                                                    buffer[ index + 1 ] = (byte) ( ( replace >> 8 ) & 0xFF );
                                                } else
                                                {
                                                    buffer[ index ] = replacementOres[ randomOre++ ];
                                                }
                                                break;
                                        }
                                    }
                                }

                                index++;
                                if (newFormat) index++;
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateNearbyBlocks(World world, int x, int y, int z, int radius, boolean updateSelf)
    {
        // If the block in question is loaded
        if ( world.isLoaded( x, y, z ) )
        {
            // Get block id
            Block block = world.getType(x, y, z);

            // See if it needs update
            if ( updateSelf && obfuscateBlocks[Block.getId( block )] )
            {
                // Send the update
                world.notify( x, y, z );
            }

            // Check other blocks for updates
            if ( radius > 0 )
            {
                updateNearbyBlocks( world, x + 1, y, z, radius - 1, true );
                updateNearbyBlocks( world, x - 1, y, z, radius - 1, true );
                updateNearbyBlocks( world, x, y + 1, z, radius - 1, true );
                updateNearbyBlocks( world, x, y - 1, z, radius - 1, true );
                updateNearbyBlocks( world, x, y, z + 1, radius - 1, true );
                updateNearbyBlocks( world, x, y, z - 1, radius - 1, true );
            }
        }
    }

    private static boolean isLoaded(World world, int x, int y, int z, int radius)
    {
        return world.isLoaded( x, y, z )
                && ( radius == 0 ||
                ( isLoaded( world, x + 1, y, z, radius - 1 )
                && isLoaded( world, x - 1, y, z, radius - 1 )
                && isLoaded( world, x, y + 1, z, radius - 1 )
                && isLoaded( world, x, y - 1, z, radius - 1 )
                && isLoaded( world, x, y, z + 1, radius - 1 )
                && isLoaded( world, x, y, z - 1, radius - 1 ) ) );
    }

    private static boolean hasTransparentBlockAdjacent(World world, int x, int y, int z, int radius)
    {
        return !isSolidBlock(world.getType(x, y, z, false)) /* isSolidBlock */
                || ( radius > 0
                && ( hasTransparentBlockAdjacent( world, x + 1, y, z, radius - 1 )
                || hasTransparentBlockAdjacent( world, x - 1, y, z, radius - 1 )
                || hasTransparentBlockAdjacent( world, x, y + 1, z, radius - 1 )
                || hasTransparentBlockAdjacent( world, x, y - 1, z, radius - 1 )
                || hasTransparentBlockAdjacent( world, x, y, z + 1, radius - 1 )
                || hasTransparentBlockAdjacent( world, x, y, z - 1, radius - 1 ) ) );
    }

    private static boolean isSolidBlock(Block block) {
        // Mob spawners are treated as solid blocks as far as the
        // game is concerned for lighting and other tasks but for
        // rendering they can be seen through therefor we special
        // case them so that the antixray doesn't show the fake
        // blocks around them.
        return block.r() && block != Blocks.MOB_SPAWNER;
    }
}
