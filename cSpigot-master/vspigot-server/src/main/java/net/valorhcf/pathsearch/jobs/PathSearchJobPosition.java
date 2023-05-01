package net.valorhcf.pathsearch.jobs;

import net.minecraft.server.EntityCreature;
import net.valorhcf.pathsearch.AsyncPathfinder;
import net.valorhcf.pathsearch.PositionPathSearchType;
import net.valorhcf.pathsearch.cache.SearchCacheEntry;
import net.valorhcf.pathsearch.cache.SearchCacheEntryPosition;

public class PathSearchJobPosition extends PathSearchJob {

    private int x, y, z;
    private PositionPathSearchType type;

    public PathSearchJobPosition(EntityCreature entity, int x, int y, int z, float range, boolean b1, boolean b2, boolean b3, boolean b4) {
        super(entity, range, b1, b2, b3, b4);
        this.type = PositionPathSearchType.ANYOTHER;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public PathSearchJob call() throws Exception {
        if(!this.isEntityStillValid()) {
            this.cancel();
        } else if(!this.issued) {
            this.issued = true;
            this.pathEntity = (new AsyncPathfinder(this.chunkCache, this.b1, this.b2, this.b3, this.b4)).a(entity, this.x, this.y, this.z, this.range);
            ((EntityCreature)this.entity).setSearchResult(this, this.pathEntity);
            this.cleanup();
        }
        return this;
    }

    @Override
    public Object getCacheEntryKey() {
        return this.type;
    }

    @Override
    public SearchCacheEntry getCacheEntryValue() {
        return new SearchCacheEntryPosition(this.entity, this.x, this.y, this.z, this.pathEntity);
    }

    @Override
    public int hashCode() {
        return this.type.hashCode() ^ (this.getSearchHash() << 4);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || !(o instanceof PathSearchJobPosition)) {
            return false;
        }
        PathSearchJobPosition other = (PathSearchJobPosition) o;

        return this.type.equals(
                other.type) &&
                this.getSearchHash() == other.getSearchHash();
    }

    @Override
    public void cancel() {
        ((EntityCreature) this.entity).cancelSearch(this);
    }
}
