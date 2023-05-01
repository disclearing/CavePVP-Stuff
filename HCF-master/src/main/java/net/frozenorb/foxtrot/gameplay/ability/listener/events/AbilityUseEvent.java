package net.frozenorb.foxtrot.gameplay.ability.listener.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class AbilityUseEvent extends Event implements Cancellable {
    @Getter private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final Player target;
    private final Location chosenLocation;
    private final Ability ability;
    private boolean cancelled;
    private boolean oneHit;

    public AbilityUseEvent(Player player, Player target, Location chosenLocation, Ability ability, boolean oneHit) {
        this.player = player;
        this.target = target;
        this.chosenLocation = chosenLocation;
        this.ability = ability;
        this.oneHit = oneHit;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
