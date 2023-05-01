package org.bukkit.craftbukkit;

import net.minecraft.server.*;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class CraftChunk implements Chunk {
    private WeakReference<net.minecraft.server.Chunk> weakChunk;
    private final WorldServer worldServer;
    private final int x;
    private final int z;
    private static final byte[] emptyData = new byte[2048];
    private static final short[] emptyBlockIDs = new short[4096];
    private static final byte[] emptySkyLight = new byte[2048];

    public CraftChunk(net.minecraft.server.Chunk chunk) {
        if (!(chunk instanceof EmptyChunk)) {
            this.weakChunk = new WeakReference<net.minecraft.server.Chunk>(chunk);
        }

        worldServer = (WorldServer) getHandle().world;
        x = getHandle().locX;
        z = getHandle().locZ;
    }

    public World getWorld() {
        return worldServer.getWorld();
    }

    public CraftWorld getCraftWorld() {
        return (CraftWorld) getWorld();
    }

    public net.minecraft.server.Chunk getHandle() {
        net.minecraft.server.Chunk c = weakChunk.get();

        if (c == null) {
            c = worldServer.getChunkAt(x, z);

            if (!(c instanceof EmptyChunk)) {
                weakChunk = new WeakReference<net.minecraft.server.Chunk>(c);
            }
        }

        return c;
    }

    void breakLink() {
        weakChunk.clear();
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "CraftChunk{" + "x=" + getX() + "z=" + getZ() + '}';
    }

    public Block getBlock(int x, int y, int z) {
        return new CraftBlock(this, (getX() << 4) | (x & 0xF), y & 0xFF, (getZ() << 4) | (z & 0xF));
    }

    public Entity[] getEntities() {
        int count = 0, index = 0;
        net.minecraft.server.Chunk chunk = getHandle();

        for (int i = 0; i < 16; i++) {
            count += chunk.entitySlices[i].size();
        }

        Entity[] entities = new Entity[count];

        for (int i = 0; i < 16; i++) {
            for (Object obj : chunk.entitySlices[i].toArray()) {
                if (!(obj instanceof net.minecraft.server.Entity)) {
                    continue;
                }

                entities[index++] = ((net.minecraft.server.Entity) obj).getBukkitEntity();
            }
        }

        return entities;
    }

    public BlockState[] getTileEntities() {
        int index = 0;
        net.minecraft.server.Chunk chunk = getHandle();
        BlockState[] entities = new BlockState[chunk.tileEntities.size()];

        for (Object obj : chunk.tileEntities.keySet().toArray()) {
            if (!(obj instanceof ChunkPosition)) {
                continue;
            }

            ChunkPosition position = (ChunkPosition) obj;
            entities[index++] = worldServer.getWorld().getBlockAt(position.x + (chunk.locX << 4), position.y, position.z + (chunk.locZ << 4)).getState();
        }
        return entities;
    }

    public boolean isLoaded() {
        return getWorld().isChunkLoaded(this);
    }

    public boolean load() {
        return getWorld().loadChunk(getX(), getZ(), true);
    }

    public boolean load(boolean generate) {
        return getWorld().loadChunk(getX(), getZ(), generate);
    }

    public boolean unload() {
        return getWorld().unloadChunk(getX(), getZ());
    }

    public boolean unload(boolean save) {
        return getWorld().unloadChunk(getX(), getZ(), save);
    }

    public boolean unload(boolean save, boolean safe) {
        return getWorld().unloadChunk(getX(), getZ(), save, safe);
    }

    public ChunkSnapshot getChunkSnapshot() {
        return getChunkSnapshot(true, false, false);
    }

    public ChunkSnapshot getChunkSnapshot(boolean includeMaxBlockY, boolean includeBiome, boolean includeBiomeTempRain) {
        net.minecraft.server.Chunk chunk = getHandle();

        ChunkSection[] cs = chunk.getSections();
        short[][] sectionBlockIDs = new short[cs.length][];
        byte[][] sectionBlockData = new byte[cs.length][];
        byte[][] sectionSkyLights = new byte[cs.length][];
        byte[][] sectionEmitLights = new byte[cs.length][];
        boolean[] sectionEmpty = new boolean[cs.length];

        for (int i = 0; i < cs.length; i++) {
            if (cs[i] == null) { /* Section is empty? */
                sectionBlockIDs[i] = emptyBlockIDs;
                sectionBlockData[i] = emptyData;
                sectionSkyLights[i] = emptySkyLight;
                sectionEmitLights[i] = emptyData;
                sectionEmpty[i] = true;
            } else { /* Not empty */
                short[] blockids = new short[4096];
                byte[] baseids = cs[i].getIdArray();

                /* Copy base IDs */
                for (int j = 0; j < 4096; j++) {
                    blockids[j] = (short) (baseids[j] & 0xFF);
                }

                // MineHQ start - 1.7 has no extended block IDs
                /*
                if (cs[i].getExtendedIdArray() != null) { /* If we've got extended IDs *//*
                    byte[] extids = cs[i].getExtendedIdArray().a;

                    for (int j = 0; j < 2048; j++) {
                        short b = (short) (extids[j] & 0xFF);

                        if (b == 0) {
                            continue;
                        }

                        blockids[j<<1] |= (b & 0x0F) << 8;
                        blockids[(j<<1)+1] |= (b & 0xF0) << 4;
                    }
                }
                */
                // MineHQ end
                sectionBlockIDs[i] = blockids;

                /* Get block data nibbles */
                sectionBlockData[i] = new byte[2048];
                System.arraycopy(cs[i].getDataArray().a, 0, sectionBlockData[i], 0, 2048);
                if (cs[i].getSkyLightArray() == null) {
                    sectionSkyLights[i] = emptyData;
                } else {
                    sectionSkyLights[i] = new byte[2048];
                    System.arraycopy(cs[i].getSkyLightArray().a, 0, sectionSkyLights[i], 0, 2048);
                }
                sectionEmitLights[i] = new byte[2048];
                System.arraycopy(cs[i].getEmittedLightArray().a, 0, sectionEmitLights[i], 0, 2048);
            }
        }

        int[] hmap = null;

        if (includeMaxBlockY) {
            hmap = new int[256]; // Get copy of height map
            System.arraycopy(chunk.heightMap, 0, hmap, 0, 256);
        }

        BiomeBase[] biome = null;
        double[] biomeTemp = null;
        double[] biomeRain = null;

        if (includeBiome || includeBiomeTempRain) {
            WorldChunkManager wcm = chunk.world.getWorldChunkManager();

            if (includeBiome) {
                biome = new BiomeBase[256];
                for (int i = 0; i < 256; i++) {
                    biome[i] = chunk.getBiome(i & 0xF, i >> 4, wcm);
                }
            }

            if (includeBiomeTempRain) {
                biomeTemp = new double[256];
                biomeRain = new double[256];
                float[] dat = getTemperatures(wcm, getX() << 4, getZ() << 4);

                for (int i = 0; i < 256; i++) {
                    biomeTemp[i] = dat[i];
                }

                dat = wcm.getWetness(null, getX() << 4, getZ() << 4, 16, 16);

                for (int i = 0; i < 256; i++) {
                    biomeRain[i] = dat[i];
                }
            }
        }

        World world = getWorld();
        return new CraftChunkSnapshot(getX(), getZ(), world.getName(), world.getFullTime(), sectionBlockIDs, sectionBlockData, sectionSkyLights, sectionEmitLights, sectionEmpty, hmap, biome, biomeTemp, biomeRain);
    }

    public static ChunkSnapshot getEmptyChunkSnapshot(int x, int z, CraftWorld world, boolean includeBiome, boolean includeBiomeTempRain) {
        BiomeBase[] biome = null;
        double[] biomeTemp = null;
        double[] biomeRain = null;

        if (includeBiome || includeBiomeTempRain) {
            WorldChunkManager wcm = world.getHandle().getWorldChunkManager();

            if (includeBiome) {
                biome = new BiomeBase[256];
                for (int i = 0; i < 256; i++) {
                    biome[i] = world.getHandle().getBiome((x << 4) + (i & 0xF), (z << 4) + (i >> 4));
                }
            }

            if (includeBiomeTempRain) {
                biomeTemp = new double[256];
                biomeRain = new double[256];
                float[] dat = getTemperatures(wcm, x << 4, z << 4);

                for (int i = 0; i < 256; i++) {
                    biomeTemp[i] = dat[i];
                }

                dat = wcm.getWetness(null, x << 4, z << 4, 16, 16);

                for (int i = 0; i < 256; i++) {
                    biomeRain[i] = dat[i];
                }
            }
        }

        /* Fill with empty data */
        int hSection = world.getMaxHeight() >> 4;
        short[][] blockIDs = new short[hSection][];
        byte[][] skyLight = new byte[hSection][];
        byte[][] emitLight = new byte[hSection][];
        byte[][] blockData = new byte[hSection][];
        boolean[] empty = new boolean[hSection];

        for (int i = 0; i < hSection; i++) {
            blockIDs[i] = emptyBlockIDs;
            skyLight[i] = emptySkyLight;
            emitLight[i] = emptyData;
            blockData[i] = emptyData;
            empty[i] = true;
        }

        return new CraftChunkSnapshot(x, z, world.getName(), world.getFullTime(), blockIDs, blockData, skyLight, emitLight, empty, new int[256], biome, biomeTemp, biomeRain);
    }

    private static float[] getTemperatures(WorldChunkManager chunkmanager, int chunkX, int chunkZ) {
        BiomeBase[] biomes = chunkmanager.getBiomes(null, chunkX, chunkZ, 16, 16);
        float[] temps = new float[biomes.length];

        for (int i = 0; i < biomes.length; i++) {
            float temp = biomes[i].temperature; // Vanilla of olde: ((int) biomes[i].temperature * 65536.0F) / 65536.0F

            if (temp > 1F) {
                temp = 1F;
            }

            temps[i] = temp;
        }

        return temps;
    }

    static {
        Arrays.fill(emptySkyLight, (byte) 0xFF);
    }

    // MineHQ start - chunk snapshot api
    @Override
    public net.valorhcf.ChunkSnapshot takeSnapshot() {
        net.minecraft.server.Chunk handle = getHandle();
        net.valorhcf.CraftChunkSnapshot snap = new net.valorhcf.CraftChunkSnapshot();

        // save chunk sections to snapshot
        for (int i = 0; i < 16; i++) {
            if (handle.getSections()[i] != null) {
                snap.getSections()[i] = handle.getSections()[i].createSnapshot();
            }
        }

        // save tile entities to snapshot
        for (Map.Entry<ChunkPosition, TileEntity> entry : handle.tileEntities.entrySet()) {
            NBTTagCompound nbt = new NBTTagCompound();
            entry.getValue().b(nbt); // writeToNBT
            snap.getTileEntities().add(nbt);
        }
        return snap;
    }

    @Override
    public void restoreSnapshot(net.valorhcf.ChunkSnapshot snapshot) {
        net.valorhcf.CraftChunkSnapshot snap = (net.valorhcf.CraftChunkSnapshot) snapshot;
        net.minecraft.server.Chunk handle = getHandle();

        // add chunk sections from snapshot
        for (int i = 0; i < 16; i++) {
            if (snap.getSections()[i] == null) {
                handle.getSections()[i] = null;
            } else {
                handle.getSections()[i] = new ChunkSection(i << 4, !worldServer.worldProvider.g);
                handle.getSections()[i].restoreSnapshot(snap.getSections()[i]);
            }
        }

        // clear tile entities currently in the chunk
        for (TileEntity tileEntity : handle.tileEntities.values()) {
            if (tileEntity instanceof IInventory) {
                for (HumanEntity h : new ArrayList<HumanEntity>(((IInventory) tileEntity).getViewers())) {
                    if (h instanceof CraftHumanEntity) {
                        ((CraftHumanEntity) h).getHandle().closeInventory();
                    }
                }
            }
            worldServer.a(tileEntity);
        }
        handle.tileEntities.clear();

        // add tile entities from snapshot
        for (NBTTagCompound nbt : snap.getTileEntities()) {
            // deserialize nbt to new tile entity instance
            TileEntity tileEntity = TileEntity.c(nbt);
            // move the tile entity into this chunk's space
            tileEntity.x = (tileEntity.x & 15) | handle.locX << 4;
            tileEntity.z = (tileEntity.z & 15) | handle.locZ << 4;
            // add it
            handle.a(tileEntity);
        }
        handle.n = true; // needs saving flag
        worldServer.getPlayerChunkMap().resend(x, z);
    }
    // MineHQ end
}
