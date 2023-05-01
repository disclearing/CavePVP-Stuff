package cc.fyre.neutron.command.profile.permission;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;

import cc.fyre.neutron.profile.packet.PermissionRemovePacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PermissionRemoveCommand {

    @Command(
            names = {"profile permission remove","permission remove"},
            permission = "neutron.command.permission.remove",
            async = true
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") UUID uuid,@Parameter(name = "permission")String permission) {

        final Player player = Neutron.getInstance().getServer().getPlayer(uuid);

        Profile profile;

        if (player != null) {
            profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        } else {
            profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);
        }

        if (!profile.getPermissions().contains(permission)) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " does not have the permission " + ChatColor.WHITE + permission + ChatColor.RED + ".");
            return;
        }

        profile.getPermissions().remove(permission);
        profile.save();

        if (player == null) {
            Proton.getInstance().getPidginHandler().sendPacket(new PermissionRemovePacket(uuid,permission));
        }

        sender.sendMessage(ChatColor.GOLD + "Removed permission "+ ChatColor.WHITE + permission + ChatColor.GOLD + " for " + profile.getFancyName() + ChatColor.GOLD + ".");
    }

}
