package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.sumo;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class SumoListeners implements Listener {

    private final GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame() instanceof SumoGame) {
            SumoGame ongoingGame = (SumoGame) gameHandler.getOngoingGame();
            if (ongoingGame.isCurrentlyFighting(event.getPlayer())) {
                if (System.currentTimeMillis() < ongoingGame.getStartedAt() + 6_000L) {
                    event.setCancelled(true);
                    event.getPlayer().teleport(event.getFrom());
                    return;
                }

                if (event.getPlayer().getLocation().getY() <= ongoingGame.getDeathHeight() || isLava(event.getTo())) {
                    ongoingGame.eliminatePlayer(event.getPlayer(), ongoingGame.getOpponent(event.getPlayer()));
                }
            }
        }
    }

    private boolean isLava(Location location) {
        try {
            Block block = location.getBlock();
            return block.getType().name().contains("LAVA");
        } catch (Exception ignored) {}
        return false;
    }

}
