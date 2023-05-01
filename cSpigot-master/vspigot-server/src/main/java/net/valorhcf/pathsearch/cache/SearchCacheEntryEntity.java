package net.valorhcf.pathsearch.cache;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.PathEntity;
import org.bukkit.util.BlockVector;

public class SearchCacheEntryEntity extends SearchCacheEntry {

    private Entity target;

    public SearchCacheEntryEntity(EntityInsentient entity, Entity target, PathEntity path) {
        super(entity, path);
        this.target = target;
        this.positionTarget = this.getEntityPosition(this.target);
    }

    @Override
    public boolean isStillValid() {
        if(this.getCurrentTick() - this.tick > 20) {
            return false;
        }
        BlockVector bvStart = this.getEntityPosition(this.entity);
        BlockVector bvTarget = this.getEntityPosition(this.target);
        if(!bvStart.equals(this.positionStart) || !bvTarget.equals(this.positionTarget)) {
            return false;
        }
        return true;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        this.target = null;
    }
}