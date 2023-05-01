package net.valorhcf.pathsearch.jobs;

import net.minecraft.server.EntityInsentient;
import net.minecraft.server.MathHelper;
import net.minecraft.server.PathEntity;
import net.valorhcf.WeakChunkCache;
import net.valorhcf.pathsearch.cache.SearchCacheEntry;

import java.util.concurrent.Callable;

public abstract class PathSearchJob implements Callable<PathSearchJob> {

    protected EntityInsentient entity;
    protected WeakChunkCache chunkCache;
    protected boolean issued;
    protected float range;
    protected boolean b1, b2, b3, b4;
    protected PathEntity pathEntity;
    protected int hash;

    public PathSearchJob(EntityInsentient entity, float range, boolean b1, boolean b2, boolean b3, boolean b4) {
        this.entity = entity;
        this.range = range;
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
        this.b4 = b4;
        this.issued = false;
        this.hash = entity.getUniqueID().hashCode();
        this.createChunkCache();
    }

    private void createChunkCache() {
        int x = MathHelper.floor(this.entity.locX);
        int y = MathHelper.floor(this.entity.locY);
        int z = MathHelper.floor(this.entity.locZ);
        int radius = (int) (this.range + 8.0F);
        int xMinor = x - radius;
        int yMinor = y - radius;
        int zMinor = z - radius;
        int xMajor = x + radius;
        int yMajor = y + radius;
        int zMajor = z + radius;
        this.chunkCache = new WeakChunkCache(this.entity.world, xMinor, yMinor, zMinor, xMajor, yMajor, zMajor, 0);
    }

    public void cleanup() {
        this.entity = null;
        this.chunkCache = null;
        this.pathEntity = null;
    }

    public int getSearchHash() {
        return this.hash;
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    public abstract Object getCacheEntryKey();

    public abstract SearchCacheEntry getCacheEntryValue();

    public abstract void cancel();

    protected boolean isEntityStillValid() {
        return this.entity != null && this.entity.valid && this.entity.isAlive();
    }
}
