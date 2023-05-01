package net.frozenorb.foxtrot.persist.maps.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
@Getter
public class SyncPlaytimeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;

    public HandlerList getHandlers() {
        return (handlers);
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }

}