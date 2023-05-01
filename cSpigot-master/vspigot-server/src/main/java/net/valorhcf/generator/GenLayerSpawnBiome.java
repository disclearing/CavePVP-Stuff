package net.valorhcf.generator;

import net.minecraft.server.GenLayer;
import net.minecraft.server.IntCache;
import net.minecraft.server.World;

public class GenLayerSpawnBiome extends GenLayer {

    private final GenLayer parent;
    private final int biomeSize;
    private final World world;

    public GenLayerSpawnBiome(GenLayer parent, int biomeSize, World world) {
        super(1);
        this.parent = parent;
        this.biomeSize = biomeSize;
        this.world = world;
    }

    @Override
    public int[] a(int x, int z, int w, int h) {
        int radiusSqrd = world.generatorConfig.spawnBiomeRadius * world.generatorConfig.spawnBiomeRadius;
        int[] in = parent.a(x, z, w, h);
        int[] out = IntCache.a(w * h);

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int index = j * w + i;
                int worldX = (x + i) << 2 << biomeSize;
                int worldZ = (z + j) << 2 << biomeSize;
                if (worldX * worldX + worldZ * worldZ < radiusSqrd) {
                    out[index] = world.generatorConfig.spawnBiome.id;
                } else {
                    out[index] = in[index];
                }
            }
        }
        return out;
    }
}
