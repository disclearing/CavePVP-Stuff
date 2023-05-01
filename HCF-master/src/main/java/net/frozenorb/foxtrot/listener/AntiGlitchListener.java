package net.frozenorb.foxtrot.listener;

import net.minecraft.util.com.google.common.collect.ImmutableSet;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.MaterialUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.block.BlockFace.*;

public class AntiGlitchListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    private void onDrop(PlayerDropItemEvent event) {
        final ItemStack itemStack = event.getItemDrop().getItemStack();
        final Player player = event.getPlayer();
        
        if (itemStack.getType().equals(Material.WRITTEN_BOOK) || itemStack.getType().equals(Material.BOOK_AND_QUILL)) {
            event.getItemDrop().remove();
            
            player.sendMessage(ChatColor.RED + "These have been disabled!");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onSpawn(BlockDispenseEvent event) {
        if (event.getItem() != null && event.getItem().getType().equals(Material.MINECART)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        
        final ItemStack itemStack = event.getItem();

        if (itemStack == null) {
            return;
        }

        if (itemStack.getType().equals(Material.FIREWORK)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Fireworks have been disabled.");
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBookPublish(PlayerEditBookEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        event.getPlayer().sendMessage(ChatColor.RED + "What are you doing with a book?");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBookPublish(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final ItemStack itemStack = event.getItem();

        if (itemStack == null || itemStack.getType() != Material.BOOK_AND_QUILL) {
            return;
        }

        if (event.getPlayer().isOp()) {
            return;
        }

        event.getPlayer().sendMessage(ChatColor.RED + "What are you doing with a book?");
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBoatMove(VehicleMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())
            return;

        Block block = to.getBlock();
        if (block.getType() == Material.FENCE_GATE) {
            event.getVehicle().teleport(from);
            Entity passenger = event.getVehicle().getPassenger();
            if (passenger instanceof Player) {
                ((Player) passenger).sendMessage(ChatColor.RED + "You can't move your boat into a fence gate.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerBoatMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())
            return;

        Player player = event.getPlayer();
        if (player.getVehicle() != null && player.getVehicle() instanceof Boat) {
            if (to.getBlock().getType() == Material.FENCE_GATE) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You can't move your boat into a fence gate.");
            }
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (event.getBlock().getType().name().contains("RAIL")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (block.getType().name().contains("RAIL")) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleExit(VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getExited();
        Location location = player.getLocation();

        while (location.getBlock().getType().isSolid()) {
            location.add(0, 1, 0);
            if (location.getBlockY() == 255) {
                break;
            }
        }

        while (location.getBlock().getType().isSolid()) {
            location.subtract(0, 1, 0);
            if (location.getBlockY() == 1) {
                break;
            }
        }

        final Location locationFinal = location;

        new BukkitRunnable() {

            public void run() {
                player.teleport(locationFinal);
            }

        }.runTaskLater(Foxtrot.getInstance(), 1L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleEnter(VehicleEnterEvent event) {

        if (event.getVehicle() instanceof Horse || event.getVehicle() instanceof Minecart) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getWorld().getEnvironment() != World.Environment.NETHER) {
            return;
        }

        if (event.getEntity() instanceof Skeleton) {
            event.getDrops().removeIf(item -> item.getType() == Material.SKULL_ITEM);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE || player.getWorld().getEnvironment() != World.Environment.NETHER) {
            return;
        }

        if (event.getBlock().getType() == Material.MOB_SPAWNER) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You aren't allowed to place mob spawners in the nether.");
        }
    }

    private static final ImmutableSet<BlockFace> SURROUNDING = ImmutableSet.of(SELF, NORTH, NORTH_EAST, NORTH_WEST, SOUTH, SOUTH_EAST, SOUTH_WEST, EAST, WEST, UP);

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void denyDismountClipping(VehicleExitEvent event) {
        // Do nothing if exited was not a player.
        if (!(event.getExited() instanceof Player))
            return;

        // Do nothing if player has permission.
        Player player = (Player) event.getExited();

        // Locate a safe position to teleport the player.
        Location pLoc = player.getLocation();
        Location vLoc = event.getVehicle().getLocation();
        if (player.getLocation().getY() > 250.0D) {
            pLoc.add(0, 10, 0);
        } else if (!MaterialUtils.isFullBlock(vLoc.add(0.0D, 1.0D, 0.0D).getBlock().getType())) {
            // If the vehicles' position is safe, teleport the player into the center of the
            // block, otherwise below.
            if (!MaterialUtils.isFullBlock(vLoc.getBlock().getType())) {
                pLoc = new Location(vLoc.getWorld(), vLoc.getBlockX() + 0.5, vLoc.getBlockY(), vLoc.getBlockZ() + 0.5, pLoc.getYaw(), pLoc.getPitch());
            } else {
                pLoc.subtract(0, 1, 0);
            }
        }

        final Location finalLocation = pLoc;
        // Teleport player to the safe location on the next tick.
        Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> player.teleport(finalLocation));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void denyDismountClipping(CreatureSpawnEvent event) {
        // Do nothing if entity is not a horse.
        if (event.getEntityType() != EntityType.HORSE)
            return;

        if (Foxtrot.getInstance().getServerHandler().isHardcore()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void denyDismountGlitching(PlayerDeathEvent event) {
        if (event.getEntity().isInsideVehicle())
            event.getEntity().getVehicle().remove();
    }

    @EventHandler
    public void denyMinecartSpawns(VehicleCreateEvent event) {
        EntityType type = event.getVehicle().getType();

        if (type == EntityType.MINECART_HOPPER || type == EntityType.MINECART_CHEST) {
            event.getVehicle().remove();
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        for (Entity entity : chunk.getEntities()) {
            if (entity.getType() == EntityType.MINECART_CHEST) {
                entity.remove();
            }
        }
    }

//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void pearl(PlayerTeleportEvent event) {
//        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
//
//        Location location = event.getTo();
//
//        location.setX(location.getBlockX() + 0.5D);
//        location.setY(location.getBlockY() + 0.5D);
//        location.setZ(location.getBlockZ() + 0.5D);
//    }
}
