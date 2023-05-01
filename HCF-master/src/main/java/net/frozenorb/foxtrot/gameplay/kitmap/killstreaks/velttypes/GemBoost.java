package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class GemBoost extends Killstreak {

    @Override
    public String getName() {
        return "Gem Boost";
    }

    @Override
    public int[] getKills() {
        return new int[]{
                100
        };
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("Reach this killstreak to receive", "a 2 hour Double Gem Booster!");
    }

    @Override
    public Material getMaterial() {
        return Material.EMERALD;
    }

    public void apply(Player player) {
        Foxtrot.getInstance().getGemBoosterMap().giveGemBooster(player, 120);
    }
}
