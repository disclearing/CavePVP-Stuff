package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes;

import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.Killstreak;
import cc.fyre.proton.util.ItemBuilder;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PotionRefillToken extends Killstreak {

    @Override
    public String getName() {
        return "Potion Refill Token";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                25
        };
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("Reach this killstreak to receive", "a Potion Refill Token!");
    }

    @Override
    public Material getMaterial() {
        return Material.NETHER_STAR;
    }

    @Override
    public void apply(Player player) {
        give(player, ItemBuilder.of(Material.NETHER_STAR).name("&5&k! &d&lPotion Refill Token &5&k!").setUnbreakable(true).setLore(ImmutableList.of("&7Right click to fill your inventory with potions!")).build());
    }

}