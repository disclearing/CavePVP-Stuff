package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.spleef;

import cc.fyre.proton.cuboid.Cuboid;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemsEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class SpleefListeners implements Listener {

    private final GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame() instanceof SpleefGame) {
            SpleefGame ongoingGame = (SpleefGame) gameHandler.getOngoingGame();

            if (ongoingGame.getState() != GameState.RUNNING) return;

            if (!ongoingGame.getVotedArena().getBounds().expand(Cuboid.CuboidDirection.DOWN, 256).contains(event.getTo())) {
                return;
            }

            if (!ongoingGame.isPlaying(event.getPlayer().getUniqueId())) {
                return;
            }

            if (event.getPlayer().getLocation().getY() <= ongoingGame.getDeathHeight()) {
                ongoingGame.eliminatePlayer(event.getPlayer(), null);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame() instanceof SpleefGame) {
            SpleefGame ongoingGame = (SpleefGame) gameHandler.getOngoingGame();

            if (event.getEntityType() != EntityType.SNOWBALL) return;

            if (ongoingGame.getState() != GameState.RUNNING) return;

            Location location = event.getEntity().getLocation();
            Block block = location.getBlock().getRelative(BlockFace.DOWN);

            if (block != null && block.getType() == Material.SNOW_BLOCK) {
                block.setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onBlockDropItems(BlockDropItemsEvent event) {

        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return;
        }

        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame() instanceof SpleefGame) {
            SpleefGame ongoingGame = (SpleefGame) gameHandler.getOngoingGame();

            if (ongoingGame.getState() != GameState.RUNNING || !ongoingGame.isPlaying(event.getPlayer().getUniqueId())) {
                return;
            }

            for (Item item : event.getToDrop()) {
                ItemStack itemStack = item.getItemStack();
                itemStack.setType(Material.SNOW_BALL);
                event.getPlayer().getInventory().addItem(itemStack);
            }

            event.getToDrop().clear();
        }
    }

}
