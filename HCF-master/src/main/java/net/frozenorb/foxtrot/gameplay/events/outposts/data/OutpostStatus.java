package net.frozenorb.foxtrot.gameplay.events.outposts.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@RequiredArgsConstructor
public enum OutpostStatus {

    CONTROLLED("Controlled", ChatColor.GREEN),
    CONTROLLING("Controlling", ChatColor.AQUA),
    CONTESTED("Contested", ChatColor.RED),
    NEUTRALIZING("Neutralizing", ChatColor.YELLOW),
    NEUTRAL("Neutral", ChatColor.GREEN);

    @Getter private final String friendlyName;
    @Getter private final ChatColor color;

    public String getDisplayName() {
        return color + friendlyName;
    }
}
