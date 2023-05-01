package org.cavepvp.profiles.playerProfiles.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum  NotificationType {
    PUNISHMENTS("Punishments", Material.TNT, "Get notified when you get punished", Arrays.asList("All Punishments", "Punishment Reasons", "Punishment Durations")),
    FACTION_MEMBER_PUNISHMENTS("Faction Punishments", Material.FLINT_AND_STEEL, "Get notified when your faction member gets punished", Arrays.asList("All faction member punishments", "All faction punishment reasons", "All faction punishment durations")),
    FACTION_ACTIONS("Faction Actions", Material.PAPER,"Get notified as a leader about your faction", Arrays.asList("Withdrawals", "Deposits", "Kicks", "Leaves", "DTR Loss", "Going Raidable")),
    EVENT_REMINDERS("Fasts Reminders", Material.DIAMOND_SWORD, "Get notified on all of the different Fasts events/changes", Arrays.asList("Fasts SOTWs", "Fasts EOTWs", "Fasts Citadels", "Fasts KOTHs")),
    KITS_REMINDERS("Kits Reminders", Material.ENDER_CHEST,"Get notified on all of the different Kits events/changes", Arrays.asList("Kits SOTW", "Kits EOTW", "Kits Citadels"));

    @Getter String displayName;
    @Getter Material material;
    @Getter String slogan;
    @Getter List<String> features;
}
