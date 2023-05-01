package org.cavepvp.profiles.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.profiles.menu.page.ProfilePaginatedMenu;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.PlayerProfileAPI;

import java.util.*;

@AllArgsConstructor
public class FriendRequestsMenu extends ProfilePaginatedMenu {
    private Profile profile;
    private PlayerProfile playerProfile;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Your Friend Requests";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>(ProfilesSharedButtons.mainButtons(player, playerProfile, profile, MenuType.FRIENDS, true));

        toReturn.put(22, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Switch to Friends";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList("", ChatColor.GREEN + "Click to switch to your friend list");
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
                new FriendsMenu(profile, playerProfile).openMenu(player);
            }
        });

        return toReturn;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();
        for (UUID friend : playerProfile.getFriendRequests()) {
            final String name = Neutron.getInstance().getProfileHandler().findDisplayName(friend);

            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.DARK_RED + ChatColor.BOLD.toString() + name;
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Left Click to accept this friend request");
                    toReturn.add(ChatColor.GREEN + "Shift + Right Click to reject this friend request");

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.SKULL_ITEM;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemBuilder.of(this.getMaterial(player)).data((byte)3).setLore(this.getDescription(player)).name(this.getName(player)).skull(name).build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (clickType == ClickType.SHIFT_RIGHT) {
                        player.sendMessage(ChatColor.RED + "Rejected " + name + "'s friend request.");

                        playerProfile.getFriendRequests().remove(friend);
                        playerProfile.save();
                        return;
                    }

                    PlayerProfileAPI.acceptFriendRequest(playerProfile, player, friend);
                }
            });
        }

        return toReturn;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 27;
    }
}
