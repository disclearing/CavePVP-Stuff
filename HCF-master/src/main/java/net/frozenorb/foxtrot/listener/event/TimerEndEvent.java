package net.frozenorb.foxtrot.listener.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
@Getter
public class TimerEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private long duration;
    private String displayName;

    public HandlerList getHandlers() {
        return (handlers);
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }

}
