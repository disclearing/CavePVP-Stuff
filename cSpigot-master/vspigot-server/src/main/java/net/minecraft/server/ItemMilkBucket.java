package net.minecraft.server;

import org.github.paperspigot.PaperSpigotConfig;

public class ItemMilkBucket extends Item {

    public ItemMilkBucket() {
        // PaperSpigot start - Stackable Buckets
        if (PaperSpigotConfig.stackableMilkBuckets) {
            this.e(org.bukkit.Material.BUCKET.getMaxStackSize());
        } else {
            this.e(1);
        }
        // PaperSpigot end
        this.a(CreativeModeTab.f);
    }

    public ItemStack b(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (!entityhuman.abilities.canInstantlyBuild) {
            --itemstack.count;
        }

        if (!world.isStatic) {
            entityhuman.removeAllEffects();
        }

        // PaperSpigot start - Stackable Buckets
        if (PaperSpigotConfig.stackableMilkBuckets) {
            if (itemstack.count <= 0) {
                return new ItemStack(Items.BUCKET);
            } else if (!entityhuman.inventory.pickup(new ItemStack(Items.BUCKET))) {
                entityhuman.drop(new ItemStack(Items.BUCKET), false);
            }
        }
        // PaperSpigot end

        return itemstack.count <= 0 ? new ItemStack(Items.BUCKET) : itemstack;
    }

    public int d_(ItemStack itemstack) {
        return 32;
    }

    public EnumAnimation d(ItemStack itemstack) {
        return EnumAnimation.DRINK;
    }

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        entityhuman.a(itemstack, this.d_(itemstack));
        return itemstack;
    }
}
