package net.frozenorb.foxtrot.listener.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;
import lombok.Setter;

public class BackstabKillEvent extends PlayerEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    @Getter private final Player killed;
    @Getter @Setter private boolean allowed = false;

    public BackstabKillEvent(Player who, Player killed) {
        super(who);
        this.killed = killed;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
