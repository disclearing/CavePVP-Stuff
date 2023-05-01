package org.cavepvp.suge.enchant.data.type;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.cavepvp.suge.enchant.data.CustomEnchant;
import org.cavepvp.suge.enchant.data.Tier;

import java.util.Collections;
import java.util.List;

public class HellForgedEnchant extends CustomEnchant {

    @Override
    public int getAmplifier() {
        return 4;
    }

    @Override
    public Tier getLevel() {
        return Tier.RARE;
    }

    @Override
    public PotionEffectType getEffect() {
        return null;
    }
    @Override
    public List<String> getDescription() {
        return Collections.singletonList("Slowly repair your items while equipped.");
    }
    @Override
    public void onEnable(Player player, int level) {}

    @Override
    public void onDisable(Player player, int level) {}
}
