package cc.fyre.neutron.command.profile.permission;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PermissionListCommand {

    @Command(
            names = {"profile permission list","permission list"},
            permission = "neutron.command.permission.list", async = true
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") UUID uuid,@Flag(value = {"e","effective"})boolean effective) {

        final Player player = Neutron.getInstance().getServer().getPlayer(uuid);

        Profile profile;

        if (player != null) {
            profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        } else {
            profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);
        }

        sender.sendMessage(profile.getFancyName() + ChatColor.GOLD + "'s permissions:");

        for (String permission : effective ? profile.getEffectivePermissions():profile.getPermissions()) {
            sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.WHITE + permission + " " + (effective ? (profile.getEffectivePermissions().contains(permission) ? ChatColor.GRAY + "(Effective)":""):""));
        }
    }

}
