package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BorderListener implements Listener {

    public static int BORDER_SIZE = 3000;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Math.abs(event.getBlock().getX()) > getBorderSize(event.getBlock().getWorld()) || Math.abs(event.getBlock().getZ()) > getBorderSize(event.getBlock().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Math.abs(event.getBlock().getX()) > getBorderSize(event.getBlock().getWorld()) || Math.abs(event.getBlock().getZ()) > getBorderSize(event.getBlock().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (Math.abs(event.getTo().getBlockX()) > getBorderSize(event.getTo().getWorld()) || Math.abs(event.getTo().getBlockZ()) > getBorderSize(event.getTo().getWorld())) {
            Location newLocation = event.getTo().clone();

            while (Math.abs(newLocation.getX()) > getBorderSize(newLocation.getWorld())) {
                newLocation.setX(newLocation.getX() - (newLocation.getX() > 0 ? 1 : -1));
            }

            while (Math.abs(newLocation.getZ()) > getBorderSize(newLocation.getWorld())) {
                newLocation.setZ(newLocation.getZ() - (newLocation.getZ() > 0 ? 1 : -1));
            }

            event.setTo(newLocation);
            event.getPlayer().sendMessage(ChatColor.RED + "That portal's location is past the border. It has been moved inwards.");
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!event.getTo().getWorld().equals(event.getFrom().getWorld())) {
            return;
        }

        if (event.getTo().distance(event.getFrom()) < 0 || event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            return;
        }

        if (Math.abs(event.getTo().getBlockX()) > getBorderSize(event.getTo().getWorld()) || Math.abs(event.getTo().getBlockZ()) > getBorderSize(event.getTo().getWorld())) {
            Location newLocation = event.getTo().clone();

            while (Math.abs(newLocation.getX()) > getBorderSize(newLocation.getWorld())) {
                newLocation.setX(newLocation.getX() - (newLocation.getX() > 0 ? 1 : -1));
            }

            while (Math.abs(newLocation.getZ()) > getBorderSize(newLocation.getWorld())) {
                newLocation.setZ(newLocation.getZ() - (newLocation.getZ() > 0 ? 1 : -1));
            }

            while (newLocation.getBlock().getType() != Material.AIR) {
                newLocation.setY(newLocation.getBlockY() + 1);
            }

            event.setTo(newLocation);
            event.getPlayer().sendMessage(ChatColor.RED + "That location is past the border.");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ() || from.getBlockY() != to.getBlockY()) {
            if (Math.abs(event.getTo().getBlockX()) > getBorderSize(event.getTo().getWorld()) || Math.abs(event.getTo().getBlockZ()) > getBorderSize(event.getTo().getWorld())) {
                if (event.getPlayer().getVehicle() != null) {
                    event.getPlayer().getVehicle().eject();
                }

                Location newLocation = event.getTo().clone();
                int tries = 0;

                while (Math.abs(newLocation.getX()) > getBorderSize(newLocation.getWorld()) && tries++ < 100) {
                    newLocation.setX(newLocation.getX() - (newLocation.getX() > 0 ? 1 : -1));
                }

                if (tries >= 99) {
                    Foxtrot.getInstance().getLogger().severe(" The server would have crashed while doing border checks! New X: " + newLocation.getX() + ", Old X: " + event.getTo().getBlockX());
                    return;
                }

                tries = 0;

                while (Math.abs(newLocation.getZ()) > getBorderSize(newLocation.getWorld()) && tries++ < 100) {
                    newLocation.setZ(newLocation.getZ() - (newLocation.getZ() > 0 ? 1 : -1));
                }

                if (tries >= 99) {
                    Foxtrot.getInstance().getLogger().severe("The server would have crashed while doing border checks! New Z: " + newLocation.getZ() + ", Old Z: " + event.getTo().getBlockZ());
                    return;
                }

                tries = 0;

                while (newLocation.getBlock().getType() != Material.AIR && tries++ < 100) {
                    newLocation.setY(newLocation.getBlockY() + 1);
                }

                /*
                I don't see how the Y loop could possibly freeze the server, but it happened so hopefully this limiter fixes it

                [09:10:18] [Spigot Watchdog Thread/ERROR]: ------------------------------
                [09:10:18] [Spigot Watchdog Thread/ERROR]: Server thread dump (Look for plugins here before reporting to Spigot!):
                [09:10:18] [Spigot Watchdog Thread/ERROR]: ------------------------------
                [09:10:18] [Spigot Watchdog Thread/ERROR]: Current Thread: Server thread
                [09:10:18] [Spigot Watchdog Thread/ERROR]: 	PID: 25 | Suspended: false | Native: false | State: RUNNABLE
                [09:10:18] [Spigot Watchdog Thread/ERROR]: 	Stack:
                [09:10:18] [Spigot Watchdog Thread/ERROR]: 		net.frozenorb.foxtrot.listener.BorderListener.onPlayerMove(BorderListener.java:116)
                */
                if (tries >= 99) {
                    Foxtrot.getInstance().getLogger().severe(" The server would have crashed while doing border checks! New Y: " + newLocation.getY() + ", Old Y: " + event.getTo().getBlockY());
                    return;
                }

                event.setTo(newLocation);
                event.getPlayer().sendMessage(ChatColor.RED + "You have hit the border!");
            }
        }
    }

    public int getBorderSize(World world) {
        if (world.getEnvironment() == World.Environment.NETHER) {
            return 1500;
        }

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return 2000;
        }

        return 3000;
    }

}
