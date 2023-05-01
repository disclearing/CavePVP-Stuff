package org.cavepvp.profiles.playerProfiles.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum PlayerType {
    EVERYONE(ChatColor.GREEN + "Everyone", 1),
    FRIENDS_ONLY(ChatColor.AQUA + "Friends Only", 2),
    NOBODY(ChatColor.RED + "Nobody", 3);

    @Getter String displayName;
    @Getter int number;

}
