package net.frozenorb.foxtrot.team.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
@Getter
@Setter
public class TeamEnterClaimEvent extends Event implements Cancellable {
    @Getter private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final Location from;
    private final Location to;
    private final Team fromTeam;
    private final Team toTeam;
    private boolean cancelled;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
