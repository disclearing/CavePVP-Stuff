package org.cavepvp.suge.enchant.data.type;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cavepvp.suge.enchant.data.CustomEnchant;
import org.cavepvp.suge.enchant.data.Tier;

import java.util.Collections;
import java.util.List;

public class FireResistanceEnchant extends CustomEnchant {

    @Override
    public PotionEffectType getEffect() {
        return PotionEffectType.FIRE_RESISTANCE;
    }

    @Override
    public int getAmplifier() {
        return 1;
    }

    @Override
    public Tier getLevel() {
        return Tier.COMMON;
    }

    @Override
    public void onEnable(Player player, int level) {
        player.addPotionEffect(new PotionEffect(this.getEffect(), Integer.MAX_VALUE, level - 1), true);
    }
    @Override
    public List<String> getDescription() {
        return Collections.singletonList("This enchant grants you permanent &cFire Resistance&7!");
    }
    @Override
    public void onDisable(Player player, int level) {
        if (!player.hasPotionEffect(getEffect())) return;

        PotionEffect potionEffect = player.getActivePotionEffects()
                .stream()
                .filter(it -> it.getType().equals(getEffect()))
                .findFirst()
                .orElse(null);

        if (potionEffect == null) return;
        if (potionEffect.getAmplifier() != (level - 1)) return;
        if (potionEffect.getDuration() < 32767) return; // This number has been scientifically proven

        player.removePotionEffect(getEffect());
    }
}
