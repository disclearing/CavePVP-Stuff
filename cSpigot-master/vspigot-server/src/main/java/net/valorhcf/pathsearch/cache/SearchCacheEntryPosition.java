package net.valorhcf.pathsearch.cache;

import net.minecraft.server.EntityInsentient;
import net.minecraft.server.PathEntity;
import org.bukkit.util.BlockVector;

public class SearchCacheEntryPosition extends SearchCacheEntry {

    public SearchCacheEntryPosition(EntityInsentient entity, int x, int y, int z, PathEntity path) {
        super(entity, path);
        this.positionTarget = this.getTargetPosition(x, y, z);
    }

    @Override
    public boolean isStillValid() {
        if(this.getCurrentTick() - this.tick > 20) {
            return false;
        }
        BlockVector bvStart = this.getEntityPosition(this.entity);
        if(!bvStart.equals(this.positionStart)) {
            return false;
        }
        return true;
    }

    public boolean targetEquals(BlockVector bv) {
        return this.positionTarget.equals(bv);
    }
}