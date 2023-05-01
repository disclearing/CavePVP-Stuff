package net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@Getter
@RequiredArgsConstructor
public enum GemFlipSide {

    HEADS("Heads", "H", Material.DIAMOND),
    TAILS("Tails", "T", Material.EMERALD);

    private final String friendlyName, abbreviation;
    private final Material material;

    public GemFlipSide getOpposite() {
        return this == HEADS ? TAILS : HEADS;
    }
}
