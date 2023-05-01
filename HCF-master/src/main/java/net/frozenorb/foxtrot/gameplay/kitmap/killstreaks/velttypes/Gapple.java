package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes;

import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Gapple extends Killstreak {

    @Override
    public String getName() {
        return "God Apple";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                21
        };
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("Reach this killstreak to receive", "an enchanted God Apple!");
    }

    @Override
    public Material getMaterial() {
        return Material.GOLDEN_APPLE;
    }

    @Override
    public void apply(Player player) {
        give(player, new ItemStack(Material.GOLDEN_APPLE, 1, (byte) 1));
    }

}
