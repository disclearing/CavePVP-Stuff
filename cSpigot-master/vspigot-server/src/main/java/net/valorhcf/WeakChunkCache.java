package net.valorhcf;

import net.minecraft.server.*;

public class WeakChunkCache implements IBlockAccess {
    private int lowerChunkX;
    private int lowerChunkZ;
    private Chunk[][] chunks;
    private boolean ownArray = false;

    public WeakChunkCache(Chunk[][] chunks, int lowerChunkX, int lowerChunkZ) {
        this.chunks = chunks;
        this.lowerChunkX = lowerChunkX;
        this.lowerChunkZ = lowerChunkZ;
    }

    public WeakChunkCache(World world, int chunkX, int chunkZ, int chunkRadius) {
        this(world, chunkX - chunkRadius, chunkZ - chunkRadius, chunkX + chunkRadius, chunkZ + chunkRadius);
    }

    public WeakChunkCache(World world, int lowerChunkX, int lowerChunkZ, int upperChunkX, int upperChunkZ) {
        this.lowerChunkX = lowerChunkX;
        this.lowerChunkZ = lowerChunkZ;

        this.chunks = new Chunk[upperChunkX - this.lowerChunkX + 1][upperChunkZ - this.lowerChunkZ + 1];
        this.ownArray = true;

        for (int chunkX = this.lowerChunkX; chunkX <= upperChunkX; ++chunkX) {
            for (int chunkZ = this.lowerChunkZ; chunkZ <= upperChunkZ; ++chunkZ) {
                this.chunks[chunkX - this.lowerChunkX][chunkZ - this.lowerChunkZ] = world.getChunkIfLoaded(chunkX, chunkZ);
            }
        }
    }

    public WeakChunkCache(World world, int lowerBlockX, int lowerBlockY, int lowerBlockZ, int upperBlockX, int upperBlockY, int upperBlockZ, int blockRadius) {
        this(world, (lowerBlockX - blockRadius) >> 4,
                    (lowerBlockZ - blockRadius) >> 4,
                    (upperBlockX + blockRadius) >> 4,
                    (upperBlockZ + blockRadius) >> 4);
    }

    public Block getType(int x, int y, int z) {
        Block block = Blocks.AIR;

        if (y >= 0 && y < 256) {
            int indexX = (x >> 4) - this.lowerChunkX;
            int indexZ = (z >> 4) - this.lowerChunkZ;

            if (indexX >= 0 && indexX < this.chunks.length && indexZ >= 0 && indexZ < this.chunks[indexX].length) {
                Chunk chunk = this.chunks[indexX][indexZ];

                if (chunk != null) {
                    block = chunk.getType(x & 15, y, z & 15);
                }
            }
        }

        return block;
    }

    public TileEntity getTileEntity(int x, int y, int z) {
        int indexX = (x >> 4) - this.lowerChunkX;
        int indexZ = (z >> 4) - this.lowerChunkZ;

        Chunk chunk = this.chunks[indexX][indexZ];
        if(chunk != null) {
            return chunk.e(x & 15, y, z & 15);
        }
        return null;
    }

    public int getData(int x, int y, int z) {
        if (y >= 0 && y < 256) {
            int indexX = (x >> 4) - this.lowerChunkX;
            int indexZ = (z >> 4) - this.lowerChunkZ;
            if (indexX >= 0 && indexX < this.chunks.length && indexZ >= 0 && indexZ < this.chunks[indexX].length) {
                Chunk chunk = this.chunks[indexX][indexZ];
                if(chunk != null) {
                    return chunk.getData(x & 15, y, z & 15);
                }
            }
        }
        return 0;
    }

    public int getBlockPower(int i, int j, int k, int l) {
        return this.getType(i, j, k).c(this, i, j, k, l);
    }

    public boolean isLoaded(int x, int y, int z) {
        int indexX = (x >> 4) - this.lowerChunkX;
        int indexZ = (z >> 4) - this.lowerChunkZ;
        if (indexX >= 0 && indexX < this.chunks.length && indexZ >= 0 && indexZ < this.chunks[indexX].length) {
            return this.chunks[indexX][indexZ] != null;
        }
        return false;
    }

    public void clear() {
        if(this.ownArray) {
            for(int i = 0; i < this.chunks.length; i++) {
                for(int j = 0; j < this.chunks[i].length; j++) {
                    this.chunks[i][j] = null;
                }
            }
        }
    }
}
