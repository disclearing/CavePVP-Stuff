package net.minecraft.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.valorhcf.util.IndexedLinkedHashSet;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EntityTracker {

    // MineHQ start
    private IndexedLinkedHashSet<EntityTrackerEntry> c = new IndexedLinkedHashSet<EntityTrackerEntry>();
    public IntHashMap trackedEntities = new IntHashMap(); // CraftBukkit - private -> public

    private int noTrackDistance = 0;

    public int getNoTrackDistance() {
        return this.noTrackDistance;
    }

    public void setNoTrackDistance(int noTrackDistance) {
        this.noTrackDistance = noTrackDistance;
    }
    // MineHQ end

    private WorldServer worldServer;
    private int e;

    public EntityTracker(WorldServer worldserver) {
        this.worldServer = worldserver;
        this.e = 128; // MineHQ
    }

    public WorldServer getWorldServer() {
        return worldServer;
    }

    public void track(Entity entity) {
        if (entity instanceof EntityPlayer) {
            this.addEntity(entity, 512, 2);
            // MineHQ start 
            /*
            EntityPlayer entityplayer = (EntityPlayer) entity;
            Iterator iterator = this.c.iterator();

            while (iterator.hasNext()) {
                EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

                if (entitytrackerentry.tracker != entityplayer) {
                    entitytrackerentry.updatePlayer(entityplayer);
                }
            }
            */
            // MineHQ end
        } else if (entity instanceof EntityFishingHook) {
            this.addEntity(entity, 64, 5, true);
        } else if (entity instanceof EntityArrow) {
            this.addEntity(entity, 64, 20, false);
        } else if (entity instanceof EntitySmallFireball) {
            this.addEntity(entity, 64, 10, false);
        } else if (entity instanceof EntityFireball) {
            this.addEntity(entity, 64, 10, false);
        } else if (entity instanceof EntitySnowball) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityEnderPearl) {
            this.addEntity(entity, 64, 2, true);
        } else if (entity instanceof EntityEnderSignal) {
            this.addEntity(entity, 64, 4, true);
        } else if (entity instanceof EntityEgg) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityPotion) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityThrownExpBottle) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityFireworks) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityItem) {
            this.addEntity(entity, 64, 20, true);
        } else if (entity instanceof EntityMinecartAbstract) {
            this.addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntityBoat) {
            this.addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntitySquid) {
            this.addEntity(entity, 64, 3, true);
        } else if (entity instanceof EntityWither) {
            this.addEntity(entity, 80, 3, false);
        } else if (entity instanceof EntityBat) {
            this.addEntity(entity, 80, 3, false);
        } else if (entity instanceof IAnimal) {
            this.addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntityEnderDragon) {
            this.addEntity(entity, 160, 3, true);
        } else if (entity instanceof EntityTNTPrimed) {
            this.addEntity(entity, 160, 10, true);
        } else if (entity instanceof EntityFallingBlock) {
            this.addEntity(entity, 160, 20, true);
        } else if (entity instanceof EntityHanging) {
            this.addEntity(entity, 160, Integer.MAX_VALUE, false);
        } else if (entity instanceof EntityExperienceOrb) {
            this.addEntity(entity, 160, 20, true);
        } else if (entity instanceof EntityEnderCrystal) {
            this.addEntity(entity, 256, Integer.MAX_VALUE, false);
        }
    }

    public void addEntity(Entity entity, int i, int j) {
        this.addEntity(entity, i, j, false);
    }

    public void addEntity(Entity entity, int i, int j, boolean flag) {
        org.spigotmc.AsyncCatcher.catchOp( "entity track"); // Spigot
        i = org.spigotmc.TrackingRange.getEntityTrackingRange(entity, i); // Spigot
        if (i > this.e) {
            i = this.e;
        }

        try {
            if (this.trackedEntities.b(entity.getId())) {
                throw new IllegalStateException("Entity is already tracked!");
            }

            EntityTrackerEntry entitytrackerentry = new EntityTrackerEntry(this, entity, i, j, flag); // MineHQ

            this.c.add(entitytrackerentry);
            this.trackedEntities.a(entity.getId(), entitytrackerentry);
            // entitytrackerentry.scanPlayers(this.world.players); // MineHQ
            entitytrackerentry.addNearPlayers();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void untrackEntity(Entity entity) {
        org.spigotmc.AsyncCatcher.catchOp( "entity untrack"); // Spigot
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;
            Iterator iterator = this.c.iterator();

            while (iterator.hasNext()) {
                EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

                entitytrackerentry.a(entityplayer);
            }
        }

        EntityTrackerEntry entitytrackerentry1 = (EntityTrackerEntry) this.trackedEntities.d(entity.getId());

        if (entitytrackerentry1 != null) {
            this.c.remove(entitytrackerentry1);
            entitytrackerentry1.a();
        }
    }

    // MineHQ start - parallel tracking
    private static int trackerThreads = 4; // <-- 3 non-this threads, one this
    private static ExecutorService pool = Executors.newFixedThreadPool(trackerThreads - 1, new ThreadFactoryBuilder().setNameFormat("entity-tracker-%d").build());
    public void updatePlayers() {
        int offset = 0;
        final CountDownLatch latch = new CountDownLatch(trackerThreads);
        for (int i = 1; i <= trackerThreads; i++) {
            final int localOffset = offset++;

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for (int i = localOffset; i < c.size(); i += trackerThreads) {
                        c.get(i).update();
                    }
                    latch.countDown();
                }
            };

            if (i < trackerThreads) {
                pool.execute(runnable);
            } else {
                runnable.run();
            }
        }

        try {
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // MineHQ end

    public void a(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) this.trackedEntities.get(entity.getId());

        if (entitytrackerentry != null) {
            entitytrackerentry.broadcast(packet);
        }
    }

    public void sendPacketToEntity(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) this.trackedEntities.get(entity.getId());

        if (entitytrackerentry != null) {
            entitytrackerentry.broadcastIncludingSelf(packet);
        }
    }

    public void untrackPlayer(EntityPlayer entityplayer) {
        Iterator iterator = this.c.iterator();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            entitytrackerentry.clear(entityplayer);
        }
    }

    // MineHQ start - nope
    /*
    public void a(EntityPlayer entityplayer, Chunk chunk) {
        // Kohi start - Optimized EntityTracker
        for (List<Entity> slice : chunk.entitySlices) {
            for (Entity entity : slice) {
                if (entity != entityplayer) {
                    EntityTrackerEntry entry = (EntityTrackerEntry) trackedEntities.get(entity.getId());

                    if (entry != null) {
                        entry.updatePlayer(entityplayer);
                    }
                }
            }
        }
        // Kohi end
    }
    */

}
