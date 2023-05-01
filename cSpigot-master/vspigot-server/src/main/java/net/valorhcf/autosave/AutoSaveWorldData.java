package net.valorhcf.autosave;

import net.minecraft.server.World;

public class AutoSaveWorldData {

    private long lastAutoSaveTimeStamp;
    private int autoSaveChunkCount;
    private final World world;

    public AutoSaveWorldData(World world) {
        this.world = world;
        this.setLastAutosaveTimeStamp();
    }

    public void setLastAutosaveTimeStamp() {
        this.lastAutoSaveTimeStamp = this.world.worldData.getTime();
        this.autoSaveChunkCount = 0;
    }

    public long getLastAutosaveTimeStamp() {
        return this.lastAutoSaveTimeStamp;
    }

    public void addAutoSaveChunkCount(int count) {
        this.autoSaveChunkCount += count;
    }

    public int getAutoSaveChunkCount() {
        return this.autoSaveChunkCount;
    }
}
