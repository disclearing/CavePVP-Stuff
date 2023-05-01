package org.cavepvp.profiles.playerProfiles.impl.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
public enum StatisticServer {
    FASTS("Fasts", Material.DIAMOND_SWORD, false, false, false, true, true, 28),
    CLANS("Clans", Material.IRON_SWORD, false, false, false, true, true, 30),
    KITS("Kits", Material.ENDER_CHEST, false, false, false, true, true, 32),
    BUNKERS("Bunkers", Material.BEACON, true, true, true, false,  false, 34);

    @Getter String displayName;
    @Getter Material icon;
    @Getter boolean provideWins;
    @Getter boolean provideLosses;
    @Getter boolean gamesPlayed;
    @Getter boolean mapsPlayed;
    @Getter boolean citadelsCaptured;
    @Getter int slot;
}
