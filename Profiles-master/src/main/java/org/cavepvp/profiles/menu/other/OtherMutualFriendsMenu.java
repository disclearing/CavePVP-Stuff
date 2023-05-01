package org.cavepvp.profiles.menu.other;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.menu.MenuType;
import org.cavepvp.profiles.menu.page.ProfilePaginatedMenu;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class OtherMutualFriendsMenu extends ProfilePaginatedMenu {
    private PlayerProfile playerProfile;
    private Profile profile;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Mutual Friends";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        return new HashMap<>(ViewingOtherSharedButtons.mainButtons(player, playerProfile, profile, MenuType.FRIENDS));
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        final PlayerProfile senderProfile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());

        if (senderProfile.getFriends().isEmpty()) {
            return toReturn;
        }

        for (UUID friend : playerProfile.getFriends().stream().filter(it -> senderProfile.getFriends().stream().anyMatch(second -> it.toString().equalsIgnoreCase(second.toString()))).collect(Collectors.toList())) {
            final String name = Neutron.getInstance().getProfileHandler().findDisplayName(friend);

            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.DARK_RED + ChatColor.BOLD.toString() + name;
                }

                @Override
                public List<String> getDescription(Player player) {
                    return null;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.SKULL_ITEM;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemBuilder.of(this.getMaterial(player)).data((byte)3).name(this.getName(player)).skull(name).build();
                }
            });
        }

        return toReturn;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 18;
    }
}
