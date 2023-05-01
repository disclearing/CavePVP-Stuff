package net.frozenorb.foxtrot.listener;

import cc.fyre.neutron.util.PlayerUtil;
import cc.fyre.proton.Proton;
import cc.fyre.proton.util.PlayerUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.RegenUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class SpawnListener implements Listener {

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() != null) {
            if (Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
                return;
            }
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ() && to.getBlockY() == from.getBlockY()) {
            return;
        }

        if (!DTRBitmask.SAFE_ZONE.appliesAt(to)) {
            return;
        }

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return;
        }

        if (event.getPlayer().getLocation().getBlock().getType() == Material.WATER || event.getPlayer().getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
            if (from.getWorld().getName().equalsIgnoreCase("Spawn")) {

                final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

                if (team == null) {
                    PlayerUtil.sendTitle(event.getPlayer(), "&4&lGet Started", "&fCreate a faction by typing /f create [name]");
                } else if (team.getClaims().isEmpty()) {
                    PlayerUtil.sendTitle(event.getPlayer(), "&4&lGet Started", "&fType &c/rtp &fto start your claiming process!");
                }

                event.getPlayer().teleport(Foxtrot.getInstance().getServer().getWorld("world").getSpawnLocation().clone());
                return;
            }

            final Block belowBlock = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);

            if (belowBlock.getType().name().contains("GLOWSTONE")) {
                return;
            }

            event.getPlayer().teleport(Foxtrot.getInstance().getServer().getWorld("Spawn").getSpawnLocation().clone());
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (Foxtrot.getInstance().getMapHandler().isKitMap() && event.getBlockPlaced().getType().equals(Material.WEB) && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Cobwebs have been disabled!");
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in spawn!");
        } else if (Foxtrot.getInstance().getServerHandler().isSpawnBufferZone(event.getBlock().getLocation()) || Foxtrot.getInstance().getServerHandler().isNetherBufferZone(event.getBlock().getLocation())) {
            if (!DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation()) && event.getItemInHand() != null && event.getItemInHand().getType() == Material.WEB && (Foxtrot.getInstance().getMapHandler().isKitMap())) {

                if (event.getBlockAgainst() != null && event.getBlockAgainst().getType().name().contains("WATER") || event.getBlock() != null && event.getBlock().getType().name().contains("WATER")) {
                    event.setCancelled(true);

                    event.getPlayer().sendMessage(ChatColor.RED + "Can't place cobwebs on Water!");
                    return;
                }

                for (Event playableEvent : Foxtrot.getInstance().getEventHandler().getEvents()) {
                    if (!playableEvent.isActive() || !(playableEvent instanceof KOTH)) {
                        continue;
                    }
                    
                    KOTH koth = (KOTH) playableEvent;

                    if (koth.onCap(event.getBlockPlaced().getLocation())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "You can't place web on cap!");
                        event.getPlayer().setItemInHand(null);
                        
                        event.getPlayer().setMetadata("ImmuneFromGlitchCheck", new FixedMetadataValue(Foxtrot.getInstance(), new Object()));
                        
                        Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> {
                            event.getPlayer().removeMetadata("ImmuneFromGlitchCheck", Foxtrot.getInstance());
                        });
                        
                        return;
                    }
                }

                if (DTRBitmask.CITADEL.appliesAt(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build on Citadel!");

                    event.getPlayer().setMetadata("ImmuneFromGlitchCheck", new FixedMetadataValue(Foxtrot.getInstance(), new Object()));

                    Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> {
                        event.getPlayer().removeMetadata("ImmuneFromGlitchCheck", Foxtrot.getInstance());
                    });
                    return;
                }

                if (Proton.getInstance().getAutoRebootHandler().isRebooting()) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "May not place webs whilst the server is rebooting.");
                    return;
                }
                
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (event.getBlock().getType() == Material.WEB) {
                            event.getBlock().setType(Material.AIR);
                        }
                    }

                }.runTaskLater(Foxtrot.getInstance(), 10 * 20L);
            } else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build this close to spawn!");
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in spawn!");
        } else if (!DTRBitmask.DTC.appliesAt(event.getBlock().getLocation()) && (Foxtrot.getInstance().getServerHandler().isSpawnBufferZone(event.getBlock().getLocation()) || Foxtrot.getInstance().getServerHandler().isNetherBufferZone(event.getBlock().getLocation()))) {
            event.setCancelled(true);

            if (event.getBlock().getType() != Material.LONG_GRASS && event.getBlock().getType() != Material.GRASS) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build this close to spawn!");
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingPlace(HangingPlaceEvent event) {
        if (Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player) || Foxtrot.getInstance().getServerHandler().isAdminOverride((Player) event.getRemover())) {
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME || Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getRightClicked().getLocation())) {
            event.setCancelled(true);
        }
    }

    // Used for item frames
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || event.getEntity().getType() != EntityType.ITEM_FRAME || Foxtrot.getInstance().getServerHandler().isAdminOverride((Player) event.getDamager())) {
            return;
        }

        if (!DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isSpawnBufferZone(event.getBlockClicked().getLocation())) {
            if (!Foxtrot.getInstance().getServerHandler().isWaterPlacementInClaimsAllowed()) {
                event.setCancelled(true);
            } else {
                final Block waterBlock = event.getBlockClicked().getRelative(event.getBlockFace());

                if (waterBlock.getRelative(BlockFace.NORTH).isLiquid() || waterBlock.getRelative(BlockFace.SOUTH).isLiquid() || waterBlock.getRelative(BlockFace.EAST).isLiquid() || waterBlock.getRelative(BlockFace.WEST).isLiquid()) {
                    event.setCancelled(true);
                    return;
                }

                RegenUtils.schedule(waterBlock, 30, TimeUnit.SECONDS, (block) -> InventoryUtils.fillBucket(event.getPlayer()), (block) -> true);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity().getWorld().getName().equalsIgnoreCase("iMake") && event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            return;
        }

        if ((event.getEntity() instanceof Player || event.getEntity() instanceof Horse) && DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamageByEntity2(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = PlayerUtils.getDamageSource(event.getDamager()); // find the player damager if one exists

        if (damager != null) {
            Player victim = (Player) event.getEntity();

            if (DTRBitmask.SAFE_ZONE.appliesAt(victim.getLocation()) || DTRBitmask.SAFE_ZONE.appliesAt(damager.getLocation())) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        Team team = LandBoard.getInstance().getTeam(event.getPlayer().getLocation());
        if (team != null && team.hasDTRBitmask(DTRBitmask.SAFE_ZONE) && Foxtrot.getInstance().getMapHandler().isKitMap()) {
            if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                event.getItemDrop().remove();
            } else if (CustomTimerCreateCommand.isSOTWTimer()) {
                event.getItemDrop().remove();
            }
        }
    }

}