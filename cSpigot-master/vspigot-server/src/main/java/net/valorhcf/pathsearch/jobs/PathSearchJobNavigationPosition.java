package net.valorhcf.pathsearch.jobs;

import net.minecraft.server.EntityInsentient;
import net.valorhcf.pathsearch.AsyncPathfinder;
import net.valorhcf.pathsearch.PositionPathSearchType;
import net.valorhcf.pathsearch.cache.SearchCacheEntryPosition;

public class PathSearchJobNavigationPosition extends PathSearchJob {

    private int x, y, z;
    private PositionPathSearchType type;

    public PathSearchJobNavigationPosition(PositionPathSearchType type, EntityInsentient entity, int x, int y, int z, float range, boolean b1, boolean b2, boolean b3, boolean b4) {
        super(entity, range, b1, b2, b3, b4);
        this.type = type;
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
            this.entity.getNavigation().setSearchResult(this);
            this.cleanup();
        }
        return this;
    }

    public PositionPathSearchType getCacheEntryKey() {
        return this.type;
    }

    public SearchCacheEntryPosition getCacheEntryValue() {
        return new SearchCacheEntryPosition(this.entity, this.x, this.y, this.z, this.pathEntity);
    }

    @Override
    public int hashCode() {
        return this.type.hashCode() ^
                (this.getSearchHash() << 4);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || !(o instanceof PathSearchJobNavigationPosition)) {
            return false;
        }
        PathSearchJobNavigationPosition other = (PathSearchJobNavigationPosition) o;

        return this.type.equals(
                other.type) &&
                this.getSearchHash() ==
                other.getSearchHash();
    }

    @Override
    public void cancel() {
        this.entity.getNavigation().cancelSearch(this);
    }
}
