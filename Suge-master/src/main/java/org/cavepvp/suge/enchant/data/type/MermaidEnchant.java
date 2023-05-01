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

public class MermaidEnchant extends CustomEnchant {

    @Override
    public Tier getLevel() {
        return Tier.COMMON;
    }

    @Override
    public int getAmplifier() {
        return 3;
    }

    @Override
    public PotionEffectType getEffect() {
        return PotionEffectType.WATER_BREATHING;
    }

    @Override
    public void onEnable(Player player, int level) {
        player.addPotionEffect(new PotionEffect(this.getEffect(), Integer.MAX_VALUE, level - 1));
    }

    @Override
    public List<Material> getApplicableItems() {
        return Arrays.asList(Material.CHAINMAIL_HELMET, Material.DIAMOND_HELMET, Material.GOLD_HELMET, Material.IRON_HELMET, Material.LEATHER_HELMET);
    }
    @Override
    public List<String> getDescription() {
        return Collections.singletonList("This enchant grants you permanent &9Water Breathing&7!");
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
