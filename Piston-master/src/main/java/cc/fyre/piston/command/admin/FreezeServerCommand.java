package cc.fyre.piston.command.admin;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.piston.Piston;

import cc.fyre.proton.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class FreezeServerCommand {

    @Command(names = { "freezeserver" }, permission = "command.freezeserver", description = "Freeze the server. Normal players won't be able to move or interact")
    public static void execute(final CommandSender sender) {
        Piston.getInstance().getServerHandler().setFrozen(!Piston.getInstance().getServerHandler().isFrozen());

        Piston.getInstance().getServer().getOnlinePlayers().forEach(loopPlayer -> {

            if (!loopPlayer.hasPermission(NeutronConstants.STAFF_PERMISSION)) {
                loopPlayer.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "The server has been " + (Piston.getInstance().getServerHandler().isFrozen() ? "" : "un") + "frozen.");
            } else {
                loopPlayer.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "The server has been " + (Piston.getInstance().getServerHandler().isFrozen() ? "" : "un") + "frozen by " + sender.getName() + ".");
            }

        });

    }

}
