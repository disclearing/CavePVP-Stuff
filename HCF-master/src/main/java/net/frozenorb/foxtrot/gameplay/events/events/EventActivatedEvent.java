package net.frozenorb.foxtrot.gameplay.events.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
 public class EventActivatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private net.frozenorb.foxtrot.gameplay.events.Event event;

    public HandlerList getHandlers() {
        return (handlers);
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }

}