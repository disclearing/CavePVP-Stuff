package cc.fyre.piston.listener;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.piston.Piston;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.event.HourEvent;
import cc.fyre.universe.Universe;
import cc.fyre.universe.server.fetch.ServerGroup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import static org.bukkit.ChatColor.*;

public class PistonListener implements Listener {

    @EventHandler
    private void onHour(HourEvent event) {
        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(System.currentTimeMillis()));

        if (Universe.getInstance().getGroup() == ServerGroup.BUNKERS) {
            return;
        }

        if (Universe.getInstance().getGroup() == ServerGroup.HUB && event.getHour() == 9 && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            Proton.getInstance().getAutoRebootHandler().rebootServer(TimeUnit.SECONDS.toMillis(30));

            Proton.getInstance().getServer().broadcastMessage("");
            Proton.getInstance().getServer().broadcastMessage(ChatColor.DARK_RED + "[Reboot] " + RED + "The server will be automatically restarting in " + WHITE + "30 seconds" + RED + ".");
            Proton.getInstance().getServer().broadcastMessage("");
            return;
        }

        if (Universe.getInstance().getGroup() != ServerGroup.HUB && event.getHour() == 3 && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            Proton.getInstance().getAutoRebootHandler().rebootServer(TimeUnit.MINUTES.toMillis(5));

            Proton.getInstance().getServer().broadcastMessage("");
            Proton.getInstance().getServer().broadcastMessage(ChatColor.DARK_RED + "[Reboot] " + RED + "The server will be automatically restarting in " + WHITE + "5 minutes" + RED + ".");
            Proton.getInstance().getServer().broadcastMessage("");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onCommand(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();

        if (player.isOp()) {
            return;
        }

        String command = event.getMessage().replace("/worledit:", "").replace("/", "");

        if (command.startsWith("calc") || command.startsWith("eval")) {
            event.setCancelled(true);
            player.sendMessage(RED + "No permission");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignChange(SignChangeEvent event) {

        if (!event.getPlayer().hasPermission(NeutronConstants.ADMIN_PERMISSION)) {
            return;
        }

        final String[] lines = event.getLines();

        for (int i = 0; i < lines.length; i++) {
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', lines[i]));
        }

    }

//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onPlayerInteract(PlayerInteractEvent event) {
//
//        if (event.isCancelled()) {
//            return;
//        }
//
//        if (!event.hasBlock()) {
//            return;
//        }
//
//        if (event.getClickedBlock().getType() != Material.SKULL) {
//            return;
//        }
//
//        final Skull skull = (Skull) event.getClickedBlock().getState();
//
//        try {
//
//            final UUID uuid = UUIDUtils.uuid(skull.getOwner());
//
//            event.getPlayer().sendMessage(ChatColor.GOLD + "This is the head of: " + (uuid == null ? WHITE + skull.getOwner() : Neutron.getInstance().getProfileHandler().fromUuid(uuid, true).getFancyName()));
//        } catch (NullPointerException ignored) {
//        }
//
//    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {

        final Player player = event.getPlayer();
        final PlayerTeleportEvent.TeleportCause cause = event.getCause();

        if (cause.name().contains("PEARL") || cause.name().contains("PORTAL")) {
            return;
        }

        if (player.hasPermission(NeutronConstants.STAFF_PERMISSION)) {
            Piston.getInstance().getBackCache().put(player.getUniqueId(), event.getFrom());
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {

        final Player player = event.getEntity();

        if (player.hasPermission(NeutronConstants.STAFF_PERMISSION)) {
            Piston.getInstance().getBackCache().put(player.getUniqueId(), player.getLocation());
        }

    }

//    @EventHandler(priority = EventPriority.LOW)
//    private void onCommand(PlayerCommandPreprocessEvent event) {
//        if (event.getMessage().contains("{") || event.getMessage().contains("}")) {
//            event.setCancelled(true);
//            event.getPlayer().sendMessage(ChatColor.RED + "Command won't work!");
//        }
//
//    }

//    @EventHandler(priority = EventPriority.LOW)
//    private void onChat(AsyncPlayerChatEvent event) {
//        if (event.getMessage().contains("{") || event.getMessage().contains("}")) {
//            event.setCancelled(true);
//            event.getPlayer().sendMessage(ChatColor.RED + "Command won't work!");
//        }
//
//    }
}
