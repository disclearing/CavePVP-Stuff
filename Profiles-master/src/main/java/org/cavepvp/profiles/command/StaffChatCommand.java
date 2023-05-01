package org.cavepvp.profiles.command;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.universe.Universe;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.packet.StaffBroadcastPacket;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;

import static org.bukkit.ChatColor.*;

public class StaffChatCommand {

    @Command(
            names = {"staffchat", "sc"},
            permission = "command.staffchat"
    )
    public static void execute(Player player, @Parameter(name = "message", wildcard = true, defaultValue = " ") String message) {
        final PlayerProfile profile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());

        if (!message.equalsIgnoreCase(" ")) {
            Profiles.getInstance().getServer().getScheduler().runTaskAsynchronously(Profiles.getInstance(), () ->
                    Proton.getInstance().getPidginHandler().sendPacket(new StaffBroadcastPacket(
                            NeutronConstants.STAFF_PERMISSION,
                            BLUE + "[SC]" + AQUA + "[" + Universe.getInstance().getServerName() + "] " + player.getDisplayName() + GRAY + ": " + ChatColor.WHITE + message)));
            return;
        }

        profile.getPreferences2().setStaffChat(!profile.getPreferences2().isStaffChat());
        profile.save();

        if (player.hasMetadata("STAFF_CHAT")) {
            player.removeMetadata("STAFF_CHAT", Profiles.getInstance());
        } else {
            player.removeMetadata("ADMIN_CHAT", Profiles.getInstance());
            player.removeMetadata("MANAGER_CHAT", Profiles.getInstance());
            player.setMetadata("STAFF_CHAT", new FixedMetadataValue(Profiles.getInstance(), true));
        }

        player.sendMessage(BLUE + "[SC] " + (profile.getPreferences2().isStaffChat() ? GREEN + "Enabled" : RED + "Disabled"));
    }

    @Command(
            names = {"adminchat", "ac"},
            permission = "command.adminchat"
    )
    public static void adminChat(Player player, @Parameter(name = "message", wildcard = true, defaultValue = " ") String message) {
        final PlayerProfile profile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());

        if (!message.equalsIgnoreCase(" ")) {
            Profiles.getInstance().getServer().getScheduler().runTaskAsynchronously(Profiles.getInstance(), () ->
                    Proton.getInstance().getPidginHandler().sendPacket(new StaffBroadcastPacket(
                            NeutronConstants.ADMIN_PERMISSION,
                            DARK_RED + "[AC]" + RED + "[" + Universe.getInstance().getServerName() + "] " + player.getDisplayName() + GRAY + ": " + ChatColor.WHITE + message)
                    ));
            return;
        }

        if (player.hasMetadata("ADMIN_CHAT")) {
            player.removeMetadata("ADMIN_CHAT", Profiles.getInstance());
        } else {
            player.removeMetadata("ADMIN_CHAT", Profiles.getInstance());
            player.removeMetadata("MANAGER_CHAT", Profiles.getInstance());
            player.setMetadata("ADMIN_CHAT", new FixedMetadataValue(Profiles.getInstance(), true));
        }

        profile.getPreferences2().setAdminChat(!profile.getPreferences2().isAdminChat());
        profile.save();

        player.sendMessage(RED + "[AC] " + (profile.getPreferences2().isAdminChat() ? GREEN + "Enabled" : RED + "Disabled"));
    }

    @Command(
            names = {"managerchat", "mc"},
            permission = "command.managerchat"
    )
    public static void managerchat(Player player, @Parameter(name = "message", wildcard = true, defaultValue = " ") String message) {
        final PlayerProfile profile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());

        if (!message.equalsIgnoreCase(" ")) {
            Profiles.getInstance().getServer().getScheduler().runTaskAsynchronously(Profiles.getInstance(), () ->
                    Proton.getInstance().getPidginHandler().sendPacket(new StaffBroadcastPacket(
                            NeutronConstants.MANAGER_PERMISSION,
                            ChatColor.DARK_PURPLE + "[MC]" + LIGHT_PURPLE + "[" + Universe.getInstance().getServerName() + "] " + player.getDisplayName() + GRAY + ": " + ChatColor.WHITE + message)));
            return;
        }

        if (player.hasMetadata("MANAGER_CHAT")) {
            player.removeMetadata("MANAGER_CHAT", Profiles.getInstance());
        } else {
            player.setMetadata("MANAGER_CHAT", new FixedMetadataValue(Profiles.getInstance(), true));
        }

        profile.getPreferences2().setManagerChat(!profile.getPreferences2().isManagerChat());
        profile.save();

        player.sendMessage(ChatColor.DARK_PURPLE + "[MC] " + (profile.getPreferences2().isManagerChat() ? GREEN + "Enabled" : RED + "Disabled"));
    }
}
