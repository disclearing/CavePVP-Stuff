package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class PersistentKillstreak {
    
    @Getter private String name;
    @Getter private int killsRequired;
    @Getter private Material material;
    @Getter private List<String> description;
    
    public boolean matchesExactly(int kills) {
        return kills == killsRequired;
    }
    
    public boolean check(int count) {
        return killsRequired <= count;
    }
    
    public void apply(Player player) {}
    
}
