package cc.fyre.proton.util;

import com.google.common.collect.ImmutableSet;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Set;

public class BlockUtils {

    private static final Set<Material> INTERACTABLE;

    public static boolean isInteractable(Block block) {
        return isInteractable(block.getType());
    }

    public static boolean isInteractable(Material material) {
        return INTERACTABLE.contains(material);
    }

    public static boolean setBlockFast(World world,int x,int y,int z,int blockId,byte data) {
        net.minecraft.server.v1_7_R4.World w = ((CraftWorld)world).getHandle();

        final Chunk chunk = w.getChunkAt(x >> 4, z >> 4);

        return a(chunk, x & 15, y, z & 15, net.minecraft.server.v1_7_R4.Block.getById(blockId), data);
    }

    private static void queueChunkForUpdate(Player player,int cx,int cz) {
        ((CraftPlayer)player).getHandle().chunkCoordIntPairQueue.add(new ChunkCoordIntPair(cx, cz));
    }

    private static boolean a(Chunk that,int i,int j,int k,net.minecraft.server.v1_7_R4.Block block,int l) {
        int i1 = k << 4 | i;

        if (j >= that.b[i1] - 1) {
            that.b[i1] = -999;
        }

        int j1 = that.heightMap[i1];
        net.minecraft.server.v1_7_R4.Block block1 = that.getType(i, j, k);
        int k1 = that.getData(i, j, k);
        if (block1 == block && k1 == l) {
            return false;
        } else {
            boolean flag = false;
            ChunkSection chunksection = that.getSections()[j >> 4];
            if (chunksection == null) {
                if (block == Blocks.AIR) {
                    return false;
                }

                chunksection = that.getSections()[j >> 4] = new ChunkSection(j >> 4 << 4, !that.world.worldProvider.g);
                flag = j >= j1;
            }

            int l1 = that.locX * 16 + i;
            int i2 = that.locZ * 16 + k;
            if (!that.world.isStatic) {
                block1.f(that.world, l1, j, i2, k1);
            }

            if (!(block1 instanceof IContainer)) {
                chunksection.setTypeId(i, j & 15, k, block);
            }

            if (!that.world.isStatic) {
                block1.remove(that.world, l1, j, i2, block1, k1);
            } else if (block1 instanceof IContainer && block1 != block) {
                that.world.p(l1, j, i2);
            }

            if (block1 instanceof IContainer) {
                chunksection.setTypeId(i, j & 15, k, block);
            }

            if (chunksection.getTypeId(i, j & 15, k) != block) {
                return false;
            } else {
                chunksection.setData(i, j & 15, k, l);
                if (flag) {
                    that.initLighting();
                }

                TileEntity tileentity;
                if (block1 instanceof IContainer) {
                    tileentity = that.e(i, j, k);
                    if (tileentity != null) {
                        tileentity.u();
                    }
                }

                if (!that.world.isStatic && (!that.world.captureBlockStates || block instanceof BlockContainer)) {
                    block.onPlace(that.world, l1, j, i2);
                }

                if (block instanceof IContainer) {
                    if (that.getType(i, j, k) != block) {
                        return false;
                    }

                    tileentity = that.e(i, j, k);
                    if (tileentity == null) {
                        tileentity = ((IContainer)block).a(that.world, l);
                        that.world.setTileEntity(l1, j, i2, tileentity);
                    }

                    if (tileentity != null) {
                        tileentity.u();
                    }
                }

                that.n = true;
                return true;
            }
        }
    }

    static {
        INTERACTABLE = ImmutableSet.of(Material.FENCE_GATE, Material.FURNACE, Material.BURNING_FURNACE, Material.BREWING_STAND, Material.CHEST, Material.HOPPER, new Material[]{Material.DISPENSER, Material.WOODEN_DOOR, Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.TRAPPED_CHEST, Material.TRAP_DOOR, Material.LEVER, Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.BED_BLOCK, Material.ANVIL, Material.BEACON});
    }


}
