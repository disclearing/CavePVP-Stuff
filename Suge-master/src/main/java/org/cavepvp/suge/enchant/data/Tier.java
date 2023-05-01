package org.cavepvp.suge.enchant.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum Tier {
    COMMON(ChatColor.GREEN + ChatColor.BOLD.toString() + "Common", 10, 11),
    RARE(ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Rare", 20, 12),
    LEGENDARY(ChatColor.GOLD + ChatColor.BOLD.toString() + "Legendary", 30, 13),
    MYTHICAL(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Mythical", 50, 14),
    CAVE(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Cave", -1, 15);

    @Getter String displayName;
    @Getter int levelCost;
    @Getter int slot;

}
