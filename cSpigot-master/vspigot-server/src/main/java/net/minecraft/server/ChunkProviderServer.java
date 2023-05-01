package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Server;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.util.LongHash;
import org.bukkit.craftbukkit.util.LongHashSet;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ChunkProviderServer implements IChunkProvider {

    private static final Logger b = LogManager.getLogger();
    // CraftBukkit start - private -> public
    public LongHashSet unloadQueue = new LongHashSet(); // LongHashSet
    public Chunk emptyChunk;
    public IChunkProvider chunkProvider;
    private IChunkLoader f;
    public boolean forceChunkLoad = false; // true -> false
    //public LongObjectHashMap<Chunk> chunks = new LongObjectHashMap<Chunk>();
    public Long2ObjectOpenHashMap<Chunk> chunks = new Long2ObjectOpenHashMap<>(); // MineHQ
    public WorldServer world;
    // CraftBukkit end

    public ChunkProviderServer(WorldServer worldserver, IChunkLoader ichunkloader, IChunkProvider ichunkprovider) {
        this.emptyChunk = new EmptyChunk(worldserver, Integer.MIN_VALUE, Integer.MIN_VALUE); // Poweruser
        this.world = worldserver;
        this.f = ichunkloader;
        this.chunkProvider = ichunkprovider;
    }

    public boolean chunkExists(int i, int j) {
        return ((ChunkRegionLoader) this.f).chunkExists(this.world, i, j);
    }

    public boolean isChunkLoaded(int i, int j) {
        return this.chunks.containsKey(LongHash.toLong(i, j)); // CraftBukkit // MineHQ
    }

    // CraftBukkit start - Change return type to Collection and return the values of our chunk map
    public java.util.Collection a() {
        // return this.chunkList;
        return this.chunks.values();
        // CraftBukkit end
    }

    // MineHQ start
    public void queueUnload(int x, int z) {
        queueUnload(x, z, false);
    }

    public void queueUnload(int i, int j, boolean checked) {
        if (!checked && this.world.getPlayerChunkMap().isChunkInUse(i, j)) return;
    // MineHQ end
        // PaperSpigot start - Asynchronous lighting updates
        Chunk chunk = this.chunks.get(LongHash.toLong(i, j)); // MineHQ
        if (chunk != null && chunk.world.paperSpigotConfig.useAsyncLighting && (chunk.pendingLightUpdates.get() > 0 || chunk.world.getTime() - chunk.lightUpdateTime < 20)) {
            return;
        }
        // PaperSpigot end
        // PaperSpigot start - Don't unload chunk if it contains an entity that loads chunks
        if (chunk != null) {
            for (List<Entity> entities : chunk.entitySlices) {
                for (Entity entity : entities) {
                    if (entity.loadChunks) {
                        return;
                    }
                }
            }
        }
        // PaperSpigot end
        if (this.world.worldProvider.e()) {
            ChunkCoordinates chunkcoordinates = this.world.getSpawn();
            int k = i * 16 + 8 - chunkcoordinates.x;
            int l = j * 16 + 8 - chunkcoordinates.z;
            short short1 = 128;

            // CraftBukkit start
            if (k < -short1 || k > short1 || l < -short1 || l > short1 || !(this.world.keepSpawnInMemory)) { // Added 'this.world.keepSpawnInMemory'
                this.unloadQueue.add(i, j);

                // MineHQ start - don't lookup twice
                if (chunk != null) {
                    chunk.mustSave = true;
                }
                // MineHQ end
            }
            // CraftBukkit end
        } else {
            // CraftBukkit start
            this.unloadQueue.add(i, j);

            // MineHQ start - don't lookup twice
            if (chunk != null) {
                chunk.mustSave = true;
            }
            // MineHQ end
            // CraftBukkit end
        }
    }

    public void b() {
        Iterator iterator = this.chunks.values().iterator(); // CraftBukkit

        while (iterator.hasNext()) {
            Chunk chunk = (Chunk) iterator.next();

            this.queueUnload(chunk.locX, chunk.locZ);
        }
    }

    // CraftBukkit start - Add async variant, provide compatibility
    public Chunk getChunkIfLoaded(int x, int z) {
        return this.chunks.get(LongHash.toLong(x, z)); // MineHQ
    }

    public Chunk getChunkAt(int i, int j) {
        return getChunkAt(i, j, null);
    }

    public Chunk getChunkAt(int i, int j, Runnable runnable) {
        this.unloadQueue.remove(i, j);
        Chunk chunk = this.chunks.get(LongHash.toLong(i, j)); // MineHQ
        ChunkRegionLoader loader = null;

        if (this.f instanceof ChunkRegionLoader) {
            loader = (ChunkRegionLoader) this.f;
        }

        // We can only use the queue for already generated chunks
        if (chunk == null && loader != null && loader.chunkExists(this.world, i, j)) {
            if (runnable != null) {
                ChunkIOExecutor.queueChunkLoad(this.world, loader, this, i, j, runnable);
                return null;
            } else {
                chunk = ChunkIOExecutor.syncChunkLoad(this.world, loader, this, i, j);
            }
        } else if (chunk == null) {
            chunk = this.originalGetChunkAt(i, j);
        }

        // If we didn't load the chunk async and have a callback run it now
        if (runnable != null) {
            runnable.run();
        }

        return chunk;
    }

    public Chunk originalGetChunkAt(int i, int j) {
        this.unloadQueue.remove(i, j);
        Chunk chunk = (Chunk) this.chunks.get(LongHash.toLong(i, j)); // MineHQ
        boolean newChunk = false;

        if (chunk == null) {
            world.timings.syncChunkLoadTimer.startTiming(); // Spigot
            chunk = this.loadChunk(i, j);
            if (chunk == null) {
                if (this.chunkProvider == null) {
                    chunk = this.emptyChunk;
                } else {
                    try {
                        chunk = this.chunkProvider.getOrCreateChunk(i, j);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.a(throwable, "Exception generating new chunk");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Chunk to be generated");

                        crashreportsystemdetails.a("Location", String.format("%d,%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j)}));
                        crashreportsystemdetails.a("Position hash", Long.valueOf(LongHash.toLong(i, j))); // CraftBukkit - Use LongHash
                        crashreportsystemdetails.a("Generator", this.chunkProvider.getName());
                        throw new ReportedException(crashreport);
                    }
                }
                newChunk = true; // CraftBukkit
            }

            this.chunks.put(LongHash.toLong(i, j), chunk); // CraftBukkit // MineHQ
            chunk.addEntities();

            // CraftBukkit start
            Server server = this.world.getServer();
            if (server != null) {
                /*
                 * If it's a new world, the first few chunks are generated inside
                 * the World constructor. We can't reliably alter that, so we have
                 * no way of creating a CraftWorld/CraftServer at that point.
                 */
                server.getPluginManager().callEvent(new org.bukkit.event.world.ChunkLoadEvent(chunk.bukkitChunk, newChunk));
            }

            // Update neighbor counts
            for (int x = -2; x < 3; x++) {
                for (int z = -2; z < 3; z++) {
                    if (x == 0 && z == 0) {
                        continue;
                    }

                    Chunk neighbor = this.getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
                    if (neighbor != null) {
                        neighbor.setNeighborLoaded(-x, -z);
                        chunk.setNeighborLoaded(x, z);
                    }
                }
            }
            // CraftBukkit end
            chunk.loadNearby(this, this, i, j);
            world.timings.syncChunkLoadTimer.stopTiming(); // Spigot
        }

        return chunk;
    }

    public Chunk getOrCreateChunk(int i, int j) {
        // CraftBukkit start
        Chunk chunk = (Chunk) this.chunks.get(LongHash.toLong(i, j)); // MineHQ

        chunk = chunk == null ? (!this.world.isLoading && !this.forceChunkLoad ? this.emptyChunk : this.getChunkAt(i, j)) : chunk;
        if (chunk == this.emptyChunk) return chunk;
        if (i != chunk.locX || j != chunk.locZ) {
            b.error("Chunk (" + chunk.locX + ", " + chunk.locZ + ") stored at  (" + i + ", " + j + ") in world '" + world.getWorld().getName() + "'");
            b.error(chunk.getClass().getName());
            Throwable ex = new Throwable();
            ex.fillInStackTrace();
            ex.printStackTrace();
        }
        return chunk;
        // CraftBukkit end
    }

    public Chunk loadChunk(int i, int j) { // CraftBukkit - private -> public
        if (this.f == null) {
            return null;
        } else {
            try {
                Chunk chunk = this.f.a(this.world, i, j);

                if (chunk != null) {
                    chunk.lastSaved = this.world.getTime();
                    if (this.chunkProvider != null) {
                        world.timings.syncChunkLoadStructuresTimer.startTiming(); // Spigot
                        this.chunkProvider.recreateStructures(i, j);
                        world.timings.syncChunkLoadStructuresTimer.stopTiming(); // Spigot
                    }
                }

                return chunk;
            } catch (Exception exception) {
                b.error("Couldn\'t load chunk", exception);
                return null;
            }
        }
    }

    public void saveChunkNOP(Chunk chunk) { // CraftBukkit - private -> public
        if (this.f != null) {
            try {
                this.f.b(this.world, chunk);
            } catch (Exception exception) {
                b.error("Couldn\'t save entities", exception);
            }
        }
    }

    public void saveChunk(Chunk chunk) { // CraftBukkit - private -> public
        if (this.f != null) {
            try {
                chunk.lastSaved = this.world.getTime();
                this.f.a(this.world, chunk);
                // CraftBukkit start - IOException to Exception
            } catch (Exception ioexception) {
                b.error("Couldn\'t save chunk", ioexception);
                /* Remove extra exception
            } catch (ExceptionWorldConflict exceptionworldconflict) {
                b.error("Couldn\'t save chunk; already in use by another instance of Minecraft?", exceptionworldconflict);
                // CraftBukkit end */
            }
        }
    }

    public void getChunkAt(IChunkProvider ichunkprovider, int i, int j) {
        Chunk chunk = this.getOrCreateChunk(i, j);

        if (!chunk.done) {
            chunk.p();
            if (this.chunkProvider != null) {
                this.chunkProvider.getChunkAt(ichunkprovider, i, j);

                // CraftBukkit start
                BlockSand.instaFall = true;
                Random random = new Random();
                random.setSeed(world.getSeed());
                long xRand = random.nextLong() / 2L * 2L + 1L;
                long zRand = random.nextLong() / 2L * 2L + 1L;
                random.setSeed((long) i * xRand + (long) j * zRand ^ world.getSeed());

                org.bukkit.World world = this.world.getWorld();
                if (world != null) {
                    this.world.populating = true;
                    try {
                        for (org.bukkit.generator.BlockPopulator populator : world.getPopulators()) {
                            populator.populate(world, random, chunk.bukkitChunk);
                        }
                    } finally {
                        this.world.populating = false;
                    }
                }
                BlockSand.instaFall = false;
                this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.world.ChunkPopulateEvent(chunk.bukkitChunk));
                // CraftBukkit end

                chunk.e();
            }
        }
    }

    public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
        int i = 0;
        // CraftBukkit start
        Iterator iterator = this.chunks.values().iterator();

        while (iterator.hasNext()) {
            Chunk chunk = (Chunk) iterator.next();
            // CraftBukkit end

            if (flag) {
                this.saveChunkNOP(chunk);
            }

            if (chunk.a(flag)) {
                this.saveChunk(chunk);
                chunk.n = false;
                ++i;
                // Poweruser start
                if (i >= org.spigotmc.SpigotConfig.autoSaveChunksPerTick && !flag) {
                    this.world.getAutoSaveWorldData().addAutoSaveChunkCount(i);
                // Poweruser end
                    return false;
                }
            }
        }
        // Poweruser start
        if(!flag) {
            this.world.getAutoSaveWorldData().addAutoSaveChunkCount(i);
        }
        // Poweruser end

        return true;
    }

    public void c() {
        if (this.f != null) {
            this.f.b();
        }
    }


    public boolean unloadChunks() {
        if (!this.world.savingDisabled) {
            int chunksSize = this.chunks.size();
            int unloadSize = this.unloadQueue.size();
            int unloaded = 0;
            long start = System.currentTimeMillis();
            long nanoStart = System.nanoTime();
            long unloadQueuePopTotal = 0, chunksGet = 0, callEvent = 0, removeEntities = 0, saveChunk = 0, saveChunkNOP = 0, chunkRemove = 0, updateNeighbourCounts = 0;
            // CraftBukkit start
            Server server = this.world.getServer();
            for (int i = 0; i < 100 && !this.unloadQueue.isEmpty() && (System.currentTimeMillis() - start) < 150; i++) {
                nanoStart = System.nanoTime();
                long chunkcoordinates = this.unloadQueue.popFirst();
                unloadQueuePopTotal += System.nanoTime() - nanoStart;
                // MineHQ start
                int locX = LongHash.msw(chunkcoordinates);
                int locZ = LongHash.lsw(chunkcoordinates);
                nanoStart = System.nanoTime();
                Chunk chunk = this.chunks.get(LongHash.toLong(locX, locZ));
                chunksGet += System.nanoTime() - nanoStart;
                // MineHQ end
                if (chunk == null) continue;

                ChunkUnloadEvent event = new ChunkUnloadEvent(chunk.bukkitChunk);
                nanoStart = System.nanoTime();
                server.getPluginManager().callEvent(event);
                callEvent += System.nanoTime() - nanoStart;
                if (!event.isCancelled()) {
                    if (chunk != null) {
                        this.world.timings.doChunkUnloadSave.startTiming();
                        nanoStart = System.nanoTime();
                        chunk.removeEntities();
                        removeEntities += System.nanoTime() - nanoStart;
                        nanoStart = System.nanoTime();
                        this.saveChunk(chunk);
                        saveChunk += System.nanoTime() - nanoStart;
                        nanoStart = System.nanoTime();
                        this.saveChunkNOP(chunk);
                        saveChunkNOP += System.nanoTime() - nanoStart;
                        nanoStart = System.nanoTime();
                        this.chunks.remove(LongHash.toLong(locX, locZ)); // CraftBukkit // MineHQ
                        chunkRemove += System.nanoTime() - nanoStart;
                        unloaded++;
                        this.world.timings.doChunkUnloadSave.stopTiming();
                    }

                    // this.unloadQueue.remove(olong);
                    // this.chunks.remove(olong.longValue());

                    // Update neighbor counts
                    nanoStart = System.nanoTime();
                    for (int x = -2; x < 3; x++) {
                        for (int z = -2; z < 3; z++) {
                            if (x == 0 && z == 0) {
                                continue;
                            }

                            Chunk neighbor = this.getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
                            if (neighbor != null) {
                                neighbor.setNeighborUnloaded(-x, -z);
                                chunk.setNeighborUnloaded(x, z);
                            }
                        }
                    }
                    updateNeighbourCounts += System.nanoTime() - nanoStart;
                }
            }
            // CraftBukkit end
            long timeTaken = System.currentTimeMillis() - start;
            if (timeTaken > 75) {
                MinecraftServer.getLogger().warn("ChunkProviderServer.unloadChunks took too long! " + timeTaken + "ms!");
                MinecraftServer.getLogger().warn("World name: " + this.world.worldData.getName());
                MinecraftServer.getLogger().warn("chunks.size() = " + chunksSize);
                MinecraftServer.getLogger().warn("unloadQueue.size() = " + unloadSize);
                MinecraftServer.getLogger().warn("chunks unloaded this run: " + unloaded);
                MinecraftServer.getLogger().warn("unloadQueuePopTotal: " + unloadQueuePopTotal);
                MinecraftServer.getLogger().warn("chunksGet: " + chunksGet);
                MinecraftServer.getLogger().warn("callEvent: " + callEvent);
                MinecraftServer.getLogger().warn("removeEntities: " + removeEntities);
                MinecraftServer.getLogger().warn("saveChunk: " + saveChunk);
                MinecraftServer.getLogger().warn("saveChunkNOP: " + saveChunkNOP);
                MinecraftServer.getLogger().warn("chunkRemove: " + chunkRemove);
                MinecraftServer.getLogger().warn("updateNeighbourCounts: " + updateNeighbourCounts);
                this.world.printTimings();
                MinecraftServer.getLogger().warn("world.N.size(): " + world.N.size());
                MinecraftServer.getLogger().warn("world.V.size(): " + world.V.size());
            }

            if (this.f != null) {
                this.f.a();
            }

            this.world.clearTimings();
        }

        return this.chunkProvider.unloadChunks();
    }

    public boolean canSave() {
        return !this.world.savingDisabled;
    }

    public String getName() {
        // CraftBukkit - this.chunks.count() -> .values().size()
        return "ServerChunkCache: " + this.chunks.values().size() + " Drop: " + this.unloadQueue.size();
    }

    public List getMobsFor(EnumCreatureType enumcreaturetype, int i, int j, int k) {
        return this.chunkProvider.getMobsFor(enumcreaturetype, i, j, k);
    }

    public ChunkPosition findNearestMapFeature(World world, String s, int i, int j, int k) {
        return this.chunkProvider.findNearestMapFeature(world, s, i, j, k);
    }

    public int getLoadedChunks() {
        // CraftBukkit - this.chunks.count() -> this.chunks.size()
        return this.chunks.size();
    }

    public void recreateStructures(int i, int j) {}
}
