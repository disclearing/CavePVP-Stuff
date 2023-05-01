package net.frozenorb.foxtrot.gameplay.pvpclasses.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.gameplay.pvpclasses.energy.EnergyEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

@AllArgsConstructor
public class BardEffectUseEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private Player source;
    @Getter
    private EnergyEffect bardEffect;
    @Getter
    private List<Player> affectedPlayers;

    @Getter
    @Setter
    private boolean cancelled;

    public BardEffectUseEvent(Player source, EnergyEffect bardEffect, List<Player> affectedPlayers) {
        this.source = source;
        this.bardEffect = bardEffect;
        this.affectedPlayers = affectedPlayers;
    }

    public HandlerList getHandlers() {
        return (handlers);
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }
}