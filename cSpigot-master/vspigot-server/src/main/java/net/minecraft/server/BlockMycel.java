package net.minecraft.server;

import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;

import java.util.Random;
// CraftBukkit end

public class BlockMycel extends Block {

    protected BlockMycel() {
        super(Material.GRASS);
        this.a(true);
        this.a(CreativeModeTab.b);
    }

    public void a(World world, int i, int j, int k, Random random) {
        if (!world.isStatic) {
            // Poweruser start
            int lightLevel = world.getLightLevel(i, j + 1, k);
            if (lightLevel < 4 && world.getType(i, j + 1, k).k() > 2) {
            // Poweruser end
                // CraftBukkit start
                org.bukkit.World bworld = world.getWorld();
                BlockState blockState = bworld.getBlockAt(i, j, k).getState();
                blockState.setType(CraftMagicNumbers.getMaterial(Blocks.DIRT));

                BlockFadeEvent event = new BlockFadeEvent(blockState.getBlock(), blockState);
                world.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    blockState.update(true);
                }
                // CraftBukkit end
            } else if (lightLevel >= 9) { // Poweruser
                int numGrowth = Math.min(4, Math.max(20, (int) (4 * 100F / world.growthOdds))); // Spigot
                for (int l = 0; l < numGrowth; ++l) { // Spigot
                    int i1 = i + random.nextInt(3) - 1;
                    int j1 = j + random.nextInt(5) - 3;
                    int k1 = k + random.nextInt(3) - 1;
                    Block block = world.getType(i1, j1 + 1, k1);

                    if (world.getType(i1, j1, k1) == Blocks.DIRT && world.getData(i1, j1, k1) == 0 && world.getLightLevel(i1, j1 + 1, k1) >= 4 && block.k() <= 2) {
                        // CraftBukkit start
                        org.bukkit.World bworld = world.getWorld();
                        BlockState blockState = bworld.getBlockAt(i1, j1, k1).getState();
                        blockState.setType(CraftMagicNumbers.getMaterial(this));

                        BlockSpreadEvent event = new BlockSpreadEvent(blockState.getBlock(), bworld.getBlockAt(i, j, k), blockState);
                        world.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled()) {
                            blockState.update(true);
                        }
                        // CraftBukkit end
                    }
                }
            }
        }
    }

    public Item getDropType(int i, Random random, int j) {
        return Blocks.DIRT.getDropType(0, random, j);
    }
}
