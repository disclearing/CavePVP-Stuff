package cc.fyre.proton.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class HalfHourEvent extends Event {

    @Getter private static final HandlerList handlerList = new HandlerList();
    @Getter private int hour;
    @Getter private int minute;

    public HandlerList getHandlers() {
        return (handlerList);
    }

}