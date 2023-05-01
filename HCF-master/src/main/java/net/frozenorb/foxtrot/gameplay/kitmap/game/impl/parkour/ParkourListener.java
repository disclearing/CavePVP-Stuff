package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.parkour;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ParkourListener implements Listener {

    private final GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (!this.gameHandler.isOngoingGame() || !(this.gameHandler.getOngoingGame() instanceof ParkourGame)) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getTo().getBlock();
        final ParkourGame ongoingGame = (ParkourGame) this.gameHandler.getOngoingGame();

        if (!ongoingGame.getPlayers().contains(player)) {
            return;
        }

        if (!ongoingGame.isStarted() && ongoingGame.getState() == GameState.RUNNING) {
            event.setCancelled(true);
            return;
        }

        if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER || block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA) {
            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> block.setType(Material.OBSIDIAN), 20);
            player.teleport(ongoingGame.getVotedArena().getPointA());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player target = (Player) event.getEntity();


        if (!this.gameHandler.isOngoingGame() || !(this.gameHandler.getOngoingGame() instanceof ParkourGame)) {
            return;
        }

        final ParkourGame ongoingGame = (ParkourGame) this.gameHandler.getOngoingGame();

        if (!ongoingGame.getPlayers().contains(target)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (event.getAction() != Action.PHYSICAL || event.getClickedBlock() == null) {
            return;
        }

        if (!this.gameHandler.isOngoingGame() || !(this.gameHandler.getOngoingGame() instanceof ParkourGame)) {
            return;
        }

        final ParkourGame ongoingGame = (ParkourGame) this.gameHandler.getOngoingGame();

        if (!ongoingGame.getPlayers().contains(player)) {
            return;
        }

        if (!ongoingGame.isStarted() || ongoingGame.getState() == GameState.ENDED) {
            return;
        }

        final Block block = event.getClickedBlock();

        if (block.getType().name().contains("PLATE")) {
            ongoingGame.getPlayers().stream().filter(it -> it != player).forEach(it -> ongoingGame.eliminatePlayer(it, null));
        }
    }
}