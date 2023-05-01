package net.frozenorb.foxtrot.gameplay.kitmap.kits.upgrades;

import lombok.Getter;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Upgrades {
    private final List<Enchantment> enchantmentList = new ArrayList<>();
    private final List<String> customEnchantmentList = new ArrayList<>();

    public Upgrades protection() {
        enchantmentList.add(Enchantment.PROTECTION_ENVIRONMENTAL);
        return this;
    }

    public Upgrades sharpness() {
        enchantmentList.add(Enchantment.DAMAGE_ALL);
        return this;
    }

    public Upgrades power() {
        enchantmentList.add(Enchantment.ARROW_DAMAGE);
        return this;
    }

    public Upgrades efficiency() {
        enchantmentList.add(Enchantment.DIG_SPEED);
        return this;
    }

    public Upgrades fireResistance() {
        customEnchantmentList.add("FireResistance");
        return this;
    }

    public Upgrades glowing() {
        customEnchantmentList.add("Glowing");
        return this;
    }

    public Upgrades repair() {
        customEnchantmentList.add("HellForged");
        return this;
    }

    public Upgrades saturation() {
        customEnchantmentList.add("Implants");
        return this;
    }

    public Upgrades bubble() {
        customEnchantmentList.add("Mermaid");
        return this;
    }

    public Upgrades recover() {
        customEnchantmentList.add("Recover");
        return this;
    }

    public Upgrades speed() {
        customEnchantmentList.add("Speed");
        return this;
    }
}
