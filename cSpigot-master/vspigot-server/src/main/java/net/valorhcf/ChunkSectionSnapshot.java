package net.valorhcf;

import net.minecraft.server.NibbleArray;

public class ChunkSectionSnapshot {

    private final int nonEmptyBlockCount;
    private final int tickingBlockCount;
    private final byte[] blockIds;
    private final NibbleArray blockData;
    private final NibbleArray emittedLight;
    private final NibbleArray skyLight;
    private final int compactId;
    private final byte compactData;
    private final byte compactEmitted;
    private final byte compactSky;

    public ChunkSectionSnapshot(int nonEmptyBlockCount,
            int tickingBlockCount,
            byte[] blockIds,
            NibbleArray blockData,
            NibbleArray emittedLight,
            NibbleArray skyLight,
            int compactId,
            byte compactData,
            byte compactEmitted,
            byte compactSky) {
        this.nonEmptyBlockCount = nonEmptyBlockCount;
        this.tickingBlockCount = tickingBlockCount;
        this.blockIds = blockIds;
        this.blockData = blockData;
        this.emittedLight = emittedLight;
        this.skyLight = skyLight;
        this.compactId = compactId;
        this.compactData = compactData;
        this.compactEmitted = compactEmitted;
        this.compactSky = compactSky;
    }

    public final int getNonEmptyBlockCount() {
        return nonEmptyBlockCount;
    }

    public final int getTickingBlockCount() {
        return tickingBlockCount;
    }

    public final byte[] getBlockIds() {
        return blockIds;
    }

    public final NibbleArray getBlockData() {
        return blockData;
    }

    public final NibbleArray getEmittedLight() {
        return emittedLight;
    }

    public final NibbleArray getSkyLight() {
        return skyLight;
    }

    public final int getCompactId() {
        return compactId;
    }

    public final byte getCompactData() {
        return compactData;
    }

    public final byte getCompactEmitted() {
        return compactEmitted;
    }

    public final byte getCompactSky() {
        return compactSky;
    }
}
