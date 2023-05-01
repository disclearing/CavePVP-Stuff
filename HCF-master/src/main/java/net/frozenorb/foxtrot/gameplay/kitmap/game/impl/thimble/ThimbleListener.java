package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.thimble;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ThimbleListener implements Listener {

    private final GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (!this.gameHandler.isOngoingGame() || !(this.gameHandler.getOngoingGame() instanceof ThimbleGame)) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getTo().getBlock();
        final ThimbleGame ongoingGame = (ThimbleGame) this.gameHandler.getOngoingGame();

        if (!ongoingGame.getPlayers().contains(player)) {
            return;
        }

        if (!ongoingGame.isStarted() && ongoingGame.getState() != GameState.ENDED) {
            event.setCancelled(true);
            return;
        }

        if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER || block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA) {
            player.teleport(ongoingGame.getVotedArena().getPointA());

            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> block.setType(Material.OBSIDIAN), 3);
        }
    }
}