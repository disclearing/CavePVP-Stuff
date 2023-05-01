package net.frozenorb.foxtrot.listener;

import cc.fyre.proton.util.PlayerUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.spigotmc.SpigotConfig;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BasicPreventionListener implements Listener {

    private static final List<EntityType> PREVENT_MOBS = Arrays.asList(EntityType.CREEPER, EntityType.SKELETON, EntityType.ZOMBIE, EntityType.SPIDER);

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerKickEvent event) {
        event.setLeaveMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Wither) {
            event.setCancelled(true);
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getGameMode() != GameMode.CREATIVE) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.ENDER_CHEST) {
            event.setCancelled(true);
        }
    }

    private static final List<String> NAMES = Arrays.asList("SimplyTrash", "Rowin");

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        final String message = event.getMessage();
        final Player player = event.getPlayer();

        if ((message.startsWith("/co l")
                || message.startsWith("/core l")
                || message.startsWith("/coreprotect l"))
                && !NAMES.contains(player.getName())
                && messageContainsName(message)) {
            event.getPlayer().sendMessage(ChatColor.RED + "No permission.");
            event.setCancelled(true);
        }
    }

    private boolean messageContainsName(String message) {
        for (String name : NAMES) {
            if (StringUtils.containsIgnoreCase(message, name)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getVehicle() instanceof Horse && event.getEntered() instanceof Player) {
            Horse horse = (Horse) event.getVehicle();
            Player player = (Player) event.getEntered();

            if (horse.getOwner() != null && !horse.getOwner().getName().equals(player.getName())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "This is not your horse!");
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (CustomTimerCreateCommand.isSOTWTimer()) {
            event.setCancelled(true);
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation()) && event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel()) {
            event.setCancelled(true);
            return;
        }

        if (event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel()) {
            // Make food drop 1/2 as fast if you have PvP protection
            if (ThreadLocalRandom.current().nextInt(100) > (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getEntity().getUniqueId()) ? 10 : 30)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(event.getPlayer())) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Foxtrot.getInstance(), () -> {
            event.getPlayer().setMetadata("PVP_TIMER_BYPASS", new FixedMetadataValue(Foxtrot.getInstance(), true));
            Foxtrot.getInstance().getPvPTimerMap().createTimer(event.getPlayer().getUniqueId(), 30 * 60);//moved inside here due to occasional CME maybe this will fix?
        }, 20L);

        event.setRespawnLocation(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (Foxtrot.getInstance().getServerHandler().isWarzone(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (Foxtrot.getInstance().getServerHandler().isSkybridgePrevention() && 110 < event.getBlock().getLocation().getY() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can't build higher than 110 blocks.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFish(PlayerInteractEvent event) {
        if ((Foxtrot.getInstance().getServerHandler().isEOTW() || Foxtrot.getInstance().getServerHandler().isRodPrevention()) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.FISHING_ROD) {
            event.getPlayer().sendMessage(ChatColor.RED + "Rods are currently disabled!");
            event.setCancelled(true);
        }
    }

    /**
     * Prevents creepers from exploding.
     */
    @EventHandler
    public void onEntityExplodeCreeper(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Creeper) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents the mobs listed in PREVENT_MOBS from spawning in claims with the SAFE_ZONE bitmask.
     */
    @EventHandler
    public void onCreatureSpawnSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL && PREVENT_MOBS.contains(event.getEntityType())) {
            Team team = LandBoard.getInstance().getTeam(event.getLocation());
            if (team != null && team.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Prevents mobs from randomly spawning all over the world.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawnAnywhere(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL
                || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CHUNK_GEN
                || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.MOUNT
                || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.JOCKEY) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents Wither Skeletons from spawning.
     */
    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawnWither(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL
                && event.getEntity().getType() == EntityType.SKELETON
                && ((Skeleton) event.getEntity()).getSkeletonType() == Skeleton.SkeletonType.WITHER) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER && (event.getBlock().getType() == Material.BED ||
                event.getBlock().getType() == Material.BED_BLOCK)) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlockPlaced().getLocation())) {
                return;
            }
            if (event.isCancelled()) {
                event.getPlayer().setItemInHand(null);
            } else {
                event.getBlock().setType(Material.AIR);
            }

            event.getPlayer().getWorld().createExplosion(event.getBlock().getLocation(), 5);
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFireBurn(BlockBurnEvent event) {
        if (Foxtrot.getInstance().getServerHandler().isWarzone(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(null, event.getBlock().getLocation())) {
            return;
        }

        if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        EntityType type = event.getEntityType();

        if (type == EntityType.MINECART_TNT) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!CustomTimerCreateCommand.isSOTWTimer()) {
            return;
        }

        Player damager = PlayerUtils.getDamageSource(event.getDamager());
        Entity damaged = event.getEntity();

        if (!(damaged instanceof Player)) {
            return;
        }

        if (damager != null && !CustomTimerCreateCommand.hasSOTWEnabled(damager.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && CustomTimerCreateCommand.isSOTWTimer() && !CustomTimerCreateCommand.hasSOTWEnabled(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
