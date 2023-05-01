package cc.fyre.piston.command.staff;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.piston.Piston;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand {

    @Command(
            names = {"clearchat", "cc"},
            permission = "command.clearchat"
    )
    public static void execute(CommandSender sender) {

        final String displayName = sender instanceof Player ? ((Player)sender).getDisplayName(): NeutronConstants.CONSOLE_NAME;

        for (Player loopPlayer : Piston.getInstance().getServer().getOnlinePlayers()) {

            if (loopPlayer.hasPermission(NeutronConstants.STAFF_PERMISSION)) {
                loopPlayer.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "C" + ChatColor.GRAY + "] " + displayName + ChatColor.GRAY + " has " + ChatColor.LIGHT_PURPLE + "cleared" + ChatColor.GRAY + " the chat.");
                continue;
            }

            for (int i = 0; i < 75; i++) {
                loopPlayer.sendMessage("");
            }

        }
    }
}
