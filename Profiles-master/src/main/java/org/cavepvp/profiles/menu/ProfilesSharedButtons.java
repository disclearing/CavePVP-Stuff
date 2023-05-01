package org.cavepvp.profiles.menu;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.impl.Brackets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfilesSharedButtons {

    public static Map<Integer, Button> mainButtons(Player player, PlayerProfile playerProfile, Profile profile, MenuType menuType, boolean glass) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(0, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Statistics";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "View your statistics across all servers");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&l┃ &fGlobal Kills: &c" + playerProfile.getTotalKills()));
                toReturn.add(ChatColor.translate("&4&l┃ &fGlobal Deaths: &c" + playerProfile.getTotalDeaths()));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to view all your statistics");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAPER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new StatisticsMenu(profile, playerProfile).openMenu(player);
            }
        });

        toReturn.put(2, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Social Media";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();
                toReturn.add(ChatColor.GRAY + "Release your social media to the public");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&l┃ &fSocials Linked: &c" + playerProfile.getSocialMedia().size() + "/5"));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to view all your social media");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAINTING;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new SocialMediaMenu(profile, playerProfile).openMenu(player);
            }
        });

        toReturn.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() +  "Your Profile";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                final Grant activeGrant = profile.getActiveGrant();

                playerProfile.recalculateKillsDeaths();

                toReturn.add(ChatColor.GRAY + "Manage your profile in this simple system");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&l┃ &fRank: &c" + activeGrant.getRank().getFancyName()));
                if (!activeGrant.isPermanent()) {
                    toReturn.add(ChatColor.translate("&4&l┃ &fRank Duration: &c" + TimeUtils.formatIntoDetailedString((int) (activeGrant.getRemaining()/1000))));
                }
                toReturn.add(ChatColor.translate("&4&l┃ &fVIP Status: &c" + (profile.hasSubscription() ? "&aYes" : "&cNo")));

                final Brackets brackets = Profiles.getInstance().getReputationHandler().findBracket(playerProfile);

                toReturn.add(ChatColor.translate("&4&l┃ &fReputation Bracket: &f" + (brackets.equals(Brackets.UNRANKED) ? ChatColor.RED : brackets.getChatColor()) + brackets.getDisplayName()));
                toReturn.add(ChatColor.translate("&4&l┃ &fCurrent Reputation: &c" + playerProfile.getPlayerReputation() + " RP"));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to view your profile data");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemBuilder.of(Material.SKULL_ITEM).skull(player.getName()).data((byte) 3).name(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Your Profile").setLore(this.getDescription(player)).build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (menuType == MenuType.MAIN_MENU) {
                    player.sendMessage(ChatColor.RED + "This feature is currently disabled!");
                    return;
                }

                new MainMenu(profile, playerProfile).openMenu(player);
            }
        });

        toReturn.put(6, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Friends";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "Manage all of your friends");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&l┃ &fFriends: &c" + playerProfile.getFriends().size()));
                toReturn.add(ChatColor.translate("&4&l┃ &fFriend Requests: &c" + playerProfile.getFriendRequests().size()));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to view all your friends");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemBuilder.of(Material.EYE_OF_ENDER).name(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Friends").setLore(this.getDescription(player)).build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new FriendsMenu(profile, playerProfile).openMenu(player);
            }
        });

        toReturn.put(8, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Notifications";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "View all of your pending notifications");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&l┃ &fUnread Notifications: &c" + playerProfile.getNotifications().size()));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to view all your notifications");

                return toReturn;
            }

            @Override
            public int getAmount(Player player) {
                return playerProfile.getNotifications().isEmpty() ? 1 : playerProfile.getNotifications().size();
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.WATCH;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new NotificationsMenu(profile, playerProfile).openMenu(player);
            }
        });

        if (glass) {
            for (int i = 9; i < 18; i++) {
                toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, ColorUtil.COLOR_MAP.get(profile.getActiveRank().getColor()).getWoolData(), ""));
            }

            for (int i = 45; i < 54; i++) {
                toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, ColorUtil.COLOR_MAP.get(profile.getActiveRank().getColor()).getWoolData(), ""));
            }
        }

        return toReturn;
    }

}
