package net.valorhcf.pathsearch.jobs;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityCreature;
import net.valorhcf.pathsearch.AsyncPathfinder;
import net.valorhcf.pathsearch.cache.SearchCacheEntry;
import net.valorhcf.pathsearch.cache.SearchCacheEntryEntity;

public class PathSearchJobEntity extends PathSearchJob {

    private Entity target;

    public PathSearchJobEntity(EntityCreature entity, Entity target, float range, boolean b1, boolean b2, boolean b3, boolean b4) {
        super(entity, range, b1, b2, b3, b4);
        this.target = target;
    }

    @Override
    public PathSearchJob call() throws Exception {
        if(!this.isEntityStillValid()) {
            this.cancel();
        } else if(!this.issued) {
            this.issued = true;
            this.pathEntity = (new AsyncPathfinder(this.chunkCache, this.b1, this.b2, this.b3, this.b4)).a(entity, this.target, this.range);
            ((EntityCreature) this.entity).setSearchResult(this, this.target, this.pathEntity);
            this.cleanup();
        }
        return this;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        this.target = null;
    }

    @Override
    public Object getCacheEntryKey() {
        return this.entity.getUniqueID();
    }

    @Override
    public SearchCacheEntry getCacheEntryValue() {
        return new SearchCacheEntryEntity(this.entity, this.target, this.pathEntity);
    }

    @Override
    public void cancel() {
        ((EntityCreature) this.entity).cancelSearch(this);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || !(o instanceof PathSearchJobEntity)) {
            return false;
        }
        return this.getSearchHash() == ((PathSearchJobEntity)o).getSearchHash();
    }
}
