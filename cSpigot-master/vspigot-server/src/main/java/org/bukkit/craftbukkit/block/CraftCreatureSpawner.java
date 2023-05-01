package org.bukkit.craftbukkit.block;

import net.minecraft.server.TileEntityMobSpawner;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.EntityType;

public class CraftCreatureSpawner extends CraftBlockState implements CreatureSpawner {
    private final TileEntityMobSpawner spawner;

    public CraftCreatureSpawner(final Block block) {
        super(block);

        spawner = (TileEntityMobSpawner) ((CraftWorld) block.getWorld()).getTileEntityAt(getX(), getY(), getZ());
    }

    @Deprecated
    public CreatureType getCreatureType() {
        return CreatureType.fromName(spawner.getSpawner().getMobName());
    }

    public EntityType getSpawnedType() {
        return EntityType.fromName(spawner.getSpawner().getMobName());
    }

    @Deprecated
    public void setCreatureType(CreatureType creatureType) {
        spawner.getSpawner().setMobName(creatureType.getName());
    }

    public void setSpawnedType(EntityType entityType) {
        if (entityType == null || entityType.getName() == null) {
            throw new IllegalArgumentException("Can't spawn EntityType " + entityType + " from mobspawners!");
        }

        spawner.getSpawner().setMobName(entityType.getName());
    }

    @Deprecated
    public String getCreatureTypeId() {
        return spawner.getSpawner().getMobName();
    }

    @Deprecated
    public void setCreatureTypeId(String creatureName) {
        setCreatureTypeByName(creatureName);
    }

    public String getCreatureTypeName() {
        return spawner.getSpawner().getMobName();
    }

    public void setCreatureTypeByName(String creatureType) {
        // Verify input
        EntityType type = EntityType.fromName(creatureType);
        if (type == null) {
            return;
        }
        setSpawnedType(type);
    }

    public int getDelay() {
        return spawner.getSpawner().spawnDelay;
    }

    public int getMaxSpawnDelay() {
        return spawner.getSpawner().maxSpawnDelay;
    }

    public int getMinSpawnDelay() {
        return spawner.getSpawner().minSpawnDelay;
    }

    public int getMaxNearbyEntities() {
        return spawner.getSpawner().maxNearbyEntities;
    }

    public int getSpawnCount() {
        return spawner.getSpawner().spawnCount;
    }

    public int getRequiredPlayerRange() {
        return spawner.getSpawner().requiredPlayerRange;
    }

    public int getSpawnRange() {
        return spawner.getSpawner().spawnRange;
    }

    public void setDelay(int delay) {
        spawner.getSpawner().spawnDelay = delay;
    }
    
    public void setMaxSpawnDelay(int maxSpawnDelay) {
        spawner.getSpawner().maxSpawnDelay = maxSpawnDelay;
    }

    public void setMinSpawnDelay(int minSpawnDelay) {
        spawner.getSpawner().minSpawnDelay = minSpawnDelay;
    }

    public void setMaxNearbyEntities(int maxNearbyEntities) {
        spawner.getSpawner().maxNearbyEntities = maxNearbyEntities;
    }

    public void setSpawnCount(int spawnCount) {
        spawner.getSpawner().spawnCount = spawnCount;
    }

    public void setRequiredPlayerRange(int requiredPlayerRange) {
        spawner.getSpawner().requiredPlayerRange = requiredPlayerRange;
    }

    public void setSpawnRange(int spawnRange) {
        spawner.getSpawner().spawnRange = spawnRange;
    }

}
