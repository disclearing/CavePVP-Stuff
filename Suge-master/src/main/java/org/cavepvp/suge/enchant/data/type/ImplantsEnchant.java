package org.cavepvp.suge.enchant.data.type;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.cavepvp.suge.enchant.data.CustomEnchant;
import org.cavepvp.suge.enchant.data.Tier;

import java.util.Collections;
import java.util.List;

public class ImplantsEnchant extends CustomEnchant {

    @Override
    public Tier getLevel() {
        return Tier.RARE;
    }

    @Override
    public int getAmplifier() {
        return 5;
    }

    @Override
    public PotionEffectType getEffect() {
        return null;
    }

    @Override
    public void onEnable(Player player, int level) {}
    @Override
    public List<String> getDescription() {
        return Collections.singletonList("Feed yourself by constantly moving!");
    }
    @Override
    public void onDisable(Player player, int level) {}

//    @Override
//    public void onTick(Player player, int level, ItemStack itemStack) {
//
//        if (player.getFoodLevel() == 20) {
//            return;
//        }
//
//        if (ThreadLocalRandom.current().nextInt(0, 100) > (15*level)) {
//            return;
//        }
//
//        player.setFoodLevel(player.getFoodLevel()+1);
//    }
}
