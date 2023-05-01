package org.cavepvp.suge.enchant.data.type;

import cc.fyre.proton.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cavepvp.suge.enchant.data.CustomEnchant;
import org.cavepvp.suge.enchant.data.Tier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpeedEnchant extends CustomEnchant {

    @Override
    public Tier getLevel() {
        return Tier.RARE;
    }

    @Override
    public int getAmplifier() {
        return 2;
    }

    @Override
    public PotionEffectType getEffect() {
        return PotionEffectType.SPEED;
    }

    @Override
    public void onEnable(Player player, int level) {
        player.addPotionEffect(new PotionEffect(this.getEffect(), Integer.MAX_VALUE, level - 1), true);
    }

    @Override
    public List<Material> getApplicableItems() {
        return Arrays.asList(Material.CHAINMAIL_BOOTS, Material.DIAMOND_BOOTS, Material.GOLD_BOOTS, Material.IRON_BOOTS, Material.LEATHER_BOOTS);
    }

    @Override
    public List<String> getDescription() {
        return Collections.singletonList("This enchant grants you permanent &bSpeed II&7!");
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
