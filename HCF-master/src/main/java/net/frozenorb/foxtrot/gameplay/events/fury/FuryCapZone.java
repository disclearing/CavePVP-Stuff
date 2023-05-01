package net.frozenorb.foxtrot.gameplay.events.fury;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum FuryCapZone {
    OVERWORLD("Overworld", ChatColor.DARK_GREEN, 1),
    END("End", ChatColor.DARK_PURPLE, 2),
    NETHER("Nether", ChatColor.RED,3);

    @Getter String displayName;
    @Getter ChatColor chatColor;
    @Getter int order;
}
