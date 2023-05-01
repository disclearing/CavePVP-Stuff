package net.frozenorb.foxtrot.gameplay.pvpclasses.energy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.util.PotionUtil;
import net.frozenorb.foxtrot.util.RomanUtil;
import org.bukkit.potion.PotionEffect;

/**
 * @author xanderume@gmail.com
 */
@AllArgsConstructor
public class EnergyEffect {

    @Getter
    private int energy;
    @Getter
    private PotionEffect potionEffect;

    public static EnergyEffect fromEnergy(int energy) {
        return new EnergyEffect(energy,null);
    }

    public static EnergyEffect fromPotion(PotionEffect potionEffect) {
        return new EnergyEffect(-1,potionEffect);
    }

    public static EnergyEffect fromPotionAndEnergy(PotionEffect potionEffect, int energy) {
        return new EnergyEffect(energy,potionEffect);
    }

    public String getFancyName() {
        return PotionUtil.getColor(this.potionEffect.getType()) + PotionUtil.getName(this.potionEffect.getType()) + " " + RomanUtil.toRoman((this.potionEffect.getAmplifier() + 1));
    }

}