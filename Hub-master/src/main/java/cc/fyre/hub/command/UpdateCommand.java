package cc.fyre.hub.command;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UpdateCommand {

    @Command(names = {"update"}, permission = "")
    public static void execute(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.translate("&4&lCavePvP 3.0"));
        player.sendMessage(ChatColor.GRAY + "Introducing our biggest update yet!");
        player.sendMessage("");
        player.sendMessage(ChatColor.translate("&4&lLaunch Dates"));
        player.sendMessage(ChatColor.translate("&7Bunkers Season 6 - &fThursday at 5 PM EST"));
        player.sendMessage(ChatColor.translate("&7Kits Season 13 - &fFriday at 5 PM EST"));
        player.sendMessage(ChatColor.translate("&7Fasts SOTW - &fSaturday at 2 PM EST"));
        player.sendMessage("");
        player.sendMessage(ChatColor.translate("&4&lAll Information"));
        player.sendMessage(ChatColor.translate("&7Check out &fhttps://cavepvp.org/3.0"));
        player.sendMessage("");
    }

}
