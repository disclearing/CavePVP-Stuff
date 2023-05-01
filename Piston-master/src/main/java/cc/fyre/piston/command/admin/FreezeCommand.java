package cc.fyre.piston.command.admin;

import cc.fyre.neutron.Neutron;
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

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BLUE;

public class FreezeCommand {

    @Command(
            names = {"freeze"},
            permission = "command.freeze"
    )
    public static void execute(CommandSender sender, @Parameter(name = "player") Player player) {

        if (!sender.isOp() && player.isOp()) {
            sender.sendMessage(ChatColor.RED + "You may not freeze that player!");
            return;
        }

        if (player.getName().equalsIgnoreCase("SimplyTrash") && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "You cannot kick SimplyTrash");
            return;
        }

        Piston.getInstance().getServerHandler().freeze(player);
        sender.sendMessage(player.getDisplayName() + ChatColor.GOLD + " has been frozen.");

        String displayName = NeutronConstants.CONSOLE_NAME;
        final String targetDisplayName = player.getDisplayName();

        if (sender instanceof Player) {
            displayName = ((Player) sender).getDisplayName();
        }

        Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                NeutronConstants.STAFF_PERMISSION,
                BLUE + "[SC]" + AQUA + "[" + UniverseAPI.getServerName() + "] " + ChatColor.translate(displayName
                        + " &7has frozen &f" + targetDisplayName + "&7.")
        ));
    }

    @Command(
            names = {"unfreeze"},
            permission = "command.freeze")
    public static void unfreeze(CommandSender sender, @Parameter(name = "player") Player player) {

        if (!sender.isOp() && player.isOp()) {
            sender.sendMessage(ChatColor.RED + "You may not freeze that player!");
            return;
        }

        Piston.getInstance().getServerHandler().unfreeze(player.getUniqueId());
        sender.sendMessage(player.getDisplayName() + ChatColor.GOLD + " has been unfrozen.");

        String displayName = NeutronConstants.CONSOLE_NAME;
        final String targetDisplayName = Neutron.getInstance().getProfileHandler().findDisplayName(player.getUniqueId());

        if (sender instanceof Player) {
            displayName = Neutron.getInstance().getProfileHandler().findDisplayName(((Player) sender).getUniqueId());
        }

        Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                NeutronConstants.STAFF_PERMISSION,
                BLUE + "[SC]" + AQUA + "[" + UniverseAPI.getServerName() + "] " + ChatColor.translate(displayName
                        + " &7has unfrozen &f" + targetDisplayName + "&7.")
        ));
    }
}
