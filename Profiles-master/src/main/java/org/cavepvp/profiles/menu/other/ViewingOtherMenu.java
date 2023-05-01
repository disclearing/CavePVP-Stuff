package org.cavepvp.profiles.menu.other;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.server.ServerProfile;
import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.PlayerProfileAPI;
import org.cavepvp.profiles.playerProfiles.impl.Brackets;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ViewingOtherMenu extends Menu {
    private Profile profile;
    private PlayerProfile playerProfile;

    @Override
    public String getTitle(Player player) {
        return playerProfile.getName() + "'s Profile";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        final PlayerProfile senderProfile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());

        toReturn.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return profile.getFancyName() + "'s Profile";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                final ServerProfile serverProfile = profile.getServerProfile();

                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&l┃ &fRank: &c" + profile.getActiveRank().getFancyName()));
                toReturn.add(ChatColor.translate("&4&l┃ &fVIP Status: &c" + (profile.hasSubscription() ? "&aYes" : "&cNo")));

                toReturn.add("");
                if (serverProfile.isOnline()) {
                    toReturn.add(ChatColor.translate("&4&l┃ &fStatus: &aOnline"));
                    toReturn.add(ChatColor.translate("&4&l┃ &fServer: &c" + serverProfile.getCurrentServer()));
                } else {
                    toReturn.add(ChatColor.translate("&4&l┃ &fStatus: &cOffline"));
                    toReturn.add(ChatColor.translate("&4&l┃ &fLast Seen: &c" + serverProfile.getLastSeenString()));
                    toReturn.add(ChatColor.translate("&4&l┃ &fLast Server: &c" + serverProfile.getLastServer()));
                }

                final Brackets brackets = Profiles.getInstance().getReputationHandler().findBracket(playerProfile);

                toReturn.add(ChatColor.translate("&4&l┃ &fReputation Bracket: &f" + (brackets.equals(Brackets.UNRANKED) ? ChatColor.RED : brackets.getChatColor()) + brackets.getDisplayName()));
                toReturn.add(ChatColor.translate("&4&l┃ &fCurrent Reputation: &c" + playerProfile.getPlayerReputation()) + " RP");
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to view their website profile");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SKULL_ITEM;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 3;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemBuilder.of(this.getMaterial(player)).name(this.getName(player)).data(this.getDamageValue(player)).setLore(this.getDescription(player)).skull(profile.getName()).build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.sendMessage("");
                player.sendMessage(ChatColor.RED + "View their website profile: " + ChatColor.WHITE + "https://cavepvp.org/player/" + profile.getName());
                player.sendMessage("");
            }
        });

        for (int i = 9; i < 18; i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, ColorUtil.COLOR_MAP.get(profile.getActiveRank().getColor()).getWoolData(), ""));
        }

        toReturn.put(28, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + profile.getName() + "'s Statistics";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();
                toReturn.add(ChatColor.GRAY + "View all of " + profile.getName() + "'s statistics");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&l┃ &fTotal Kills: &c" + playerProfile.getTotalKills()));
                toReturn.add(ChatColor.translate("&4&l┃ &fTotal Deaths: &c" + playerProfile.getTotalDeaths()));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to view " + profile.getName() + "'s statistics");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAPER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new OtherStatisticsMenu(playerProfile, profile).openMenu(player);
            }
        });

        toReturn.put(30, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + profile.getName() + "'s Mutual Friends";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<UUID> mutuals = playerProfile.getFriends().stream().filter(it -> playerProfile.getFriends().stream().anyMatch(second -> it.toString().equalsIgnoreCase(second.toString()))).collect(Collectors.toList());

                final List<String> toReturn = new ArrayList<>();
                toReturn.add(ChatColor.GRAY + "View all of the mutual friends you have");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&l┃ &fMutual Friends: &c" + mutuals.size()));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to view " + profile.getName() + "'s mutual friends");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SKULL_ITEM;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 3;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new OtherMutualFriendsMenu(playerProfile, profile).openMenu(player);
            }
        });

        toReturn.put(32, new Button() {
            @Override
            public String getName(Player player) {
                if (playerProfile.getFriends().contains(player.getUniqueId())) {
                    return ChatColor.DARK_RED + "Remove " + profile.getName() + " as a friend";
                }

                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Add " + profile.getName() + " as a Friend";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                if (playerProfile.getFriends().contains(player.getUniqueId())) {
                    toReturn.add(ChatColor.GRAY + "Remove this player from your friend list");
                    toReturn.add("");
                    toReturn.add(ChatColor.translate("&4&l┃ &fFriends: &c" + playerProfile.getFriends().size()));
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Shift Right Click to remove " + profile.getName() + " as a friend");
                } else {
                    toReturn.add(ChatColor.GRAY + "Add this player to your friend list");
                    toReturn.add("");
                    toReturn.add(ChatColor.translate("&4&l┃ &fFriends: &c" + playerProfile.getFriends().size()));
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to add " + profile.getName() + " as a friend");
                }

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                if (playerProfile.getFriends().contains(player.getUniqueId())) {
                    return Material.REDSTONE;
                }

                return Material.EMERALD;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (clickType == ClickType.SHIFT_RIGHT) {
                    final PlayerProfile targetProfile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());

                    player.sendMessage(ChatColor.RED + "Removed " + playerProfile.getName() + ChatColor.RED + " as a friend.");

                    playerProfile.getFriends().remove(player.getUniqueId());
                    playerProfile.save();

                    targetProfile.getFriends().remove(playerProfile.getUuid());
                    targetProfile.save();
                    return;
                }

                PlayerProfileAPI.sendFriendRequest(player, playerProfile.getUuid());
            }
        });

        toReturn.put(34, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + profile.getName() + "'s Social Media";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();
                toReturn.add(ChatColor.GRAY + "View all of " + profile.getName() + "'s social media");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&l┃ &fSocials Posted: &c" + playerProfile.getSocialMedia().size()));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to view " + profile.getName() + "'s social media");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAINTING;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new OtherSocialMediaMenu(playerProfile, profile).openMenu(player);
            }
        });

        for (int i = 45; i < 54; i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, ColorUtil.COLOR_MAP.get(profile.getActiveRank().getColor()).getWoolData(), ""));
        }

        return toReturn;
    }

    @Override
    public int size(Player player) {
        return 54;
    }
}
