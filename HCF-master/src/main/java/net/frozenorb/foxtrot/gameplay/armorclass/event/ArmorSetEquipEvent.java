package net.frozenorb.foxtrot.gameplay.armorclass.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.util.event.FoxtrotEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Getter
@Setter
@RequiredArgsConstructor
public class ArmorSetEquipEvent extends FoxtrotEvent implements Cancellable {

    private final Player player;
    private final ArmorClass armorClass;

    private boolean cancelled;
}
