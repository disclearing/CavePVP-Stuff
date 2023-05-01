package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.knockout;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class KnockoutListeners implements Listener {

    private final GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!this.gameHandler.isOngoingGame() || !(this.gameHandler.getOngoingGame() instanceof KnockoutGame)) {
            return;
        }

        final Player player = (Player) event.getEntity();
        final KnockoutGame ongoingGame = (KnockoutGame) this.gameHandler.getOngoingGame();

        if (!ongoingGame.getPlayers().contains(player)) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            ongoingGame.eliminatePlayer(player, null);
            event.setCancelled(true);
        }
    }

}
