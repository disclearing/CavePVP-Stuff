package org.cavepvp.profiles.menu;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.cavepvp.profiles.menu.page.ProfilePaginatedMenu;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.impl.Notification;

import java.util.*;

@AllArgsConstructor
public class NotificationsMenu extends ProfilePaginatedMenu {
    private Profile profile;
    private PlayerProfile playerProfile;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Your Notifications";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>(ProfilesSharedButtons.mainButtons(player, playerProfile, profile, MenuType.FRIENDS, true));

        toReturn.put(20, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Read All";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList("", ChatColor.GREEN + "Click to mark all your notifications as read");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.REDSTONE;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.sendMessage(ChatColor.GREEN + "Marked all your notifications as read!");
                playerProfile.getNotifications().clear();
                playerProfile.save();
            }
        });

        toReturn.put(24, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Notification Settings";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList("", ChatColor.GREEN + "Click to view all notification settings");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.GLOWSTONE_DUST;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new NotificationsTypeMenu(profile, playerProfile).openMenu(player);
            }
        });

        return toReturn;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        int amount = 0;

        final List<Notification> sortedNotifications = playerProfile.getNotifications();
        sortedNotifications.sort(Comparator.comparingLong(Notification::getDateSent).reversed());

        for (Notification notification : sortedNotifications) {
            amount++;
            int finalAmount = amount;
            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.DARK_RED + "Notification #" + finalAmount + " - " + TimeUtils.formatIntoCalendarString(new Date(notification.getDateSent()));
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();
                    for (String notificationText : notification.getNotification()) {
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "┃ " + ChatColor.WHITE + ChatColor.translate(notificationText));
                    }
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to set this notification as read");

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.PAPER;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.sendMessage(ChatColor.GREEN + "Marked this notification as read!");

                    playerProfile.getNotifications().remove(notification);
                    playerProfile.save();
                }
            });
        }

        return toReturn;
    }
}
