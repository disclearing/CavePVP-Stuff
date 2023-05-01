package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes;

import net.frozenorb.foxtrot.gameplay.ability.type.kitmap.TacticalNuke;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Nuke extends Killstreak {

    @Override
    public String getName() {
        return "Tactical Nuke";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                100
        };
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("Reach this killstreak to receive a Nuke", "that kills everyone within 30 blocks!");
    }

    @Override
    public Material getMaterial() {
        return Material.TNT;
    }

    @Override
    public void apply(Player player) {
        give(player, TacticalNuke.itemStack.clone());
    }

}
