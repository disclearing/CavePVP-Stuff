package net.minecraft.server;

import org.bukkit.event.player.PlayerFishEvent;

public class ItemFishingRod extends Item {

    public ItemFishingRod() {
        this.setMaxDurability(64);
        this.e(1);
        this.a(CreativeModeTab.i);
    }

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (entityhuman.hookedFish != null) {
            int i = entityhuman.hookedFish.e();

            itemstack.damage(i, entityhuman);
            entityhuman.ba();
        } else {
            // CraftBukkit start
            EntityFishingHook hook = new EntityFishingHook(world, entityhuman);
            PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), null, (org.bukkit.entity.Fish) hook.getBukkitEntity(), PlayerFishEvent.State.FISHING);
            world.getServer().getPluginManager().callEvent(playerFishEvent);

            if (playerFishEvent.isCancelled()) {
                entityhuman.hookedFish = null;
                return itemstack;
            }
            // CraftBukkit end

            // Velt start
            if (!world.isStatic && world.addEntity(hook)) {
                world.makeSound(entityhuman, "random.bow", 0.5F, 0.4F / (g.nextFloat() * 0.4F + 0.8F));
            }
            // Velt end

            entityhuman.ba();
        }

        return itemstack;
    }

    public boolean e_(ItemStack itemstack) {
        return super.e_(itemstack);
    }

    public int c() {
        return 1;
    }
}
