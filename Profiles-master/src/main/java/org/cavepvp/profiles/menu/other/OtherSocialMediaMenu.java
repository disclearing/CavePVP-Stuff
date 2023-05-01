package org.cavepvp.profiles.menu.other;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.profiles.menu.MenuType;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.impl.socialmedia.SocialMediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class OtherSocialMediaMenu extends Menu {
    private PlayerProfile playerProfile;
    private Profile profile;

    @Override
    public String getTitle(Player player) {
        return "Social Media";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>(ViewingOtherSharedButtons.mainButtons(player, playerProfile, profile, MenuType.STATISTICS));

        int i = 29;

        for (SocialMediaType value : SocialMediaType.values()) {
            final String link = playerProfile.getSocialMedia().getOrDefault(value, "N/A");

            toReturn.put(i, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.DARK_RED + ChatColor.BOLD.toString() + value.getDisplayName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add("");
                    if (value == SocialMediaType.DISCORD) {
                        toReturn.add(ChatColor.translate("&4&l┃ &fDiscord Tag: &c" + link));
                    } else {
                        toReturn.add(ChatColor.translate("&4&l┃ &fLink: &c" + link));
                    }
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Left Click to view this link");

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.SKULL_ITEM;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemBuilder.of(Material.SKULL_ITEM).skull(value.getSkullOwner()).data((byte) 3).name(ChatColor.DARK_RED + ChatColor.BOLD.toString() + value.getDisplayName()).setLore(this.getDescription(player)).build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.sendMessage("");
                    player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + playerProfile.getName() + "'s " + value.getDisplayName());
                    player.sendMessage(ChatColor.GRAY + link);
                    player.sendMessage("");
                }
            });

            i++;
        }

        return toReturn;
    }
}
