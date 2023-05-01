package net.minecraft.server;

public class EnchantmentDepthStrider extends Enchantment {

    public EnchantmentDepthStrider(int i,int j) {
        super(i,j,EnchantmentSlotType.ARMOR_FEET);
        this.name = "waterWalker";
    }

    public int a(int var1) {
        return var1 * 10;
    }

    public int b(int var1) {
        return this.a(var1) + 15;
    }

    public int getMaxLevel() {
        return 3;
    }
}

