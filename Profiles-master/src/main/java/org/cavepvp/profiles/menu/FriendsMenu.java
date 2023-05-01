package org.cavepvp.profiles.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.UUIDUtils;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.menu.page.ProfilePaginatedMenu;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;

import java.util.*;

@AllArgsConstructor
public class FriendsMenu extends ProfilePaginatedMenu {
    private Profile profile;
    private PlayerProfile playerProfile;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Your Friends";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>(ProfilesSharedButtons.mainButtons(player, playerProfile, profile, MenuType.FRIENDS, true));

        toReturn.put(22, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Switch to Friend Requests";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList("", ChatColor.GREEN + "Click to switch to your friend requests");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EMERALD;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new FriendRequestsMenu(profile, playerProfile).openMenu(player);
            }
        });

        return toReturn;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (UUID friend : playerProfile.getFriends()) {
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
                    toReturn.add(ChatColor.GREEN + "Click to view " + ChatColor.WHITE + ChatColor.stripColor(name) + "'s" + ChatColor.GREEN + " profile");
                    toReturn.add(ChatColor.GREEN + "Shift + Right Click to remove this friend");

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
                        final PlayerProfile targetProfile = Profiles.getInstance().getPlayerProfileHandler().requestProfile(friend).orElse(null);

                        if (targetProfile == null) {
                            player.sendMessage(ChatColor.RED + "That player's profile could not load!");
                            return;
                        }

                        player.sendMessage(ChatColor.RED + "Removed " + name + ChatColor.RED + " as a friend.");

                        targetProfile.getFriends().remove(player.getUniqueId());
                        targetProfile.getFriendRequests().remove(player.getUniqueId());
                        targetProfile.save();

                        playerProfile.getFriends().remove(friend);
                        playerProfile.getFriendRequests().remove(friend);
                        playerProfile.save();
                        return;
                    }

                    player.closeInventory();
                    player.chat("/profiles check " + UUIDUtils.name(friend));
                }
            });
        }

        return toReturn;
    }
}
