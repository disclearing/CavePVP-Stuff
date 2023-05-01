package net.minecraft.server;

import org.github.paperspigot.PaperSpigotConfig;

public class MobEffectAttackDamage extends MobEffectList {

    protected MobEffectAttackDamage(int i, boolean flag, int j) {
        super(i, flag, j);
    }

    public double a(int i, AttributeModifier attributemodifier) {
        return this.id == MobEffectList.WEAKNESS.id ? (double) (PaperSpigotConfig.weaknessEffectModifier * (float) (i + 1)) : PaperSpigotConfig.strengthEffectModifier * (double) (i + 1);
    }
}
