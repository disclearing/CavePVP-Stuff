package net.valorhcf.pathsearch;

import net.minecraft.server.*;
import net.valorhcf.pathsearch.cache.SearchCacheEntry;
import net.valorhcf.pathsearch.cache.SearchCacheEntryEntity;
import net.valorhcf.pathsearch.cache.SearchCacheEntryPosition;
import net.valorhcf.pathsearch.jobs.PathSearchJob;
import net.valorhcf.pathsearch.jobs.PathSearchJobNavigationEntity;
import net.valorhcf.pathsearch.jobs.PathSearchJobNavigationPosition;
import net.valorhcf.pathsearch.jobs.PathSearchQueuingManager;
import org.bukkit.util.BlockVector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

public class AsyncNavigation extends Navigation {

    private HashMap<UUID, SearchCacheEntry> searchCache;
    private HashMap<PositionPathSearchType, SearchCacheEntryPosition> positionSearchCache;
    private static double minimumDistanceForOffloadingSquared = 0.0D;
    private int cleanUpDelay = 0;
    private PathSearchQueuingManager queuingManager;

    public AsyncNavigation(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
        this.searchCache = new HashMap<UUID, SearchCacheEntry>();
        this.positionSearchCache = new HashMap<PositionPathSearchType, SearchCacheEntryPosition>();
        this.queuingManager = new PathSearchQueuingManager();
    }

    private BlockVector createBlockVectorForPosition(int x, int y, int z) {
        return new BlockVector(x, y, z);
    }

    private void issueSearch(Entity target, float range, boolean j, boolean k, boolean l, boolean m) {
        this.queuingManager.queueSearch(new PathSearchJobNavigationEntity(this.a, target, range, j, k, l, m));
    }

    private void issueSearch(PositionPathSearchType type, int x, int y, int z, float range, boolean j, boolean k, boolean l, boolean m) {
        this.queuingManager.queueSearch(new PathSearchJobNavigationPosition(type, this.a, x, y, z, range, j, k, l, m));
    }

    @Override
    public void cancelSearch(PathSearchJob pathSearch) {
        this.queuingManager.checkLastSearchResult(pathSearch);
        pathSearch.cleanup();
    }

    @Override
    public void setSearchResult(PathSearchJobNavigationEntity pathSearch) {
        this.queuingManager.checkLastSearchResult(pathSearch);
        SearchCacheEntry entry = pathSearch.getCacheEntryValue();
        if(entry != null && entry.didSearchSucceed()) {
            synchronized(this.searchCache) {
                UUID key = pathSearch.getCacheEntryKey();
                this.searchCache.put(key, entry);
            }
        }
    }

    @Override
    public void setSearchResult(PathSearchJobNavigationPosition pathSearch) {
        this.queuingManager.checkLastSearchResult(pathSearch);
        SearchCacheEntryPosition entry = pathSearch.getCacheEntryValue();
        if(entry != null && entry.didSearchSucceed()) {
            synchronized(this.positionSearchCache) {
                PositionPathSearchType key = pathSearch.getCacheEntryKey();
                this.positionSearchCache.put(key, entry);
            }
        }
    }

    @Override
    public PathEntity a(double d0, double d1, double d2) {
        return this.a(PositionPathSearchType.ANYOTHER, d0, d1, d2);
    }

    @Override
    public boolean a(PositionPathSearchType type, double d0, double d1, double d2, double d3) {
        PathEntity pathentity = this.a(type, (double) MathHelper.floor(d0), (double) ((int) d1), (double) MathHelper.floor(d2));

        return this.a(pathentity, d3);
    }

    public void cleanUpExpiredSearches() {
        if(++this.cleanUpDelay > 100) {
            this.cleanUpDelay = 0;
            synchronized(this.searchCache) {
                Iterator<Entry<UUID, SearchCacheEntry>> iter = this.searchCache.entrySet().iterator();
                while(iter.hasNext()) {
                    Entry<UUID, SearchCacheEntry> entry = iter.next();
                    if(entry.getValue().hasExpired()) {
                        iter.remove();
                    }
                }
            }
            synchronized(this.positionSearchCache) {
                Iterator<Entry<PositionPathSearchType, SearchCacheEntryPosition>> iter2 = this.positionSearchCache.entrySet().iterator();
                while(iter2.hasNext()) {
                    Entry<PositionPathSearchType, SearchCacheEntryPosition> entry = iter2.next();
                    if(entry.getValue().hasExpired()) {
                        iter2.remove();
                    }
                }
            }
        }
    }

    @Override
    public PathEntity a(Entity entity) {
        if(!this.offloadSearches() || this.a.f(entity) < minimumDistanceForOffloadingSquared) {
            return super.a(entity);
        }
        if(!this.l()) {
            return null;
        }
        SearchCacheEntry entry = null;
        UUID id = entity.getUniqueID();
        synchronized(this.searchCache) {
            if(this.searchCache.containsKey(id)) {
                entry = this.searchCache.get(id);
            }
        }
        PathEntity resultPath = null;
        if(entry != null) {
            resultPath = entry.getAdjustedPathEntity();
            if(!entry.isStillValid()) {
                this.issueSearch(entity, this.d(), this.j, this.k, this.l, this.m);
            }
        }
        if(entry == null && !this.queuingManager.hasAsyncSearchIssued()) {
            resultPath = super.a(entity);
            if(resultPath != null) {
                entry = new SearchCacheEntryEntity(this.a, entity, resultPath);
                synchronized(this.searchCache) {
                    SearchCacheEntry oldEntry = this.searchCache.put(id, entry);
                    if(oldEntry != null) {
                        oldEntry.cleanup();
                    }
                }
            }
        }
        return resultPath;
    }

    @Override
    public PathEntity a(PositionPathSearchType type, double d0, double d1, double d2) {
        if(!this.offloadSearches() || this.a.e(d0, d1, d2) < minimumDistanceForOffloadingSquared) {
            return super.a(d0, d1, d2);
        }
        if(!this.l()) {
            return null;
        }

        int x = MathHelper.floor(d0);
        int y = (int) d1;
        int z = MathHelper.floor(d2);

        SearchCacheEntryPosition entry = null;
        synchronized(this.positionSearchCache) {
            if(this.positionSearchCache.containsKey(type)) {
                entry = this.positionSearchCache.get(type);
            }
        }

        PathEntity resultPath = null;
        if(entry != null) {
            resultPath = entry.getAdjustedPathEntity();
            if(!entry.isStillValid()) {
                this.issueSearch(type, x, y, z, this.d(), this.j, this.k, this.l, this.m);
            }
        }
        if(entry == null && !this.queuingManager.hasAsyncSearchIssued()) {
            resultPath = super.a(d0, d1, d2);
            if(resultPath != null) {
                entry = new SearchCacheEntryPosition(this.a, x, y, z, resultPath);
                synchronized(this.positionSearchCache) {
                    SearchCacheEntry oldEntry = this.positionSearchCache.put(type, entry);
                    if(oldEntry != null) {
                        oldEntry.cleanup();
                    }
                }
            }
        }
        return resultPath;
    }

    private boolean offloadSearches() {
        return true;
        //return Migot.getConfig().isPathSearchOffloadedFor(this.b);
    }
}
