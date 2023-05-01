package cc.fyre.piston.command.admin;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.piston.Piston;
import cc.fyre.piston.client.data.Client;
import cc.fyre.piston.client.data.Version;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public class ClientCommands {
    @Command(
            names = {"version"},
            permission = "command.version"
    )
    public static void executeVersion(Player player, @Parameter(name = "self") Player target) {
        Version version = Piston.getInstance().getClientHandler().getPlayerVerision(target);
        Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(target.getUniqueId());
        player.sendMessage(ChatColor.GOLD + profile.getFancyName() + ChatColor.GOLD + " is on " + ChatColor.WHITE + version.getVersion());
    }
    @Command(
            names = {"client"},
            permission = "command.client"
    )
    public static void executeClient(Player player, @Parameter(name = "self") Player target) {
        Client client = Piston.getInstance().getClientHandler().getPlayerClient(target);
        Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(target.getUniqueId());
        player.sendMessage(ChatColor.GOLD + profile.getFancyName() + ChatColor.GOLD + " is on " + ChatColor.WHITE + client.getDisplayName());
    }
}
