package org.cavepvp.suge.enchant.data.type;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.cavepvp.suge.enchant.data.CustomEnchant;
import org.cavepvp.suge.enchant.data.Tier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GreedEnchant extends CustomEnchant {

    @Override
    public int getAmplifier() {
        return 1;
    }

    @Override
    public Tier getLevel() {
        return Tier.LEGENDARY;
    }

    @Override
    public PotionEffectType getEffect() {
        return null;
    }
    @Override
    public List<String> getDescription() {
        return Collections.singletonList("This enchant will reduce your golden apple timer by 10 seconds.");
    }
    @Override
    public void onEnable(Player player, int level) {
    }

    @Override
    public void onDisable(Player player, int level) {
    }
}
