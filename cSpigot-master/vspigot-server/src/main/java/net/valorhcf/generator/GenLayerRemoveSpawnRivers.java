package net.valorhcf.generator;

import net.minecraft.server.GenLayer;
import net.minecraft.server.IntCache;
import net.minecraft.server.World;

public class GenLayerRemoveSpawnRivers extends GenLayer {

    private final GenLayer parent;
    private final World world;

    public GenLayerRemoveSpawnRivers(GenLayer parent, World world) {
        super(1);

        this.parent = parent;
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
                int worldX = (x + i) << 2;
                int worldZ = (z + j) << 2;
                if (worldX * worldX + worldZ * worldZ < radiusSqrd) {
                    out[index] = -1;
                } else {
                    out[index] = in[index];
                }
            }
        }
        return out;
    }
}
