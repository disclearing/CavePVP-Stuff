package net.minecraft.server;

import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
// CraftBukkit end

public abstract class MobSpawnerAbstract {

    public int spawnDelay = 10;
    private String mobName = "Pig";
    private List mobs;
    private TileEntityMobSpawnerData spawnData;
    public double c;
    public double d;
    public int minSpawnDelay = 50;
    public int maxSpawnDelay = 100;
    public int spawnCount = 5;
    private Entity j;
    public int maxNearbyEntities = 7;
    public int requiredPlayerRange = 16;
    public int spawnRange = 4;

    public MobSpawnerAbstract() {}

    public String getMobName() {
        if (this.i() == null) {
            if (this.mobName.equals("Minecart")) {
                this.mobName = "MinecartRideable";
            }

            return this.mobName;
        } else {
            return this.i().c;
        }
    }

    public void setMobName(String s) {
        this.mobName = s;
    }

    public boolean f() {
        return this.a().findNearbyPlayerWhoAffectsSpawning((double) this.b() + 0.5D, (double) this.c() + 0.5D, (double) this.d() + 0.5D, (double) this.requiredPlayerRange) != null; // PaperSpigot
    }

    public void g() {
        if (this.spawnDelay == -1) {
            this.j();
        }

        // Kohi - check spawn delay before player distance
        if (this.spawnDelay > 0) {
            --this.spawnDelay;
            return;
        }

        if (!this.f()) {
            this.j();
            return;
        }

        for (int i = 0; i < this.spawnCount; ++i) {
            Entity entity = EntityTypes.createEntityByName(this.getMobName(), this.a());

            if (entity == null) {
                return;
            }

            int j = this.a().a(entity.getClass(), AxisAlignedBB.a((double) this.b(), (double) this.c(), (double) this.d(), (double) (this.b() + 1), (double) (this.c() + 1), (double) (this.d() + 1)).grow((double) (this.spawnRange * 2), 4.0D, (double) (this.spawnRange * 2))).size();

            if (j >= this.maxNearbyEntities) {
                this.j();
                return;
            }

            double d0 = (double) this.b() + (this.a().random.nextDouble() - this.a().random.nextDouble()) * (double) this.spawnRange;
            double d3 = (double) (this.c() + this.a().random.nextInt(3) - 1);
            double d4 = (double) this.d() + (this.a().random.nextDouble() - this.a().random.nextDouble()) * (double) this.spawnRange;
            EntityInsentient entityinsentient = entity instanceof EntityInsentient ? (EntityInsentient) entity : null;

            entity.setPositionRotation(d0, d3, d4, this.a().random.nextFloat() * 360.0F, 0.0F);

            if (entityinsentient == null || entityinsentient.canSpawn()) {
                this.a(entity);
                this.a().triggerEffect(2004, this.b(), this.c(), this.d(), 0);

                if (entityinsentient != null) {
                    entityinsentient.s();
                }
            }
        }

        this.j();
    }

    public Entity a(Entity entity) {
        if (this.i() != null) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            entity.d(nbttagcompound);
            Iterator iterator = this.i().b.c().iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                NBTBase nbtbase = this.i().b.get(s);

                nbttagcompound.set(s, nbtbase.clone());
            }

            entity.f(nbttagcompound);
            if (entity.world != null) {
                // CraftBukkit start - call SpawnerSpawnEvent, abort if cancelled
                SpawnerSpawnEvent event = CraftEventFactory.callSpawnerSpawnEvent(entity, this.b(), this.c(), this.d());
                if (!event.isCancelled()) {
                    entity.world.addEntity(entity, CreatureSpawnEvent.SpawnReason.SPAWNER); // CraftBukkit
                    // Spigot Start
                    if ( entity.world.spigotConfig.nerfSpawnerMobs )
                    {
                        entity.fromMobSpawner = true;
                    }
                    // Spigot End
                }
                // CraftBukkit end
            }

            NBTTagCompound nbttagcompound1;

            for (Entity entity1 = entity; nbttagcompound.hasKeyOfType("Riding", 10); nbttagcompound = nbttagcompound1) {
                nbttagcompound1 = nbttagcompound.getCompound("Riding");
                Entity entity2 = EntityTypes.createEntityByName(nbttagcompound1.getString("id"), entity.world);

                if (entity2 != null) {
                    NBTTagCompound nbttagcompound2 = new NBTTagCompound();

                    entity2.d(nbttagcompound2);
                    Iterator iterator1 = nbttagcompound1.c().iterator();

                    while (iterator1.hasNext()) {
                        String s1 = (String) iterator1.next();
                        NBTBase nbtbase1 = nbttagcompound1.get(s1);

                        nbttagcompound2.set(s1, nbtbase1.clone());
                    }

                    entity2.f(nbttagcompound2);
                    entity2.setPositionRotation(entity1.locX, entity1.locY, entity1.locZ, entity1.yaw, entity1.pitch);
                    // CraftBukkit start - call SpawnerSpawnEvent, skip if cancelled
                    SpawnerSpawnEvent event = CraftEventFactory.callSpawnerSpawnEvent(entity2, this.b(), this.c(), this.d());
                    if (event.isCancelled()) {
                        continue;
                    }
                    if (entity.world != null) {
                        entity.world.addEntity(entity2, CreatureSpawnEvent.SpawnReason.SPAWNER); // CraftBukkit
                    }

                    entity1.mount(entity2);
                }

                entity1 = entity2;
            }
        } else if (entity instanceof EntityLiving && entity.world != null) {
            ((EntityInsentient) entity).prepare((GroupDataEntity) null);
            // Spigot start - call SpawnerSpawnEvent, abort if cancelled
            SpawnerSpawnEvent event = CraftEventFactory.callSpawnerSpawnEvent(entity, this.b(), this.c(), this.d());
            if (!event.isCancelled()) {
                this.a().addEntity(entity, CreatureSpawnEvent.SpawnReason.SPAWNER); // CraftBukkit
                // Spigot Start
                if ( entity.world.spigotConfig.nerfSpawnerMobs )
                {
                    entity.fromMobSpawner = true;
                }
                // Spigot End
            }
            // Spigot end
        }

        return entity;
    }

    private void j() {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            int i = this.maxSpawnDelay - this.minSpawnDelay;

            this.spawnDelay = this.minSpawnDelay + this.a().random.nextInt(i);
        }

        if (this.mobs != null && this.mobs.size() > 0) {
            this.a((TileEntityMobSpawnerData) WeightedRandom.a(this.a().random, (Collection) this.mobs));
        }

        this.a(1);
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.mobName = nbttagcompound.getString("EntityId");
        this.spawnDelay = nbttagcompound.getShort("Delay");
        if (nbttagcompound.hasKeyOfType("SpawnPotentials", 9)) {
            this.mobs = new ArrayList();
            NBTTagList nbttaglist = nbttagcompound.getList("SpawnPotentials", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                this.mobs.add(new TileEntityMobSpawnerData(this, nbttaglist.get(i)));
            }
        } else {
            this.mobs = null;
        }

        if (nbttagcompound.hasKeyOfType("SpawnData", 10)) {
            this.a(new TileEntityMobSpawnerData(this, nbttagcompound.getCompound("SpawnData"), this.mobName));
        } else {
            this.a((TileEntityMobSpawnerData) null);
        }

        if (nbttagcompound.hasKeyOfType("MinSpawnDelay", 99)) {
            this.minSpawnDelay = nbttagcompound.getShort("MinSpawnDelay");
            this.maxSpawnDelay = nbttagcompound.getShort("MaxSpawnDelay");
            this.spawnCount = nbttagcompound.getShort("SpawnCount");
        }

        if (nbttagcompound.hasKeyOfType("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = nbttagcompound.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = nbttagcompound.getShort("RequiredPlayerRange");
        }

        if (nbttagcompound.hasKeyOfType("SpawnRange", 99)) {
            this.spawnRange = nbttagcompound.getShort("SpawnRange");
        }

        if (this.a() != null && this.a().isStatic) {
            this.j = null;
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("EntityId", this.getMobName());
        nbttagcompound.setShort("Delay", (short) this.spawnDelay);
        nbttagcompound.setShort("MinSpawnDelay", (short) this.minSpawnDelay);
        nbttagcompound.setShort("MaxSpawnDelay", (short) this.maxSpawnDelay);
        nbttagcompound.setShort("SpawnCount", (short) this.spawnCount);
        nbttagcompound.setShort("MaxNearbyEntities", (short) this.maxNearbyEntities);
        nbttagcompound.setShort("RequiredPlayerRange", (short) this.requiredPlayerRange);
        nbttagcompound.setShort("SpawnRange", (short) this.spawnRange);
        if (this.i() != null) {
            nbttagcompound.set("SpawnData", this.i().b.clone());
        }

        if (this.i() != null || this.mobs != null && this.mobs.size() > 0) {
            NBTTagList nbttaglist = new NBTTagList();

            if (this.mobs != null && this.mobs.size() > 0) {
                Iterator iterator = this.mobs.iterator();

                while (iterator.hasNext()) {
                    TileEntityMobSpawnerData tileentitymobspawnerdata = (TileEntityMobSpawnerData) iterator.next();

                    nbttaglist.add(tileentitymobspawnerdata.a());
                }
            } else {
                nbttaglist.add(this.i().a());
            }

            nbttagcompound.set("SpawnPotentials", nbttaglist);
        }
    }

    public boolean b(int i) {
        if (i == 1 && this.a().isStatic) {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        } else {
            return false;
        }
    }

    public TileEntityMobSpawnerData i() {
        return this.spawnData;
    }

    public void a(TileEntityMobSpawnerData tileentitymobspawnerdata) {
        this.spawnData = tileentitymobspawnerdata;
    }

    public abstract void a(int i);

    public abstract World a();

    public abstract int b();

    public abstract int c();

    public abstract int d();
}
