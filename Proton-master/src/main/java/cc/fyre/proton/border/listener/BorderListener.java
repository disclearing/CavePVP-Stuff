package cc.fyre.proton.border.listener;

import cc.fyre.proton.Proton;
import cc.fyre.proton.border.event.player.PlayerBorderEvent;
import cc.fyre.proton.border.event.player.PlayerExitBorderEvent;
import cc.fyre.proton.border.Border;
import cc.fyre.proton.border.action.DefaultBorderActions;
import cc.fyre.proton.border.event.player.PlayerEnterBorderEvent;
import cc.fyre.proton.cuboid.Cuboid;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

public class BorderListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        final Location fromLoc = event.getFrom();
        final Location toLoc = event.getTo();
        final Border border = Proton.getInstance().getBorderHandler().getBorderForWorld(fromLoc.getWorld());

        if (border == null) {
            return;
        }

        final boolean from = border.contains(fromLoc.getBlockX(), fromLoc.getBlockZ());
        final boolean to = border.contains(toLoc.getBlockX(), toLoc.getBlockZ());
        final boolean movedBlock = event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ();

        if (!movedBlock) {
            return;
        }

        PlayerBorderEvent playerBorderEvent = null;

        if (from && !to) {
            playerBorderEvent = new PlayerExitBorderEvent(border, event.getPlayer(), fromLoc, toLoc);
        } else if (!from && to) {
            playerBorderEvent = new PlayerEnterBorderEvent(border, event.getPlayer(), fromLoc, toLoc);
        }

        if (playerBorderEvent != null) {

            Proton.getInstance().getServer().getPluginManager().callEvent(playerBorderEvent);

            if (playerBorderEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onVehicleUpdate(VehicleUpdateEvent event) {

        if (!(event.getVehicle().getPassenger() instanceof Player)) {
            return;
        }

        final Border border = Proton.getInstance().getBorderHandler().getBorderForWorld(event.getVehicle().getWorld());

        if (border == null) {
            return;
        }

        final Player player = (Player)event.getVehicle().getPassenger();
        final Vehicle vehicle = event.getVehicle();

        if (border.contains(vehicle.getLocation()) && !(vehicle instanceof Horse)) {
            return;
        }

        final Location location = vehicle.getLocation();
        final Cuboid cuboid = border.getPhysicalBounds();

        double validX = location.getX();
        double validZ = location.getZ();

        if (location.getBlockX() + 2 > cuboid.getUpperX()) {
            validX = (double)(cuboid.getUpperX() - 3);
        } else if (location.getBlockX() - 2 < cuboid.getLowerX()) {
            validX = (double)(cuboid.getLowerX() + 4);
        }

        if (location.getBlockZ() + 2 > cuboid.getUpperZ()) {
            validZ = (double)(cuboid.getUpperZ() - 3);
        } else if (location.getBlockZ() - 2 < cuboid.getLowerZ()) {
            validZ = (double)(cuboid.getLowerZ() + 4);
        }

        final Location validLoc = new Location(location.getWorld(), validX, location.getY(), validZ);
        final Vector velocity = validLoc.toVector().subtract(location.toVector()).multiply(2);

        vehicle.setVelocity(velocity);

        if (!DefaultBorderActions.getLastMessaged().containsKey(player.getUniqueId()) || System.currentTimeMillis() - DefaultBorderActions.getLastMessaged().get(player.getUniqueId()) > TimeUnit.SECONDS.toMillis(1L)) {
            player.sendMessage(ChatColor.RED + "You have reached the border!");
            DefaultBorderActions.getLastMessaged().put(player.getUniqueId(), System.currentTimeMillis());
        }

    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {

        final Border border = Proton.getInstance().getBorderHandler().getBorderForWorld(event.getTo().getWorld());

        if (border == null) {
            return;
        }

        final Location location = event.useTravelAgent() ? event.getPortalTravelAgent().findOrCreate(event.getTo()) : event.getTo();
        final Cuboid cuboid = border.getPhysicalBounds();

        double validX = location.getX();
        double validZ = location.getZ();

        final int buffer = 30;

        if (location.getBlockX() + 2 > cuboid.getUpperX()) {
            validX = (double)(cuboid.getUpperX() - buffer);
        } else if (location.getBlockX() - 2 < cuboid.getLowerX()) {
            validX = (double)(cuboid.getLowerX() + buffer + 1);
        }

        if (location.getBlockZ() + 2 > cuboid.getUpperZ()) {
            validZ = (double)(cuboid.getUpperZ() - buffer);
        } else if (location.getBlockZ() - 2 < cuboid.getLowerZ()) {
            validZ = (double)(cuboid.getLowerZ() + buffer + 1);
        }

        final Location validLoc = new Location(location.getWorld(), validX, location.getY(), validZ);

        event.setTo(validLoc);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {

        final Location toLoc = event.getTo();
        final Border border = Proton.getInstance().getBorderHandler().getBorderForWorld(toLoc.getWorld());

        if (border == null) {
            return;
        }

        final boolean to = border.contains(toLoc.getBlockX(), toLoc.getBlockZ());

        if (!to && event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);
            event.setTo(event.getFrom());
        }

    }

}
