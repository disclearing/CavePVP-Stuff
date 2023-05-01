package org.cavepvp.suge.enchant.data.type;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cavepvp.suge.enchant.data.CustomEnchant;
import org.cavepvp.suge.enchant.data.Tier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GlowingEnchant extends CustomEnchant {

    @Override
    public int getAmplifier() {
        return 4;
    }

    @Override
    public Tier getLevel() {
        return Tier.COMMON;
    }
    @Override
    public List<String> getDescription() {
        return Collections.singletonList("This enchant grants you permanent &9Night Vision&7!");
    }
    @Override
    public PotionEffectType getEffect() {
        return PotionEffectType.NIGHT_VISION;
    }

    @Override
    public void onEnable(Player player, int level) {
        player.addPotionEffect(new PotionEffect(this.getEffect(), Integer.MAX_VALUE, level-1), true);
    }

    @Override
    public void onDisable(Player player, int level) {
        player.removePotionEffect(this.getEffect());
    }
}
