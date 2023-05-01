package cc.fyre.proton.nametag.construct;

import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * A nametag update that is queued to happen.
 * Commonly the update is queued from a sync. thread.
 */
public final class NameTagUpdate {

    @Getter private String toRefresh;
    @Getter private String refreshFor;

    /**
     * Refreshes one player for all players online.
     *
     * @param toRefresh The player to refresh.
     */
    public NameTagUpdate(Player toRefresh) {
        this.toRefresh = toRefresh.getName();
    }

    /**
     * Refreshes one player for another player only.
     *
     * @param toRefresh  The player to refresh.
     * @param refreshFor The player to refresh toRefresh for.
     */
    public NameTagUpdate(Player toRefresh,Player refreshFor) {
        this.toRefresh = toRefresh.getName();
        this.refreshFor = refreshFor.getName();
    }

}