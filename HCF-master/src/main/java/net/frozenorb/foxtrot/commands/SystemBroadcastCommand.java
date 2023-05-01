package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SystemBroadcastCommand {

    @Command(names = {"systembc", "sbc", "systembroadcast"}, permission = "op")
    public static void execute(CommandSender sender, @Parameter(name = "text", wildcard = true)String text) {

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {

            if (!Foxtrot.getInstance().getAnnoyingBroadcastMap().isAnnoyingBroadcast(onlinePlayer.getUniqueId())) {
                continue;
            }

            onlinePlayer.sendMessage(ChatColor.translate(text));
        }

    }

}
