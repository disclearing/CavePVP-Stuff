package net.minecraft.server;

import java.util.Random;

public class EnchantmentThorns extends Enchantment {

    public EnchantmentThorns(int i, int j) {
        super(i, j, EnchantmentSlotType.ARMOR_TORSO);
        this.b("thorns");
    }

    public int a(int n) {
        return 10 + 20 * (n - 1);
    }

    public int b(int i) {
        return super.a(i) + 50;
    }

    public int getMaxLevel() {
        return 3;
    }

    public boolean canEnchant(ItemStack itemstack) {
        return itemstack.getItem() instanceof ItemArmor ? true : super.canEnchant(itemstack);
    }

    public void b(EntityLiving entityliving, Entity entity, int n) {
        Random ai = entityliving.aI();
        ItemStack a = EnchantmentManager.a(Enchantment.THORNS, entityliving);

        if (a(n, ai)) {
            entity.damageEntity(DamageSource.a(entityliving), b(n, ai));
            entity.makeSound("damage.thorns", 0.5F, 1.0F);
            if (a != null) {
                a.damage(3, entityliving);
            }
        } else if (a != null) {
            a.damage(1, entityliving);
        }
    }

    public static boolean a(int n, Random random) {
        return n > 0 && random.nextFloat() < 0.15f * n;
    }

    public static int b(int n, Random random) {
        if (n > 10) {
            return n - 10;
        }

        return 1 + random.nextInt(4);
    }
}
