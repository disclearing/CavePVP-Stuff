package org.bukkit.event.player;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerAttackEvent extends PlayerEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    @Getter private final Entity target;

    public PlayerAttackEvent(Player player, Entity target) {
        super(player);

        this.target = target;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
