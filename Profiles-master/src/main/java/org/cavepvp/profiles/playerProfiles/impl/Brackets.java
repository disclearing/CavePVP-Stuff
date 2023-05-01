package org.cavepvp.profiles.playerProfiles.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum Brackets {
    UNRANKED("Unranked", 0, 4, ChatColor.WHITE),
    SILVER("Silver", 5, 19, ChatColor.GRAY),
    GOLD("Gold", 20, 49, ChatColor.GOLD),
    EMERALD("Emerald", 50, 99, ChatColor.GREEN),
    DIAMOND("Diamond", 100, 199, ChatColor.AQUA),
    MASTER("Master", 200, Integer.MAX_VALUE, ChatColor.DARK_PURPLE),
    CHAMPION("Champion", -10000, -10000, ChatColor.DARK_RED);

    @Getter final String displayName;
    @Getter final int minReputation;
    @Getter final int maxReputation;
    @Getter final ChatColor chatColor;

}
