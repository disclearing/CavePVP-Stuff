package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.tntrun;

import cc.fyre.proton.cuboid.Cuboid;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TNTRunListeners implements Listener {

    private final GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {

        if (!gameHandler.isOngoingGame() || !(this.gameHandler.getOngoingGame() instanceof TNTRunGame)) {
            return;
        }

        TNTRunGame ongoingGame = (TNTRunGame) gameHandler.getOngoingGame();

        if (ongoingGame.getState() != GameState.RUNNING) {
            return;
        }

        if (!ongoingGame.getVotedArena().getBounds().expand(Cuboid.CuboidDirection.DOWN, 256).contains(event.getTo())) {
            return;
        }

        if (!ongoingGame.isPlaying(event.getPlayer().getUniqueId())) {
            return;
        }

        if (!ongoingGame.isStarted()) {
            return;
        }

        final Block from = event.getFrom().getBlock();
        final Block to = event.getTo().getBlock();

        if (from.getType().name().contains("WATER") || to.getType().name().contains("WATER") || from.getType().name().contains("LAVA") || to.getType().name().contains("LAVA")) {
            ongoingGame.eliminatePlayer(event.getPlayer(), null);
        }

//        if (to.getRelative(BlockFace.DOWN).getType() == Material.TNT) {
//            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () ->
//                    to.getRelative(BlockFace.DOWN).setType(Material.AIR), 7);
//        }
    }

}
