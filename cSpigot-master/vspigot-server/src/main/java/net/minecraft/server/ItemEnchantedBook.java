package net.minecraft.server;

import java.util.Random;

public class ItemEnchantedBook extends Item {

    public ItemEnchantedBook() {}

    public boolean e_(ItemStack itemStack) {
        return false;
    }

    public EnumItemRarity f(ItemStack itemstack) {
        if (this.g(itemstack).size() > 0) {
            return EnumItemRarity.UNCOMMON;
        }
        return super.f(itemstack);
    }

    public NBTTagList g(ItemStack itemStack) {
        if (itemStack.tag == null || !itemStack.tag.hasKeyOfType("StoredEnchantments", 9)) {
            return new NBTTagList();
        }
        return (NBTTagList)itemStack.tag.get("StoredEnchantments");
    }

    public void a(ItemStack itemStack, EnchantmentInstance enchantmentInstance) {
        NBTTagList g = this.g(itemStack);
        boolean b = true;
        for (int i = 0; i < g.size(); ++i) {
            NBTTagCompound value = g.get(i);
            if (value.getShort("id") == enchantmentInstance.enchantment.id) {
                if (value.getShort("lvl") < enchantmentInstance.level) {
                    value.setShort("lvl", (short)enchantmentInstance.level);
                }

                b = false;
                break;
            }
        }

        if (b) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setShort("id", (short)enchantmentInstance.enchantment.id);
            nbtTagCompound.setShort("lvl", (short)enchantmentInstance.level);
            g.add(nbtTagCompound);
        }

        if (!itemStack.hasTag()) {
            itemStack.setTag(new NBTTagCompound());
        }

        itemStack.getTag().set("StoredEnchantments", g);
    }

    public ItemStack a(EnchantmentInstance enchantmentInstance) {
        ItemStack itemStack = new ItemStack(this);
        this.a(itemStack, enchantmentInstance);
        return itemStack;
    }

    public StructurePieceTreasure b(Random random) {
        return this.a(random, 1, 1, 1);
    }

    public StructurePieceTreasure a(final Random random, final int n, final int n2, final int n3) {
        ItemStack itemstack = new ItemStack(Items.BOOK, 1, 0);

        EnchantmentManager.a(random, itemstack, 30);
        return new StructurePieceTreasure(itemstack, n, n2, n3);
    }
}
