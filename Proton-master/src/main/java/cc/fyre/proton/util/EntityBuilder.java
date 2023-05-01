package cc.fyre.proton.util;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

public class EntityBuilder {
    private final LivingEntity livingEntity;

    public static EntityBuilder of(EntityType entityType, Location location) {
        return new EntityBuilder((LivingEntity) location.getWorld().spawnEntity(location, entityType));
    }

    public static EntityBuilder copyOf(EntityBuilder builder) {
        return new EntityBuilder(builder.build());
    }

    public static EntityBuilder copyOf(LivingEntity livingEntity) {
        return new EntityBuilder(livingEntity);
    }

    private EntityBuilder(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    public EntityBuilder setMaxHealth(double health) {
        this.livingEntity.setMaxHealth(health);
        this.livingEntity.setHealth(health);
        return this;
    }

    public EntityBuilder setHealth(double health) {
        Preconditions.checkArgument(health > this.livingEntity.getMaxHealth(), "Health may not be higher than the maximum health set.");
        this.livingEntity.setHealth(health);
        return this;
    }

    public EntityBuilder setHelmet(ItemStack itemStack) {
        this.livingEntity.getEquipment().setHelmet(itemStack);
        return this;
    }

    public EntityBuilder setChestplate(ItemStack itemStack) {
        this.livingEntity.getEquipment().setChestplate(itemStack);
        return this;
    }

    public EntityBuilder setLeggings(ItemStack itemStack) {
        this.livingEntity.getEquipment().setLeggings(itemStack);
        return this;
    }

    public EntityBuilder setBoots(ItemStack itemStack) {
        this.livingEntity.getEquipment().setBoots(itemStack);
        return this;
    }

    public EntityBuilder setMetadata(String key, Object value, Plugin owningPlugin) {
        this.livingEntity.setMetadata(key, new FixedMetadataValue(owningPlugin, value));
        return this;
    }

    public EntityBuilder setCustomName(String name) {
        this.livingEntity.setCustomNameVisible(true);
        this.livingEntity.setCustomName(ChatColor.translate(name));
        return this;
    }

    public EntityBuilder addPotionEffect(PotionEffect potionEffect) {
        this.livingEntity.addPotionEffect(potionEffect);
        return this;
    }
    public EntityBuilder setCanPickupItems(boolean result) {
        this.livingEntity.setCanPickupItems(result);
        return this;
    }

    public LivingEntity build() {
        return this.livingEntity;
    }
}
