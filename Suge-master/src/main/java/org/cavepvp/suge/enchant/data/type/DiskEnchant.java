package org.cavepvp.suge.enchant.data.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.cavepvp.suge.enchant.data.CustomEnchant;
import org.cavepvp.suge.enchant.data.Tier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiskEnchant extends CustomEnchant {

    @Override
    public int getAmplifier() {
        return 1;
    }

    @Override
    public Tier getLevel() {
        return Tier.CAVE;
    }

    @Override
    public PotionEffectType getEffect() {
        return null;
    }

    @Override
    public List<Material> getApplicableItems() {
        return Arrays.asList(Material.DIAMOND_PICKAXE, Material.DIAMOND_SPADE);
    }

    @Override
    public void onEnable(Player player, int level) {
    }

    @Override
    public void onDisable(Player player, int level) {
    }
}
