package net.frozenorb.foxtrot.team.track;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public enum TrackCategory {
    STAFF("Staff Actions", Material.DIAMOND_AXE, Collections.emptyList(), (byte) 0, 0),

    GENERAL("General Logs", Material.GRASS, Collections.emptyList(), (byte) 0, 10),
    CHAT("Chat History", Material.PAPER, Collections.emptyList(), (byte) 0, 12),
    FINANCIAL_LAND("Financial & Land", Material.GOLD_INGOT, Collections.emptyList(), (byte) 0, 14),
    CONNECTIONS("Connections", Material.COMMAND, Collections.emptyList(), (byte) 0, 16),
    MEMBERS("Members Logs", Material.SKULL_ITEM, Collections.emptyList(), (byte) 3, 28),
    DEATHS("Deaths", Material.SKULL_ITEM, Collections.emptyList(), (byte) 0, 30),
    KILLS("Kills", Material.DIAMOND_SWORD, Collections.emptyList(), (byte) 0, 32),
    ALL("All Logs", Material.BOOK, Collections.emptyList(), (byte) 0, 34);

    @Getter String displayName;
    @Getter Material material;
    @Getter List<String> lore;
    @Getter byte data;
    @Getter int slot;
}
