package org.cavepvp.profiles.listener;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.piston.Piston;
import cc.fyre.proton.Proton;
import cc.fyre.universe.Universe;
import cc.fyre.universe.UniverseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.packet.StaffBroadcastPacket;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;

import static org.bukkit.ChatColor.*;

public class SwitchListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (!player.hasPermission(NeutronConstants.STAFF_PERMISSION)) {
            return;
        }

       Piston.getInstance().getServer().getScheduler().runTaskLater(Piston.getInstance(), () -> Proton.getInstance().getPidginHandler().sendPacket(new StaffBroadcastPacket(NeutronConstants.STAFF_PERMISSION,
                ChatColor.translate("&9[Staff] " + player.getDisplayName() + " &7has joined &f" + UniverseAPI.getServerName() + "&7."))
        ), 20L);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (!player.hasPermission(NeutronConstants.STAFF_PERMISSION)) {
            return;
        }

        Proton.getInstance().getPidginHandler().sendPacket(new StaffBroadcastPacket(NeutronConstants.STAFF_PERMISSION,
                ChatColor.translate("&9[Staff] " + player.getDisplayName() + " &7has left &f" + UniverseAPI.getServerName() + "&7."))
        );
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final PlayerProfile profile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());

        boolean staff = player.hasPermission(NeutronConstants.STAFF_PERMISSION) && profile.getPreferences2().isStaffChat();
        boolean admin = player.hasPermission(NeutronConstants.ADMIN_PERMISSION) && profile.getPreferences2().isAdminChat();
        boolean manager = player.hasPermission(NeutronConstants.MANAGER_PERMISSION) && profile.getPreferences2().isManagerChat();

        if (!staff && !admin && !manager) {
            return;
        }

        event.setCancelled(true);

        Bukkit.getScheduler().runTaskAsynchronously(Piston.getInstance(), () -> {
            if (manager) {
                Proton.getInstance().getPidginHandler().sendPacket(new StaffBroadcastPacket(
                        NeutronConstants.MANAGER_PERMISSION,
                        DARK_PURPLE + "[MC]" + LIGHT_PURPLE + "[" + Universe.getInstance().getServerName() + "] " + player.getDisplayName() + GRAY + ": " + WHITE + event.getMessage()
                ));
                return;
            }

            if (admin) {
                Proton.getInstance().getPidginHandler().sendPacket(new StaffBroadcastPacket(
                        NeutronConstants.ADMIN_PERMISSION,
                        DARK_RED + "[AC]" + RED + "[" + Universe.getInstance().getServerName() + "] " + player.getDisplayName() + GRAY + ": " + WHITE + event.getMessage()
                ));
                return;
            }

            Proton.getInstance().getPidginHandler().sendPacket(new StaffBroadcastPacket(
                    NeutronConstants.STAFF_PERMISSION,
                    BLUE + "[SC]" + AQUA + "[" + Universe.getInstance().getServerName() + "] " + player.getDisplayName() + GRAY + ": " + WHITE + event.getMessage()
            ));
        });
    }

}
