package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes;

import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.PersistentKillstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class Invis extends PersistentKillstreak {

    public Invis() {
        super("Invis", 27,  Material.INK_SACK, Arrays.asList("Reach this killstreak to", "receive a Invisibility potion"));
    }
    
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 600*20, 1));
    }
    
}
