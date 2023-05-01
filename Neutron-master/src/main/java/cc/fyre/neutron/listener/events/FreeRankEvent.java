package cc.fyre.neutron.listener.events;

import cc.fyre.neutron.profile.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
@Getter
public class FreeRankEvent extends Event {
    @Getter private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final Profile profile;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
