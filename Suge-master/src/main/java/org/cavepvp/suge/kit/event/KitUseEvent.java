package org.cavepvp.suge.kit.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.cavepvp.suge.kit.data.Kit;

public class KitUseEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter private Player player;
    @Getter private Kit kit;

    public KitUseEvent(Player player, Kit kit) {
        this.player = player;
        this.kit = kit;
    }

    @Getter @Setter private boolean cancelled;

    public HandlerList getHandlers() {
        return (handlers);
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}