package cc.fyre.piston.command.staff;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.piston.Piston;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteChatCommand {

    @Command(
            names = {"mutechat","mc"},
            permission = "command.mutechat"
    )
    public static void execute(CommandSender sender) {

        Piston.getInstance().getChatHandler().setMuted(!Piston.getInstance().getChatHandler().isMuted());

        final String displayName = sender instanceof Player ? ((Player)sender).getDisplayName(): NeutronConstants.CONSOLE_NAME;

        for (Player loopPlayer : Piston.getInstance().getServer().getOnlinePlayers()) {
            if (!loopPlayer.hasPermission(NeutronConstants.STAFF_PERMISSION)) {
                loopPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "Chat has been " + (Piston.getInstance().getChatHandler().isMuted() ? "muted":"unmuted") + ".");
                continue;
            }

            loopPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "Chat has been " + (Piston.getInstance().getChatHandler().isMuted() ? "muted":"unmuted") + " by " + displayName + ".");
        }
    }

    @Command(
            names = {"cavechat", "cavechatonly"},
            permission = "command.cavechat"
    )
    public static void caveChat(CommandSender sender) {

        Piston.getInstance().getChatHandler().setCaveRankOnly(!Piston.getInstance().getChatHandler().isCaveRankOnly());

        final String displayName = sender instanceof Player ? ((Player)sender).getDisplayName(): NeutronConstants.CONSOLE_NAME;

        for (Player loopPlayer : Piston.getInstance().getServer().getOnlinePlayers()) {
            if (!Piston.getInstance().getChatHandler().isCaveRankOnly() && loopPlayer.hasPermission(NeutronConstants.STAFF_PERMISSION)) {
                loopPlayer.sendMessage(ChatColor.translate("&d" + displayName + " &dhas turned off Cave Rank only chat."));
                return;
            }

            loopPlayer.sendMessage("");
            loopPlayer.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Cave Only Chat");
            loopPlayer.sendMessage(ChatColor.translate(displayName + " &chas turned on &4&lCave Rank &conly chat."));
            loopPlayer.sendMessage(ChatColor.translate("&7Players with the &4&lCave Rank &7can now speak in chat."));
            loopPlayer.sendMessage("");
        }
    }
}
