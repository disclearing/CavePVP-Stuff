package net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
@RequiredArgsConstructor
public class GemFlipEntry {

    private final Player creator;
    private final long amount;

    private GemFlipSide chosenSide;
    private boolean started;
}
