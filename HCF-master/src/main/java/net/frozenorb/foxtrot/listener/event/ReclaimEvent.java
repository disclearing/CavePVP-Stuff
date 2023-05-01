package net.frozenorb.foxtrot.listener.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
@Getter
public class ReclaimEvent extends Event {
    @Getter private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final String rankName;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
