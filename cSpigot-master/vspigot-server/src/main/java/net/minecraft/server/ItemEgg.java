package net.minecraft.server;

public class ItemEgg extends Item {

    public ItemEgg() {
        this.maxStackSize = 16;
        this.a(CreativeModeTab.l);
    }

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        // Velt start
        if (!world.isStatic && world.addEntity(new EntityEgg(world, entityhuman))) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                --itemstack.count;
            }

            world.makeSound(entityhuman, "random.bow", 0.5f, 0.4f / (ItemEgg.g.nextFloat() * 0.4f + 0.8f));
        }
        // Velt end

        return itemstack;
    }
}