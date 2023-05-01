package org.cavepvp.suge.enchant.data.type;

import cc.fyre.proton.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cavepvp.suge.enchant.data.CustomEnchant;
import org.cavepvp.suge.enchant.data.Tier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InvisibilityEnchant extends CustomEnchant {

    @Override
    public Tier getLevel() {
        return Tier.LEGENDARY;
    }

    @Override
    public int getAmplifier() {
        return 1;
    }

    @Override
    public PotionEffectType getEffect() {
        return PotionEffectType.INVISIBILITY;
    }

    @Override
    public void onEnable(Player player, int level) {
        if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
            player.addPotionEffect(new PotionEffect(this.getEffect(), Integer.MAX_VALUE, level-1), true);
        }
    }
    @Override
    public List<String> getDescription() {
        return Collections.singletonList("This enchant grants you permanent &3Invisibility&7!");
    }
    @Override
    public List<Material> getApplicableItems() {
        return Arrays.asList(Material.CHAINMAIL_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.GOLD_CHESTPLATE, Material.IRON_CHESTPLATE, Material.LEATHER_CHESTPLATE);
    }

    @Override
    public void onDisable(Player player, int level) {
        player.removePotionEffect(this.getEffect());
    }
}
