package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes;

import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.PersistentKillstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class Strength extends PersistentKillstreak {

    public Strength() {
        super("Strength", 18, Material.BLAZE_POWDER, Arrays.asList("Reach this killstreak to", "receive Strength II for 45 seconds!"));
    }
    
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 45*20, 1));
    }
    
}