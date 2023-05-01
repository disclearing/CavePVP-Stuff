package net.frozenorb.foxtrot.team.dtr;

import lombok.Getter;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Location;

public enum DTRBitmask {

    // Used in spawns
    SAFE_ZONE(1, "Safe-Zone", "Determines if a region is considered completely safe"),

    // Used in the end spawn
    DENY_REENTRY(2, "Deny-Reentry", "Determines if a region can be reentered"),

    // Used in Citadel
    FIFTEEN_MINUTE_DEATHBAN(4, "15m-Deathban", "Determines if a region has a 15m deathban"),

    // Used in KOTHs
    FIVE_MINUTE_DEATHBAN(8, "5m-Deathban", "Determines if a region has a 5m deathban"),

    // Used in Citadel
    THIRTY_SECOND_ENDERPEARL_COOLDOWN(16, "30s-Enderpearl-Cooldown", "Determines if a region has a 30s enderpearl cooldown"),

    // Used in Citadel
    CITADEL(32, "Citadel", "Determines if a region is part of Citadel"),

    // Used in KOTHs
    KOTH(64, "KOTH", "Determines if a region is a KOTH"),

    // Used in KOTHs & Citadel.
    REDUCED_DTR_LOSS(128, "Reduced-DTR-Loss", "Determines if a region takes away reduced DTR upon death"),

    // Used in various regions.
    NO_ENDERPEARL(256, "No-Enderpearl", "Determines if a region cannot be pearled into"),

    // Used in various regions.
    QUARTER_DTR_LOSS(512, "1/4-DTR-Loss", "Determines if a region takes away 1/4th DTR loss."),

    // Used on the road.
    ROAD(1024, "Road", "Determines if a region is a road."),

    // Used in Conquest.
    CONQUEST(2048, "Conquest", "Determines if a region is part of Conquest."),

    DUEL(4096, "Duel", "Determines if a region is part of a duel"),
    
    DTC(8192, "DTC", "Determines if a region is part of DTC"),

    // Used in Citadel
    FURY(47, "Fury", "Determines if a region is part of Fury"),

    MOUNTAIN(16384, "Mountain", "Determines if a region is part of Mountain"),

    OUTPOST(32768, "Outpost", "Determines if a region is part of Outpost");

    @Getter private int bitmask;
    @Getter private String name;
    @Getter private String description;

    DTRBitmask(int bitmask, String name, String description) {
        this.bitmask = bitmask;
        this.name = name;
        this.description = description;
    }

    public boolean appliesAt(Location location) {
        Team ownerTo = LandBoard.getInstance().getTeam(location);
        return (ownerTo != null && ownerTo.getOwner() == null && ownerTo.hasDTRBitmask(this));
    }

}