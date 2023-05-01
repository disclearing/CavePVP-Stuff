package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerHealthChangeEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final double previousHealth;
    private final double newHealth;

    public PlayerHealthChangeEvent(final Player player, final double previousHealth, final double newHealth) {
        super(player);

        this.previousHealth = previousHealth;
        this.newHealth = newHealth;
    }

    public double getPreviousHealth() {
        return previousHealth;
    }

    public double getNewHealth() {
        return newHealth;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
