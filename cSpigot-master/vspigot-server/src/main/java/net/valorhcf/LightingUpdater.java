package net.valorhcf;

import net.minecraft.server.*;
import org.bukkit.craftbukkit.util.LongHash;

import java.util.HashMap;
import java.util.List;

public class LightingUpdater {

    private int[] arrayI;
    private HashMap<Long, Chunk> chunks;

    public LightingUpdater() {
        this.arrayI = new int['\u8000'];
        this.chunks = new HashMap<Long, Chunk>();
    }

    public boolean c(EnumSkyBlock enumskyblock, int i, int j, int k, Chunk chunk, List<Chunk> neighbors) { // PaperSpigot
        if (chunk != null && neighbors != null) {
            this.chunks.clear();
            this.chunks.put(LongHash.toLong(chunk.locX, chunk.locZ), chunk);
            for(Chunk neighborchunk: neighbors) {
                this.chunks.put(LongHash.toLong(neighborchunk.locX, neighborchunk.locZ), neighborchunk);
            }

            int l = 0;
            int i1 = 0;

            //this.methodProfiler.a("getBrightness");
            int j1 = this.b(enumskyblock, i, j, k);
            int k1 = this.a(i, j, k, enumskyblock);
            int l1;
            int i2;
            int j2;
            int k2;
            int l2;
            int i3;
            int j3;
            int k3;
            int l3;

            if (k1 > j1) {
                arrayI[i1++] = 133152;
            } else if (k1 < j1) {
                arrayI[i1++] = 133152 | j1 << 18;

                while (l < i1) {
                    l1 = arrayI[l++];
                    i2 = (l1 & 63) - 32 + i;
                    j2 = (l1 >> 6 & 63) - 32 + j;
                    k2 = (l1 >> 12 & 63) - 32 + k;
                    l2 = l1 >> 18 & 15;
                    i3 = this.b(enumskyblock, i2, j2, k2);
                    if (i3 == l2) {
                        this.b(enumskyblock, i2, j2, k2, 0);
                        if (l2 > 0) {
                            j3 = MathHelper.a(i2 - i);
                            l3 = MathHelper.a(j2 - j);
                            k3 = MathHelper.a(k2 - k);
                            if (j3 + l3 + k3 < 17) {
                                for (int i4 = 0; i4 < 6; ++i4) {
                                    int j4 = i2 + Facing.b[i4];
                                    int k4 = j2 + Facing.c[i4];
                                    int l4 = k2 + Facing.d[i4];
                                    int i5 = Math.max(1, this.getType(j4, k4, l4).k());

                                    i3 = this.b(enumskyblock, j4, k4, l4);
                                    if (i3 == l2 - i5 && i1 < arrayI.length) {
                                        arrayI[i1++] = j4 - i + 32 | k4 - j + 32 << 6 | l4 - k + 32 << 12 | l2 - i5 << 18;
                                    }
                                }
                            }
                        }
                    }
                }

                l = 0;
            }

            //this.methodProfiler.b();
            //this.methodProfiler.a("checkedPosition < toCheckCount");

            while (l < i1) {
                l1 = arrayI[l++];
                i2 = (l1 & 63) - 32 + i;
                j2 = (l1 >> 6 & 63) - 32 + j;
                k2 = (l1 >> 12 & 63) - 32 + k;
                l2 = this.b(enumskyblock, i2, j2, k2);
                i3 = this.a(i2, j2, k2, enumskyblock);
                if (i3 != l2) {
                    this.b(enumskyblock, i2, j2, k2, i3);
                    if (i3 > l2) {
                        j3 = Math.abs(i2 - i);
                        l3 = Math.abs(j2 - j);
                        k3 = Math.abs(k2 - k);
                        boolean flag = i1 < arrayI.length - 6;

                        if (j3 + l3 + k3 < 17 && flag) {
                            if (this.b(enumskyblock, i2 - 1, j2, k2) < i3) {
                                arrayI[i1++] = i2 - 1 - i + 32 + (j2 - j + 32 << 6) + (k2 - k + 32 << 12);
                            }

                            if (this.b(enumskyblock, i2 + 1, j2, k2) < i3) {
                                arrayI[i1++] = i2 + 1 - i + 32 + (j2 - j + 32 << 6) + (k2 - k + 32 << 12);
                            }

                            if (this.b(enumskyblock, i2, j2 - 1, k2) < i3) {
                                arrayI[i1++] = i2 - i + 32 + (j2 - 1 - j + 32 << 6) + (k2 - k + 32 << 12);
                            }

                            if (this.b(enumskyblock, i2, j2 + 1, k2) < i3) {
                                arrayI[i1++] = i2 - i + 32 + (j2 + 1 - j + 32 << 6) + (k2 - k + 32 << 12);
                            }

                            if (this.b(enumskyblock, i2, j2, k2 - 1) < i3) {
                                arrayI[i1++] = i2 - i + 32 + (j2 - j + 32 << 6) + (k2 - 1 - k + 32 << 12);
                            }

                            if (this.b(enumskyblock, i2, j2, k2 + 1) < i3) {
                                arrayI[i1++] = i2 - i + 32 + (j2 - j + 32 << 6) + (k2 + 1 - k + 32 << 12);
                            }
                        }
                    }
                }
            }

            // PaperSpigot start - Asynchronous light updates
            if (chunk.world.paperSpigotConfig.useAsyncLighting) {
                chunk.pendingLightUpdates.decrementAndGet();
                if (neighbors != null) {
                    for (Chunk neighbor : neighbors) {
                        neighbor.pendingLightUpdates.decrementAndGet();
                    }
                }
            }
            // PaperSpigot end
            //this.methodProfiler.b();
            this.chunks.clear();
            return true;
        }
        return false;
    }

    private void b(EnumSkyBlock enumskyblock, int i, int j, int k, int l) {
        if (i >= -30000000 && k >= -30000000 && i < 30000000 && k < 30000000) {
            if (j >= 0) {
                if (j < 256) {
                    Chunk chunk = this.getChunk(i >> 4, k >> 4);
                    if(chunk != null) {
                        chunk.a(enumskyblock, i & 15, j, k & 15, l);
                        chunk.world.m(i, j, k);
                    }
                }
            }
        }
    }

    private int b(EnumSkyBlock enumskyblock, int x, int y, int z) {
        Chunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null && x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
            if (y < 0) {
                y = 0;
            }

            if (y >= 256) {
                y = 255;
            }
            return chunk.getBrightness(enumskyblock, x & 15, y, z & 15);
        } else {
            return enumskyblock.c;
        }
    }

    private Chunk getChunk(int x, int z) {
        return this.chunks.get(LongHash.toLong(x, z));
    }

    private int a(int i, int j, int k, EnumSkyBlock enumskyblock) {
        if (enumskyblock == EnumSkyBlock.SKY && this.i(i, j, k)) {
            return 15;
        } else {
            Block block = this.getType(i, j, k);
            int l = enumskyblock == EnumSkyBlock.SKY ? 0 : block.m();
            int i1 = block.k();

            if (i1 >= 15 && block.m() > 0) {
                i1 = 1;
            }

            if (i1 < 1) {
                i1 = 1;
            }

            if (i1 >= 15) {
                return 0;
            } else if (l >= 14) {
                return l;
            } else {
                for (int j1 = 0; j1 < 6; ++j1) {
                    int k1 = i + Facing.b[j1];
                    int l1 = j + Facing.c[j1];
                    int i2 = k + Facing.d[j1];
                    int j2 = this.b(enumskyblock, k1, l1, i2) - i1;

                    if (j2 > l) {
                        l = j2;
                    }

                    if (l >= 14) {
                        return l;
                    }
                }

                return l;
            }
        }
    }

    private Block getType(int i, int j, int k) {
        if (i >= -30000000 && k >= -30000000 && i < 30000000 && k < 30000000 && j >= 0 && j < 256) {
            Chunk chunk = this.getChunk(i >> 4, k >> 4);
            if(chunk != null) {
                return chunk.getType(i & 15, j, k & 15);
            }
        }
        return Blocks.AIR;
    }

    private boolean i(int i, int j, int k) {
        Chunk chunk = this.getChunk(i >> 4, k >> 4);
        if(chunk != null) {
            return chunk.d(i & 15, j, k & 15);
        }
        return true;
    }
}
