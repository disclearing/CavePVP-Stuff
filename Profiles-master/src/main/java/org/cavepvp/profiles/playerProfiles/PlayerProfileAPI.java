package org.cavepvp.profiles.playerProfiles;

import cc.fyre.proton.Proton;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.packet.type.FriendRequestAcceptPacket;
import org.cavepvp.profiles.packet.type.FriendRequestSendPacket;
import org.cavepvp.profiles.packet.type.NotificationSendPacket;
import org.cavepvp.profiles.playerProfiles.impl.Notification;
import org.cavepvp.profiles.playerProfiles.impl.NotificationType;
import org.cavepvp.profiles.playerProfiles.impl.stats.StatisticServer;
import org.cavepvp.profiles.playerProfiles.impl.stats.StatisticType;
import org.cavepvp.profiles.playerProfiles.impl.stats.Statistics;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerProfileAPI {

    public static boolean sendNotification(UUID uuid, String... message) {
        return sendNotification(uuid, Arrays.asList(message));
    }

    public static boolean sendNotificationGroup(NotificationType notificationType, String... message) {
        int amount = 0;

        for (Document document : Profiles.getInstance().getPlayerProfileHandler().getCollection().find()) {

            if (!document.containsKey("enabledNotificatons")) {
                continue;
            }

            final List<String> notifications = document.getList("enabledNotificatons", String.class);

            if (notifications == null) {
                continue;
            }

            if (notifications.contains(notificationType.name())) {
                final UUID uuid = UUID.fromString(document.getString("_id"));

                sendNotification(uuid, message);
                amount++;
            }
        }

        System.out.println("");
        System.out.println("Dispatched notification out to " + amount + " people.");
        System.out.println("");

        return true;
    }
    public static boolean sendNotificationAll(List<String> message) {
        int amount = 0;

        for (Document document : Profiles.getInstance().getPlayerProfileHandler().getCollection().find()) {
            final UUID uuid = UUID.fromString(document.getString("_id"));

            final Optional<PlayerProfile> optionalPlayerProfile = Profiles.getInstance().getPlayerProfileHandler().requestProfile(uuid);

            if (!optionalPlayerProfile.isPresent()) {
                continue;
            }

            if (optionalPlayerProfile.get().getNotifications().size() > 50) {
                optionalPlayerProfile.get().getNotifications().remove(optionalPlayerProfile.get().getNotifications().size()-1);
            }

            Proton.getInstance().getPidginHandler().sendPacket(new NotificationSendPacket(uuid));

            optionalPlayerProfile.get().getNotifications().add(new Notification(uuid, java.lang.System.currentTimeMillis(), message));
            optionalPlayerProfile.get().save();

            amount++;

            if (amount % 100 == 0) {
                System.out.println("Dispatched notifications to " + amount + " players...");
            }
        }

        System.out.println("");
        System.out.println("Dispatched notification out to " + amount + " people.");
        System.out.println("");
        return true;
    }

    public static boolean clearNotifications(String... message) {
        int amount = 0;

        for (Document document : Profiles.getInstance().getPlayerProfileHandler().getCollection().find()) {
            final UUID uuid = UUID.fromString(document.getString("_id"));

            final Optional<PlayerProfile> optionalPlayerProfile = Profiles.getInstance().getPlayerProfileHandler().requestProfile(uuid);

            if (!optionalPlayerProfile.isPresent()) {
                continue;
            }

            optionalPlayerProfile.get().getNotifications().clear();
            optionalPlayerProfile.get().save();

            amount++;

            if (amount % 100 == 0) {
                System.out.println("Cleared notifications to " + amount + " players...");
            }
        }

        System.out.println("");
        System.out.println("Cleared notification from " + amount + " people.");
        System.out.println("");
        return true;
    }

    public static boolean sendNotification(UUID uuid, List<String> message) {
        final Optional<PlayerProfile> optionalPlayerProfile = Profiles.getInstance().getPlayerProfileHandler().requestProfile(uuid);

        if (!optionalPlayerProfile.isPresent()) {
            return false;
        }

        if (optionalPlayerProfile.get().getNotifications().size() > 50) {
            optionalPlayerProfile.get().getNotifications().remove(optionalPlayerProfile.get().getNotifications().size()-1);
        }

        Proton.getInstance().getPidginHandler().sendPacket(new NotificationSendPacket(uuid));

        optionalPlayerProfile.get().getNotifications().add(new Notification(uuid, java.lang.System.currentTimeMillis(), message));
        optionalPlayerProfile.get().save();
        return true;
    }

    public static boolean sendFriendRequest(Player sender, UUID target) {

        if (sender.getUniqueId().toString().equalsIgnoreCase(target.toString())) {
            sender.sendMessage(ChatColor.RED + "You can't friend yourself!");
            return false;
        }

        final PlayerProfile senderProfile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(sender.getUniqueId(), sender.getName());

        final Optional<PlayerProfile> targetProfile = Profiles.getInstance().getPlayerProfileHandler().requestProfile(target);

        if (!targetProfile.isPresent()) {
            sender.sendMessage(ChatColor.RED + "That player doesn't have a valid profile!");
            return false;
        }

        if (senderProfile.getFriends().contains(target)) {
            sender.sendMessage(ChatColor.RED + "That person is already your friend!");
            return false;
        }

        if (senderProfile.getFriendRequests().stream().anyMatch(it -> it.toString().equalsIgnoreCase(target.toString()))) {
            acceptFriendRequest(senderProfile, sender, target);
            return true;
        }

        if (targetProfile.get().getFriendRequests().stream().anyMatch(it -> it.toString().equalsIgnoreCase(sender.getUniqueId().toString()))) {
            sender.sendMessage(ChatColor.RED + "You already have an outgoing friend request to " + targetProfile.get().getName() + "!");
            return false;
        }

        Proton.getInstance().getPidginHandler().sendPacket(new FriendRequestSendPacket(sender.getName(), target));

        targetProfile.get().getFriendRequests().add(sender.getUniqueId());
        targetProfile.get().save();

        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Friends");
        sender.sendMessage(ChatColor.GRAY + "You have sent a friend request to " + ChatColor.WHITE + targetProfile.get().getName() + ChatColor.GRAY + "!");
        sender.sendMessage("");
        return true;
    }

    public static boolean acceptFriendRequest(PlayerProfile senderProfile, Player sender, UUID target) {
        if (sender.getUniqueId().toString().equalsIgnoreCase(target.toString())) {
            sender.sendMessage(ChatColor.RED + "You can't friend yourself!");
            return false;
        }

        final Optional<PlayerProfile> targetProfile = Profiles.getInstance().getPlayerProfileHandler().requestProfile(target);

        if (!targetProfile.isPresent()) {
            sender.sendMessage(ChatColor.RED + "That player doesn't have a valid profile!");
            return false;
        }

        if (senderProfile.getFriends().contains(target)) {
            sender.sendMessage(ChatColor.RED + "That person is already your friend!");
            return false;
        }

        if (senderProfile.getFriendRequests().stream().noneMatch(it -> it.toString().equalsIgnoreCase(target.toString()))) {
            sender.sendMessage(ChatColor.RED + "That player has not sent you a friend request!");
            return false;
        }

        senderProfile.getFriends().add(target);
        // incase there is one somehow idk bro shits weird
        senderProfile.getFriendRequests().remove(target);
        senderProfile.save();

        targetProfile.get().getFriends().add(sender.getUniqueId());
        targetProfile.get().getFriendRequests().remove(sender.getUniqueId());
        targetProfile.get().save();

        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Friends");
        sender.sendMessage(ChatColor.GRAY + "You and " + ChatColor.WHITE + targetProfile.get().getName() + ChatColor.GRAY + " are now friends!");
        sender.sendMessage("");

        Proton.getInstance().getPidginHandler().sendPacket(new FriendRequestAcceptPacket(sender.getName(), target));
        return true;
    }

    public static boolean addStatistic(UUID target, StatisticServer statisticServer, StatisticType statisticType, int amount) {
        final Optional<PlayerProfile> optionalPlayerProfile = Profiles.getInstance().getPlayerProfileHandler().requestProfile(target);

        if (!optionalPlayerProfile.isPresent()) {
            return false;
        }

        optionalPlayerProfile.get().getStatistics().putIfAbsent(statisticServer, new Statistics(statisticServer));

        final Statistics statistics = optionalPlayerProfile.get().getStatistics().get(statisticServer);

        if (statistics == null) {
            return false;
        }

        statistics.addStatistic(statisticType, amount);
        optionalPlayerProfile.get().recalculateKillsDeaths();
        optionalPlayerProfile.get().save();
        return true;
    }

}
