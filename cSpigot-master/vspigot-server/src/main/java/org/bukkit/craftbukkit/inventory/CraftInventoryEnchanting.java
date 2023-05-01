package org.bukkit.craftbukkit.inventory;

import net.minecraft.server.ContainerEnchantTableInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryEnchanting extends CraftInventory implements EnchantingInventory {
    public CraftInventoryEnchanting(ContainerEnchantTableInventory inventory) {
        super(inventory);
    }

    public void setItem(ItemStack item) {
        setItem(0,item);
    }

    public ItemStack getItem() {
        return getItem(0);
    }

    @Override
    public ContainerEnchantTableInventory getInventory() {
        return (ContainerEnchantTableInventory)inventory;
    }
}
