package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.bukkit.craftbukkit.util.LongHash;

import java.util.ArrayList;
import java.util.List;

public class BiomeCache {

    private final WorldChunkManager a;
    private long b;
    private Long2ObjectMap c = new Long2ObjectOpenHashMap(); // MineHQ
    private List d = new ArrayList();

    public BiomeCache(WorldChunkManager worldchunkmanager) {
        this.a = worldchunkmanager;
    }

    public BiomeCacheBlock a(int i, int j) {
        i >>= 4;
        j >>= 4;
        // MineHQ start
        long k = LongHash.toLong(i, j);
        BiomeCacheBlock biomecacheblock = (BiomeCacheBlock) this.c.get(k);
        // MineHQ end

        if (biomecacheblock == null) {
            biomecacheblock = new BiomeCacheBlock(this, i, j);
            this.c.put(k, biomecacheblock);
            this.d.add(biomecacheblock);
        }

        biomecacheblock.e = MinecraftServer.ar();
        return biomecacheblock;
    }

    public BiomeBase b(int i, int j) {
        return this.a(i, j).a(i, j);
    }

    public void a() {
        long i = MinecraftServer.ar();
        long j = i - this.b;

        if (j > 7500L || j < 0L) {
            this.b = i;

            for (int k = 0; k < this.d.size(); ++k) {
                BiomeCacheBlock biomecacheblock = (BiomeCacheBlock) this.d.get(k);
                long l = i - biomecacheblock.e;

                if (l > 30000L || l < 0L) {
                    this.d.remove(k--);
                    long i1 = LongHash.toLong(biomecacheblock.c, biomecacheblock.d); // MineHQ

                    this.c.remove(i1);
                }
            }
        }
    }

    public BiomeBase[] d(int i, int j) {
        return this.a(i, j).b;
    }

    static WorldChunkManager a(BiomeCache biomecache) {
        return biomecache.a;
    }
}