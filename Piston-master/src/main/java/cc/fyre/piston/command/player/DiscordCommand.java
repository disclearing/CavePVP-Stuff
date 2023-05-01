package cc.fyre.piston.command.player;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DiscordCommand {
    @Command(names = {"discord"}, permission = "")
    public static void execute(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Discord");
        player.sendMessage(ChatColor.translate("&7Join our Discord at &fhttps://cavepvp.org/discord&7!"));
        player.sendMessage("");
    }

    @Command(names = {"telegram"}, permission = "")
    public static void telegram(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Telegram");
        player.sendMessage(ChatColor.translate("&7Join our Telegram at &fhttps://t.me/CavePvPorg&7!"));
        player.sendMessage("");
    }

    @Command(names = {"twitter"}, permission = "")
    public static void twitter(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Telegram");
        player.sendMessage(ChatColor.translate("&7Follow us on Twitter at &fhttps://twitter.com/CavePvPorg&7!"));
        player.sendMessage("");
    }

    @Command(names = {"giveaway"}, permission = "")
    public static void giveaway(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Giveaway");
        player.sendMessage(ChatColor.translate("&7We are currently hosting a big giveaway on Twitter!"));
        player.sendMessage(ChatColor.translate("&cLink - &fhttps://cavepvp.org/giveaway"));
        player.sendMessage("");
    }
}
