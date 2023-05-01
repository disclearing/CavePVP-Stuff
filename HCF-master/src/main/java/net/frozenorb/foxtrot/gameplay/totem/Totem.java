package net.frozenorb.foxtrot.gameplay.totem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

@AllArgsConstructor
public class Totem {
    @Getter private UUID placedBy;
    @Getter @Setter private Location location;
    @Getter @Setter private PotionEffectType potionEffectType;
    @Getter @Setter private long placedAt;
    @Getter @Setter private long expiresAt;
    @Getter @Setter private int hitsRemaining;
}
