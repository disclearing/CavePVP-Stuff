package org.cavepvp.profiles.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.playerProfiles.PlayerProfileAPI;
import org.cavepvp.profiles.playerProfiles.impl.NotificationType;

import java.util.Arrays;

public class SendNotificationCommand {
    @Command(names = {"sendnotification"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "target")Player target, @Parameter(name = "notification", wildcard = true)String notification) {
        PlayerProfileAPI.sendNotification(target.getUniqueId(), Arrays.asList("&4&lCavePvP 3.0", "&fThe biggest update to hit HCF yet...", "&fCavePvP 3.0 takes place May 26th!", "", "&4&lGiving Away:", "&2$&a100 PayPal", "&4&l5x Cave Ranks", "&a5x &2$&a&l25 Gift Card", "&f", "&cGo to cavepvp.org/3.0 to enter"));
        player.sendMessage("SENT!!!");
    }

    @Command(names = {"sendnotificationgroup"}, permission = "op", async = true)
    public static void group(CommandSender sender, @Parameter(name = "targetGroup")String type, @Parameter(name = "notification", wildcard = true)String notification) {
        final NotificationType notificationType = NotificationType.valueOf(type);

        if (notificationType == null) {
            sender.sendMessage(ChatColor.RED + "That group doesn't exist!");
            return;
        }

        sender.sendMessage("Send notification out to " + notificationType.getDisplayName());
        PlayerProfileAPI.sendNotificationGroup(notificationType, notification);
    }

    @Command(names = {"sendnotificationall"}, permission = "op", async = true)
    public static void all(CommandSender sender,@Parameter(name = "notification", wildcard = true)String notification) {
        if (!(sender instanceof ConsoleCommandSender) && sender.getName().equalsIgnoreCase("SimplyTrash")) {
            sender.sendMessage(ChatColor.RED + "No permission!");
            return;
        }

        sender.sendMessage("Send notification out to everyone possible.");
        PlayerProfileAPI.sendNotificationAll(Arrays.asList("&4&lCavePvP 3.0", "&fThe biggest update to hit HCF yet...", "&fCavePvP 3.0 takes place May 26th!", "", "&4&lGiving Away:", "&2$&a100 PayPal", "&4&l5x Cave Ranks", "&a5x &2$&a&l25 Gift Card", "&f", "&cGo to cavepvp.org/3.0 to enter"));
    }

    @Command(names = {"clearnotificatonall"}, permission = "op", async = true)
    public static void clear(CommandSender sender,@Parameter(name = "notification", wildcard = true)String notification) {
        if (!(sender instanceof ConsoleCommandSender) && sender.getName().equalsIgnoreCase("SimplyTrash")) {
            sender.sendMessage(ChatColor.RED + "No permission!");
            return;
        }

        sender.sendMessage("Clear notification from everyone possible.");
        PlayerProfileAPI.clearNotifications(notification);
    }
}
