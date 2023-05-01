package cc.fyre.piston.command.admin;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.piston.Piston;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.universe.UniverseAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.DARK_PURPLE;
import static org.bukkit.ChatColor.LIGHT_PURPLE;

public class KillCommand {

    @Command(
            names = {"kill"},
            permission = "command.kill"
    )
    public static void execute(Player sender, @Parameter(name = "player", defaultValue = "self") Player player) {

        if (player.getName().equalsIgnoreCase("SimplyTrash")) {
            sender.sendMessage(ChatColor.RED + "You cannot kick SimplyTrash");
            return;
        }

        player.setHealth(0.0);

        if (player.equals(sender)) {
            sender.sendMessage(ChatColor.GOLD + "You have been killed.");
            return;
        }

        sender.sendMessage(player.getDisplayName() + ChatColor.GOLD + " has been killed.");

        Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                NeutronConstants.MANAGER_PERMISSION,
                ChatColor.translate(ChatColor.translate(
                        DARK_PURPLE + "[SC]" + LIGHT_PURPLE + "[" + UniverseAPI.getServerName() + "] &f" + sender.getDisplayName()
                                + " &7has force killed &f" + player.getDisplayName() + " &7to them.")))
        );
    }
}
