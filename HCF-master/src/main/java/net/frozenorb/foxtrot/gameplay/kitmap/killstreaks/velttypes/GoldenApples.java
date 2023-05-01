package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes;

import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class GoldenApples extends Killstreak {

    @Override
    public String getName() {
        return "5 Golden Apples";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                3
        };
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("Reach this killstreak to", "receive a 5x Golden Apples");
    }

    @Override
    public Material getMaterial() {
        return Material.GOLDEN_APPLE;
    }

    @Override
    public void apply(Player player) {
        give(player, new ItemStack(Material.GOLDEN_APPLE, 5));
    }

}