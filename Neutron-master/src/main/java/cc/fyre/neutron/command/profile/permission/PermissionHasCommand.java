package cc.fyre.neutron.command.profile.permission;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.packet.PermissionRemovePacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PermissionHasCommand {
    @Command(
            names = {"profile permission has","permission has"},
            permission = "neutron.command.permission.has", async = true
    )
    public static void execute(CommandSender sender, @Parameter(name = "player") UUID uuid, @Parameter(name = "permission")String permission, @Flag(value = {"e","effective"})boolean effective) {

        final Player player = Neutron.getInstance().getServer().getPlayer(uuid);

        Profile profile;

        if (player != null) {
            profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        } else {
            profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);
        }

        if(effective ? profile.getEffectivePermissions().contains(permission):profile.getPermissions().contains(permission)) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " does not have the permission " + ChatColor.WHITE + permission + ChatColor.RED + ".");
        } else {
            sender.sendMessage(profile.getFancyName() + ChatColor.GREEN + " does has the permission " + ChatColor.WHITE + permission +
                    ChatColor.GREEN + (effective ? (profile.getEffectivePermissions().contains(permission) ? ChatColor.GRAY + "(Effective)":""):"") + ".");
        }

    }
}
