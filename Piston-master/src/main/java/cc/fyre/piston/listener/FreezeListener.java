package cc.fyre.piston.listener;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.piston.Piston;
import cc.fyre.piston.PistonConstants;
import cc.fyre.piston.packet.FrozenLogoutPacket;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.proton.Proton;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class FreezeListener implements Listener {

    private static final String FROZEN_MESSAGE = ChatColor.RED + "You cannot do this while frozen.";

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {

        final Player player = event.getPlayer();

        if (!player.hasMetadata(PistonConstants.FREEZE_METADATA)) {
            return;
        }

        player.removeMetadata(PistonConstants.FREEZE_METADATA, Piston.getInstance());
        player.sendMessage(FROZEN_MESSAGE);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {

        final Player player = event.getPlayer();

        if (!player.hasMetadata(PistonConstants.FREEZE_METADATA)) {
            return;
        }

        String message = event.getMessage();

        // names = {"message", "msg", "tell", "whisper", "t", "w", "m"},
        // names = {"reply", "r"},

        if (message.startsWith("/message")
                || message.startsWith("/msg")
                || message.startsWith("/tell")
                || message.startsWith("/whisper")
                || message.startsWith("/t")
                || message.startsWith("/w")
                || message.startsWith("/m")
                || message.startsWith("/reply")
                || message.startsWith("/r")
        ) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {

        final Player player = event.getPlayer();

        if (!player.hasMetadata(PistonConstants.FREEZE_METADATA)) {
            return;
        }

        Piston.getInstance().sendPacketAsync(new FrozenLogoutPacket(player.getName()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {

        final Player player = event.getPlayer();

        if (!player.hasMetadata(PistonConstants.FREEZE_METADATA)) {
            return;
        }

        final Location to = event.getTo();
        final Location from = event.getFrom();

        if (from.getX() != to.getX() || event.getFrom().getZ() != event.getTo().getZ()) {

            final Location newLocation = from.getBlock().getLocation().add(0.5, 0.0, 0.5);

            newLocation.setPitch(to.getPitch());
            newLocation.setYaw(to.getYaw());

            event.setTo(newLocation);
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        if (!player.hasMetadata(PistonConstants.FREEZE_METADATA)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (event.getDamager().hasMetadata(PistonConstants.FREEZE_METADATA)) {
            event.setCancelled(true);
            ((Player) event.getDamager()).sendMessage(FROZEN_MESSAGE);
        }

        if (event.getEntity() instanceof Player && event.getEntity().hasMetadata(PistonConstants.FREEZE_METADATA)) {
            ((Player) event.getDamager()).sendMessage(((Player) event.getEntity()).getDisplayName() + ChatColor.RED + " is currently frozen and cannot be damaged.");
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {

        final Player player = (Player) event.getWhoClicked();

        if (!player.hasMetadata(PistonConstants.FREEZE_METADATA)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(FROZEN_MESSAGE);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        final Player player = event.getPlayer();

        if (!player.hasMetadata(PistonConstants.FREEZE_METADATA)) {
            return;
        }

        event.setCancelled(true);

        player.updateInventory();
        player.sendMessage(FROZEN_MESSAGE);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {

        final Player player = event.getPlayer();

        if (!player.hasMetadata(PistonConstants.FREEZE_METADATA)) {
            return;
        }

        event.setCancelled(true);

        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {

        final Player player = event.getPlayer();

        if (!player.hasMetadata(PistonConstants.FREEZE_METADATA)) {
            return;
        }

        event.setCancelled(true);

        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {

        final Player player = event.getPlayer();

        if (!player.hasMetadata(PistonConstants.FREEZE_METADATA)) {
            return;
        }

        event.setCancelled(true);

        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();

        if (!player.hasMetadata(PistonConstants.FREEZE_METADATA)) {
            return;
        }

        event.setCancelled(true);

        String message = ChatColor.translateAlternateColorCodes('&', "&b&l[Frozen] &f" + player.getDisplayName() + "&7: &f" + ChatColor.WHITE + event.getMessage());

        player.sendMessage(message);
        Proton.getInstance().getPidginHandler().sendPacket(new StaffBroadcastPacket(NeutronConstants.STAFF_PERMISSION, message));
    }
}
