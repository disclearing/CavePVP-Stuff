package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.Portal;
import net.frozenorb.foxtrot.util.PortalDirection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class PortalTrapListener implements Listener {

    public static final BlockFace[] FACES = new BlockFace[]{
            BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST
    };
    private Map<UUID, Long> lastMessaged = new HashMap<>();

    @EventHandler
    public void onPortal(final PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) return;

        new BukkitRunnable() {
            public void run() {
                Player player = event.getPlayer();
                Location center = player.getLocation();

                Block block = null;
                for (BlockFace face : FACES) {
                    if (center.getBlock().getRelative(face).getType() == Material.PORTAL) {
                        block = center.getBlock().getRelative(face);
                    }
                }
                if (block == null) {
                    if (center.getBlock().getType() == Material.PORTAL) {
                        block = center.getBlock();
                    }
                }
                if (block == null) {
                    return;
                }

                PortalDirection dir = null;
                if (block.getRelative(BlockFace.NORTH).getType() == Material.PORTAL || block.getRelative(BlockFace.SOUTH).getType() == Material.PORTAL) {
                    dir = PortalDirection.NORTH_SOUTH;
                } else if (block.getRelative(BlockFace.EAST).getType() == Material.PORTAL || block.getRelative(BlockFace.WEST).getType() == Material.PORTAL) {
                    dir = PortalDirection.EAST_WEST;
                }

                Portal portal = new Portal(block, dir);

                if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
                    if (Math.abs(player.getLocation().getX()) <= 10 && Math.abs(player.getLocation().getZ()) <= 10) {
                        return;
                    }

                    portal.patchNether();
                } else if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
                    portal.patchOverworld();
                }
            }
        }.runTaskLater(Foxtrot.getInstance(), 1L);
    }

    @EventHandler
    public void onPortalCreate(final PortalCreateEvent event) {
        final List<Block> blocks = new ArrayList<>(event.getBlocks());

        for (Block block : event.getBlocks()) {
            if (block.getType() != Material.AIR && block.getType() != Material.FIRE) blocks.remove(block);
        }

        for (Block block : blocks) {
            for (BlockFace face : FACES) {
                if (block.getRelative(face).getType() == Material.PORTAL) {
                    event.setCancelled(true);
                    for (Entity entity : getNearbyEntities(block.getLocation(), 5)) {
                        if (entity instanceof Player) {
                            Player player = (Player) entity;
                            if (!lastMessaged.containsKey(player.getUniqueId())) {
                                lastMessaged.put(player.getUniqueId(), -1L);
                            }

                            if (System.currentTimeMillis() - lastMessaged.get(player.getUniqueId()) > 5000) {
                                player.sendMessage(ChatColor.RED + "You can't create a portal touching another portal!");
                                lastMessaged.put(player.getUniqueId(), System.currentTimeMillis());
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        for(BlockFace face : FACES) {
            if(event.getBlock().getRelative(face).getType() == Material.PORTAL) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onBlockForm(EntityChangeBlockEvent event) {
        for(BlockFace face : FACES) {
            if(event.getBlock().getRelative(face).getType() == Material.PORTAL) {
                event.setCancelled(true);
                break;
            }
        }
    }

    private List<Entity> getNearbyEntities(Location l, int radius) {
        return l.getWorld().getPlayers().stream().filter(e -> l.distance(e.getLocation()) <= radius).collect(Collectors.toList());
    }
}
