package cc.fyre.piston.command.staff;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.security.AlertType;
import cc.fyre.piston.Piston;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.command.param.defaults.offlineplayer.OfflinePlayerWrapper;
import cc.fyre.proton.util.UUIDUtils;
import cc.fyre.universe.UniverseAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

public class TeleportCommand {

    @Command(names = {"teleport", "tp", "tpto", "goto"}, permission = "command.teleport", description = "Teleport yourself to a player")
    public static void execute(Player player, @Parameter(name = "player") OfflinePlayerWrapper offlinePlayerWrapper) {
        final Player target = offlinePlayerWrapper.loadSync();

        if (target == null) {
            player.sendMessage(ChatColor.RED + "No online or offline player with the name " + offlinePlayerWrapper.getName() + " found.");
            return;
        }

        Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                NeutronConstants.MANAGER_PERMISSION,
                ChatColor.translate(ChatColor.translate(
                        DARK_PURPLE + "[MC]" + LIGHT_PURPLE + "[" + UniverseAPI.getServerName() + "] &f" + player.getDisplayName()
                                + " &7has teleported to &f" + target.getDisplayName() + "&7."))));

        final Location previous = player.getLocation();
        final Location newLocation = target.getLocation();

        Neutron.getInstance().getSecurityHandler().addSecurityAlert(player.getUniqueId(), target.getUniqueId(), AlertType.TELEPORT_HERE, false,
                "Previous Location: " + previous.getWorld().getName() + ", " + previous.getBlockX() + ", " + previous.getBlockY() + ", " + previous.getBlockZ(), "New Location: " + newLocation.getWorld().getName() + ", " + newLocation.getBlockX() + ", " + newLocation.getBlockY() + ", " + newLocation.getBlockZ());

        player.teleport(target);
        player.sendMessage(ChatColor.GOLD + "Teleporting you to " + (player.isOnline() ? "" : "offline player ") + ChatColor.WHITE + target.getDisplayName() + ChatColor.GOLD + ".");
    }

    @Command(names = {"tphere", "bring", "s"}, permission = "command.teleport.here", description = "Teleport a player to you")
    public static void execute(Player player, @Parameter(name = "player") Player target, @Flag(value = {"s", "silent"}, description = "Silently teleport the player (staff members always get messaged)") boolean silent) {
        final Location staffLocation = player.getLocation();
        final Location victimLocation = target.getLocation();

        Neutron.getInstance().getSecurityHandler().addSecurityAlert(player.getUniqueId(), target.getUniqueId(), AlertType.TELEPORT_HERE, false,
                "Staff (New) Location: " + staffLocation.getWorld().getName() + ", " + staffLocation.getBlockX() + ", " + staffLocation.getBlockY() + ", " + staffLocation.getBlockZ(), "Victim (Old) Location: " + victimLocation.getWorld().getName() + ", " + victimLocation.getBlockX() + ", " + victimLocation.getBlockY() + ", " + victimLocation.getBlockZ());

        target.teleport(player);

        player.sendMessage(ChatColor.GOLD + "Teleporting " + ChatColor.WHITE + target.getDisplayName() + ChatColor.GOLD + " to you.");

        if (!silent || target.hasPermission(NeutronConstants.STAFF_PERMISSION)) {
            target.sendMessage(ChatColor.GOLD + "Teleporting you to " + ChatColor.WHITE + player.getDisplayName() + ChatColor.GOLD + ".");
        }

        Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                NeutronConstants.MANAGER_PERMISSION,
                ChatColor.translate(ChatColor.translate(
                        DARK_PURPLE + "[SC]" + LIGHT_PURPLE + "[" + UniverseAPI.getServerName() + "] &f" + player.getDisplayName()
                                + " &7has teleported &f" + target.getDisplayName() + " &7to them.")))
        );
    }

    @Command(names = {"back"}, permission = "command.back", description = "Teleport to your last location")
    public static void execute(Player player, @Parameter(name = "player", defaultValue = "self") Player target) {

        if (!Piston.getInstance().getBackCache().containsKey(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "No previous location recorded.");
            return;
        }

        target.teleport(Piston.getInstance().getBackCache().get(target.getUniqueId()));
        target.sendMessage(ChatColor.GOLD + "Teleporting " + (target.getUniqueId().equals(player.getUniqueId()) ? "" : target.getDisplayName()) + "to " + (target.getUniqueId().equals(player.getUniqueId()) ? "your" : "their") + " last recorded location.");
    }

    @Command(names = {"tppos"}, permission = "command.tppos", description = "Teleport to coordinates")
    public static void execute(Player player, @Parameter(name = "x") double x, @Parameter(name = "y") double y, @Parameter(name = "z") double z, @Parameter(name = "player", defaultValue = "self") Player target) {

        if (!player.equals(target) && !player.hasPermission("piston.command.teleport.other")) {
            player.sendMessage(ChatColor.RED + "No permission to teleport other players.");
            return;
        }

        if (isBlock(x)) {
            x += ((z >= 0.0) ? 0.5 : -0.5);
        }

        if (isBlock(z)) {
            z += ((x >= 0.0) ? 0.5 : -0.5);
        }

        target.teleport(new Location(target.getWorld(), x, y, z));

        final String location = ChatColor.translateAlternateColorCodes('&', String.format("&e[&f%s&e, &f%s&e, &f%s&e]&6", x, y, z));

        if (!player.equals(target)) {
            player.sendMessage(ChatColor.GOLD + "Teleporting " + ChatColor.WHITE + target.getDisplayName() + ChatColor.GOLD + " to " + location + ".");
        }

        target.sendMessage(ChatColor.GOLD + "Teleporting you to " + location + ".");

        Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                NeutronConstants.MANAGER_PERMISSION,
                ChatColor.translate(ChatColor.translate(
                        LIGHT_PURPLE + "[MC]" + LIGHT_PURPLE + "[" + UniverseAPI.getServerName() + "] &f" + player.getDisplayName() + " &7has teleported to &f" + x + ", " + y + ", " + z))));
    }

    private static boolean isBlock(double value) {
        return value % 1.0 == 0.0;
    }

}
