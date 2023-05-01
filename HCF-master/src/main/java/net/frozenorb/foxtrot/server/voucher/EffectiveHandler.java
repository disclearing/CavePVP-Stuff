package net.frozenorb.foxtrot.server.voucher;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectiveHandler {

    @Getter private List<Integer> effectivePackages = new ArrayList<>();

    public EffectiveHandler() {
        this.loadPackages();
    }

    // I'm sorry to whoever is seeing this, tebex is so aids, I am deeply deeply sorry for this.
    public void loadPackages() {

        // RANKS

        effectivePackages.add(2995317); // IRON
        effectivePackages.add(2995325); // GOLD
        effectivePackages.add(2995326); // DIAMOND
        effectivePackages.add(2995327); // EMERALD
        effectivePackages.add(2995333); // PEARL
        effectivePackages.add(2995343); // SAPPHIRE
        effectivePackages.add(3095615); // PLATINUM
        effectivePackages.add(3263578); // RAVINE
        effectivePackages.add(4152653); // HOLIDAY

        // RANK UPGRADES

        effectivePackages.add(2995344); // IRON -> GOLD
        effectivePackages.add(2995350); // GOLD -> DIAMOND
        effectivePackages.add(2995354); // DIAMOND -> EMERALD
        effectivePackages.add(2995356); // EMERALD -> PEARL
        effectivePackages.add(2995358); // PEARL -> SAPPHIRE
        effectivePackages.add(3110697); // SAPPHIRE -> PLATINUM
        effectivePackages.add(3284922); // PLATINUM -> RAVINE
        effectivePackages.add(4156976); // RAVINE -> HOLIDAY

        // CRATE KEYS

        effectivePackages.add(3180037); // RARE KEYS
        effectivePackages.add(5224213); // LEGENDARY KEYS
        effectivePackages.add(5224218); // CAVE KEYS
        effectivePackages.add(4753839); // HALLOWEEN KEYS
        effectivePackages.add(3242328); // OCTOBER KEY BUNDLE
        effectivePackages.add(3351528); // REINFORCE LOOTBOX
        effectivePackages.add(4356882); // Fall Lootbox

        // OWNER KEYS

        effectivePackages.add(4841877); // DYLAN KEYS
        effectivePackages.add(4557465); // RESUCTING KEYS
        effectivePackages.add(4557467); // NOTRAMIX KEYS
        effectivePackages.add(4244976); // IMAKEMCVIDS KEYS
        effectivePackages.add(4296329); // HEADED KEYS
        effectivePackages.add(4545681); // SAMHCF KEYS
        effectivePackages.add(4887221); // FLUSHA KEYS
        effectivePackages.add(4730630); // QNA KEYS
        effectivePackages.add(4741224); // FROZEADO KEYS
        effectivePackages.add(4643515); // LECTORS KEYS
        effectivePackages.add(4592932); // VIK KEYS
    }
}
