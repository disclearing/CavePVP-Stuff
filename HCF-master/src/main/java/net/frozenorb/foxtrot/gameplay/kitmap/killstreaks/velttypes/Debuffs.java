package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes;

import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.List;

public class Debuffs extends Killstreak {

    @Override
    public String getName() {
        return "Debuffs";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                9
        };
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("Reach this killstreak to receive", "a Slowness and Poison potion!");
    }

    @Override
    public Material getMaterial() {
        return Material.SPIDER_EYE;
    }

    @Override
    public void apply(Player player) {
        Potion poison = new Potion(PotionType.POISON);
        poison.setSplash(true);

        Potion slowness = new Potion(PotionType.SLOWNESS);
        slowness.setSplash(true);

        give(player, poison.toItemStack(1));
        give(player, slowness.toItemStack(1));
    }

}
