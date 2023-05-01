package cc.fyre.proton.autoreboot.command;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RebootCancelCommand {

    @Command(
            names = {"reboot cancel"},
            permission = "proton.command.reboot.cancel"
    )
    public static void execute(CommandSender sender) {

        if (!Proton.getInstance().getAutoRebootHandler().isRebooting()) {
            sender.sendMessage(ChatColor.RED + "No reboot has been scheduled.");
            return;
        }

        Proton.getInstance().getAutoRebootHandler().cancelReboot();
        Proton.getInstance().getServer().broadcastMessage(ChatColor.RED + "⚠ " + ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " ⚠");
        Proton.getInstance().getServer().broadcastMessage(ChatColor.RED + "The server reboot has been cancelled.");
        Proton.getInstance().getServer().broadcastMessage(ChatColor.RED + "⚠ " + ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " ⚠");
    }

}
