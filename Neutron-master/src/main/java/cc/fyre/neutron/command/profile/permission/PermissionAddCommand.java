package cc.fyre.neutron.command.profile.permission;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;

import cc.fyre.neutron.profile.packet.PermissionAddPacket;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PermissionAddCommand {

    @Command(
            names = {"profile permission add","permission add","addindividualperm"},
            permission = "neutron.command.permission.add",
            async = true
    )
    public static void execute(CommandSender sender,@Parameter(name = "player")UUID uuid,@Parameter(name = "permission")String permission) {

        final Player player = Neutron.getInstance().getServer().getPlayer(uuid);

        Profile profile;

        if (player != null) {
            profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        } else {
            profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);
        }

        String lowerCase = permission.toLowerCase();

        if (profile.getPermissions().contains(lowerCase)) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " already has the permission " + ChatColor.WHITE + lowerCase + ChatColor.RED + ".");
            return;
        }

        if ((lowerCase.equalsIgnoreCase("*") || lowerCase.contains("*") || lowerCase.contains("bukkit") || lowerCase.contains("hc") || lowerCase.contains("foxtrot") || lowerCase.contains("neutron")) && sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "You may not add that permission unless you're in CONSOLE!");
            return;
        }

        profile.getPermissions().add(lowerCase);
        profile.save();

        if (player == null) {
            Proton.getInstance().getPidginHandler().sendPacket(new PermissionAddPacket(uuid,lowerCase));
        }

        sender.sendMessage(ChatColor.GOLD + "Granted "+  profile.getFancyName() + ChatColor.GOLD + " permission " + ChatColor.WHITE + lowerCase + ChatColor.GOLD + ".");
    }
}
