package net.valorhcf.pathsearch.cache;

import net.minecraft.server.*;
import org.bukkit.util.BlockVector;

public class SearchCacheEntry {
    protected long tick;
    protected BlockVector positionStart;
    protected BlockVector positionTarget;
    protected EntityInsentient entity;
    private PathEntity path;

    public SearchCacheEntry(EntityInsentient entity, PathEntity path) {
        this.entity = entity;
        this.positionStart = this.getEntityPosition(this.entity);
        this.path = path;
        this.tick = this.getCurrentTick();
    }

    protected int getCurrentTick() {
        return MinecraftServer.getServer().al();
    }

    protected BlockVector getEntityPosition(Entity entity) {
        return new BlockVector(entity.locX, entity.locY, entity.locZ);
    }

    protected BlockVector getTargetPosition(int x, int y, int z) {
        return new BlockVector(x, y, z);
    }

    public boolean isStillValid() {
        return false;
    }

    public PathEntity getPathEntity() {
        return this.path;
    }

    public boolean hasExpired() {
        return !this.entity.isAlive() || (this.getCurrentTick() - this.tick) > 200;
    }

    public boolean didSearchSucceed() {
        return this.path != null;
    }

    public boolean shouldBeRefreshed() {
        return (this.getCurrentTick() - this.tick) > 5;
    }

    public PathEntity getAdjustedPathEntity() {
        if(this.path != null && (this.path.e() < this.path.d() - 1)) {
            PathPoint pathpoint = this.path.a(this.path.e());
            double currentDist = this.entity.e(pathpoint.a, pathpoint.b, pathpoint.c);
            while(this.path.e() < this.path.d() - 1) {
                pathpoint = this.path.a(this.path.e() + 1);
                double nextDist = this.entity.e(pathpoint.a, pathpoint.b, pathpoint.c);
                if(nextDist < currentDist) {
                    currentDist = nextDist;
                    this.path.a();
                } else {
                    break;
                }
            }
        }
        return this.path;
    }

    public void cleanup() {
        this.positionStart = null;
        this.positionTarget = null;
        this.entity = null;
        this.path = null;
    }
}
