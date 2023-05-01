package org.cavepvp.suge.enchant.data;

import cc.fyre.proton.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CustomEnchant {

    public String getName() {
        return this.getClass().getSimpleName().replace("Enchant", "");
    }

    public List<Material> getApplicableItems() {
        final List<Material> toReturn = new ArrayList<>();

        Arrays.stream(Material.values()).filter(it -> Arrays.stream(ItemUtils.ArmorPart.values())
                .anyMatch(part ->
                        part.name().equalsIgnoreCase(it.name()
                            .replace("_", "")
                            .replace("DIAMOND", "")
                            .replace("GOLD", "")
                            .replace("LEATHER", "")
                            .replace("IRON", "")
                            .replace("CHAINMAIL", ""))))
                .forEach(toReturn::add);

        return toReturn;
    }

    public List<String> getDescription() {
        return new ArrayList<>();
    }

    public abstract PotionEffectType getEffect();
    public abstract Tier getLevel();
    public abstract int getAmplifier();

    public abstract void onEnable(Player player, int level);
    public abstract void onDisable(Player player, int level);

}
