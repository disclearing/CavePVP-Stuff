package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.shuffle;

import cc.fyre.proton.cuboid.Cuboid;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ShuffleListeners implements Listener {

    private final GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame() instanceof ShuffleGame) {
            ShuffleGame ongoingGame = (ShuffleGame) gameHandler.getOngoingGame();

            if (!ongoingGame.isPlaying(event.getPlayer().getUniqueId())) {
                return;
            }

            if (ongoingGame.getState() != GameState.RUNNING) return;

            if (!ongoingGame.getVotedArena().getBounds().expand(Cuboid.CuboidDirection.DOWN, 255).contains(event.getTo()))
                return;

            if (event.getPlayer().getLocation().getY() <= ongoingGame.getDeathHeight()) {
                ongoingGame.eliminatePlayer(event.getPlayer(), null);
            } else if (event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.LAPIS_BLOCK) {
                ongoingGame.eliminatePlayer(event.getPlayer(), null);
            }
        }
    }
}
