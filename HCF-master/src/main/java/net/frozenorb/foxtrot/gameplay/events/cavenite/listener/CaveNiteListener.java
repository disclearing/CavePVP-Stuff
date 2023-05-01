package net.frozenorb.foxtrot.gameplay.events.cavenite.listener;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteHandler;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteState;
import net.frozenorb.foxtrot.gameplay.events.cavenite.SpectateMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.suge.kit.event.KitUseEvent;

@AllArgsConstructor
public class CaveNiteListener implements Listener {
    private Foxtrot instance;
    private CaveNiteHandler caveNiteHandler;

    @EventHandler(priority = EventPriority.LOW)
    private void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();

        if (this.caveNiteHandler.getGameState() != CaveNiteState.RUNNING) {
            return;
        }

        if (!this.caveNiteHandler.getOnlinePlayers().contains(player)) {
            return;
        }

        this.caveNiteHandler.disqualify(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDrop(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final CaveNiteHandler caveNiteHandler = this.caveNiteHandler;

        if (this.caveNiteHandler.getPlayersRemaining().contains(player.getUniqueId()) && this.caveNiteHandler.getGameState().equals(CaveNiteState.SCATTERING)) {
            event.getItemDrop().remove();
        }

        if (caveNiteHandler.getGameState() == CaveNiteState.INACTIVE) {
            return;
        }

        if (this.caveNiteHandler.getSpectators().contains(player.getUniqueId()) && this.caveNiteHandler.getGameState() != CaveNiteState.INACTIVE) {
            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.LOW)
    private void onPickup(PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        final CaveNiteHandler caveNiteHandler = this.caveNiteHandler;

        if (caveNiteHandler.getGameState() == CaveNiteState.INACTIVE) {
            return;
        }

        if (this.caveNiteHandler.getSpectators().contains(player.getUniqueId()) && this.caveNiteHandler.getGameState() != CaveNiteState.INACTIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (this.caveNiteHandler.getGameState() != CaveNiteState.RUNNING) {
            return;
        }

        if (!this.caveNiteHandler.getOnlinePlayers().contains(player)) {
            return;
        }

        this.caveNiteHandler.disqualify(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onKitUse(KitUseEvent event) {
        if (this.caveNiteHandler.getGameState() == CaveNiteState.INACTIVE) {
            return;
        }

        if (event.getPlayer().isOp()) {
            return;
        }

        event.getPlayer().sendMessage(ChatColor.RED + "Cant use a kit now!");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onMove(PlayerMoveEvent event) {
        if (this.caveNiteHandler.getGameState() == CaveNiteState.INACTIVE) {
            return;
        }

        if (event.isCancelled() || event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        final CaveNiteHandler caveNiteHandler = this.caveNiteHandler;

        if (this.caveNiteHandler.getGameState() == CaveNiteState.SCATTERING && caveNiteHandler.getOnlinePlayers().contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onTeleport(PlayerTeleportEvent event) {
        if (this.caveNiteHandler.getGameState() == CaveNiteState.INACTIVE) {
            return;
        }

        if (event.isCancelled() || event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        final CaveNiteHandler caveNiteHandler = this.caveNiteHandler;

        if (this.caveNiteHandler.getGameState() == CaveNiteState.SCATTERING && caveNiteHandler.getOnlinePlayers().contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        final CaveNiteHandler caveNiteHandler = this.caveNiteHandler;

        if (caveNiteHandler.getGameState() == CaveNiteState.INACTIVE) {
            return;
        }

        final Player damager = (Player) event.getDamager();

        if (this.caveNiteHandler.getSpectators().contains(damager.getUniqueId()) && this.caveNiteHandler.getGameState() != CaveNiteState.INACTIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (caveNiteHandler.getGameState() == CaveNiteState.INACTIVE) {
            return;
        }

        if (!this.caveNiteHandler.getOnlineSpectators().contains(player)) {
            return;
        }

        if (event.getClickedBlock() != null && event.getClickedBlock().getType().name().contains("CHEST")) {
            event.setCancelled(true);
            return;
        }

        final ItemStack itemStack = event.getItem();

        if (itemStack == null || itemStack.getType() != Material.WATCH) {
            return;
        }

        new SpectateMenu().openMenu(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageEvent event) {
        if (this.caveNiteHandler.getGameState() == CaveNiteState.INACTIVE) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        if (this.caveNiteHandler.getSpectators().contains(player.getUniqueId()) && this.caveNiteHandler.getGameState() != CaveNiteState.INACTIVE) {
            event.setCancelled(true);
            return;
        }

        if (!this.caveNiteHandler.getOnlinePlayers().contains(player)) {
            return;
        }

        if (this.caveNiteHandler.getGameState() == CaveNiteState.RUNNING) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onBreak(BlockBreakEvent event) {

        final Player player = event.getPlayer();

        if (player.getWorld().getName().equalsIgnoreCase("sg") && !player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlace(BlockPlaceEvent event) {

        final Player player = event.getPlayer();

        if (player.getWorld().getName().equalsIgnoreCase("sg") && !player.isOp()) {
            event.setCancelled(true);
        }
    }

}