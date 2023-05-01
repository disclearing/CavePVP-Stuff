package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.oitq;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class OITQListeners implements Listener {

    private final GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();

    @EventHandler(ignoreCancelled = true)
    private void onDamage(EntityDamageByEntityEvent event) {

        if (!gameHandler.isOngoingGame() || !(this.gameHandler.getOngoingGame() instanceof OITQGame)) {
            return;
        }

        OITQGame ongoingGame = (OITQGame) gameHandler.getOngoingGame();

        if (ongoingGame.getState() != GameState.RUNNING) {
            return;
        }

        if (!(event.getDamager() instanceof Arrow) || !(event.getEntity() instanceof Player)) {
            return;
        }

        if (!ongoingGame.isStarted()) {
            return;
        }

        final Arrow arrow = (Arrow) event.getDamager();

        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }

        final Player target = (Player) event.getEntity();
        final Player damager = (Player) arrow.getShooter();

        if (damager.getName().equalsIgnoreCase(target.getName())) {
            return;
        }

        if (!ongoingGame.isPlaying(target.getUniqueId()) || !ongoingGame.isPlaying(damager.getUniqueId())) {
            return;
        }

        if (!target.getLocation().getWorld().getName().equals("kits_events")) return;

        int killCount = ongoingGame.getKillsCount().getOrDefault(damager.getUniqueId(), 0)+1;

        event.setDamage(0);

        if (killCount == 20) {
            ongoingGame.getPlayers().stream().filter(it -> !it.getName().equalsIgnoreCase(damager.getName())).forEach(ongoingGame::removePlayer);
            ongoingGame.endGame();
            return;
        }

        target.closeInventory();
        target.getInventory().clear();

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            target.getInventory().addItem(new ItemStack(Material.STONE_AXE, 1));
            target.getInventory().addItem(new ItemStack(Material.BOW, 1));
            target.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        }, 5);
        target.setVelocity(new Vector(0, 0, 0));

        damager.getInventory().addItem(new ItemStack(Material.ARROW, 1));

        ongoingGame.getKillsCount().put(damager.getUniqueId(), killCount);
        ongoingGame.sendMessages(ChatColor.DARK_RED.toString() + target.getName() + ChatColor.YELLOW + " was been shot by " + ChatColor.DARK_RED + damager.getName() + ChatColor.YELLOW + "!");

        final List<Location> locationList = ongoingGame.getVotedArena().getAlternateSpawns();

        if (locationList.isEmpty()) {
            target.teleport(ongoingGame.getVotedArena().getPointA());
            return;
        }

        target.teleport(locationList.get(ThreadLocalRandom.current().nextInt(locationList.size())));
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        if (!gameHandler.isOngoingGame() || !(this.gameHandler.getOngoingGame() instanceof OITQGame)) {
            return;
        }

        OITQGame ongoingGame = (OITQGame) gameHandler.getOngoingGame();

        if (ongoingGame.getState() != GameState.RUNNING) {
            return;
        }

        final Player target = event.getEntity();
        final Player killer = target.getKiller();

        if (killer == null || !ongoingGame.isStarted()) {
            return;
        }

        if (!ongoingGame.isPlaying(target.getUniqueId()) || !ongoingGame.isPlaying(killer.getUniqueId())) {
            return;
        }

        int killCount = ongoingGame.getKillsCount().getOrDefault(killer.getUniqueId(), 0)+1;

        if (killCount == 20) {
            ongoingGame.getPlayers().stream().filter(it -> !it.getName().equalsIgnoreCase(killer.getName())).forEach(ongoingGame::removePlayer);
            ongoingGame.endGame();
            return;
        }

        event.setKeepInventory(true);

        killer.getInventory().addItem(new ItemStack(Material.ARROW, 1));

        ongoingGame.getKillsCount().put(killer.getUniqueId(), killCount);
        ongoingGame.sendMessages(ChatColor.DARK_RED.toString() + target.getName() + ChatColor.YELLOW + " was been slain by " + ChatColor.DARK_RED + killer.getName() + ChatColor.YELLOW + "!");
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent event) {
        if (!gameHandler.isOngoingGame() || !(this.gameHandler.getOngoingGame() instanceof OITQGame)) {
            return;
        }

        OITQGame ongoingGame = (OITQGame) gameHandler.getOngoingGame();

        if (ongoingGame.getState() != GameState.RUNNING) {
            return;
        }

        final Player target = event.getPlayer();

        if (!ongoingGame.isStarted()) {
            return;
        }

        if (!ongoingGame.isPlaying(target.getUniqueId()) ) {
            return;
        }

        target.closeInventory();

        target.getInventory().clear();
        target.setHealth(20);
        target.getInventory().addItem(new ItemStack(Material.STONE_AXE, 1));
        target.getInventory().addItem(new ItemStack(Material.BOW, 1));
        target.getInventory().addItem(new ItemStack(Material.ARROW, 1));

        final List<Location> locationList = ongoingGame.getVotedArena().getAlternateSpawns();

        if (locationList.isEmpty()) {
            event.setRespawnLocation(ongoingGame.getVotedArena().getPointA());
            return;
        }

        target.teleport(locationList.get(ThreadLocalRandom.current().nextInt(locationList.size())));
    }

    // cancel bow breaking XDDD
    @EventHandler(priority = EventPriority.LOW)
    private void onItemDamage(PlayerItemDamageEvent event) {
        if (!gameHandler.isOngoingGame() || !(this.gameHandler.getOngoingGame() instanceof OITQGame)) {
            return;
        }

        final Player player = event.getPlayer();

        if (event.getItem().getType() != Material.BOW) {
            return;
        }

        final OITQGame ongoingGame = (OITQGame) gameHandler.getOngoingGame();

        if (ongoingGame.getState() != GameState.RUNNING) {
            return;
        }

        if (ongoingGame.isPlaying(player.getUniqueId())) {
            return;
        }

        event.setCancelled(true);
    }
}
