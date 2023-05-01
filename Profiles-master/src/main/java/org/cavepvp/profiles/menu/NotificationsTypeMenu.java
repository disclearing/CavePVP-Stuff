package org.cavepvp.profiles.menu;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.impl.NotificationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class NotificationsTypeMenu extends Menu {
    private Profile profile;
    private PlayerProfile playerProfile;

    @Override
    public String getTitle(Player player) {
        return "Your Statistics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>(ProfilesSharedButtons.mainButtons(player, playerProfile, profile, MenuType.STATISTICS, true));

        int i = 29;

        if (playerProfile.getEnabledNotificatons() == null) {
            playerProfile.setEnabledNotificatons(new ArrayList<>());
            playerProfile.save();
        }

        for (NotificationType notificationType : NotificationType.values()) {

            toReturn.put(i, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.DARK_RED + ChatColor.BOLD.toString() + notificationType.getDisplayName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add(ChatColor.GRAY + notificationType.getSlogan());
                    toReturn.add("");
                    toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Notified Actions");
                    for (String feature : notificationType.getFeatures()) {
                        toReturn.add(ChatColor.translate("&4&l┃ &f" + feature));
                    }
                    toReturn.add("");
                    toReturn.add(ChatColor.translate("&4&l┃ &fStatus: &c" + (playerProfile.getEnabledNotificatons().contains(notificationType) ? "&aEnabled" : "&cDisabled")));
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to toggle this notification");

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return notificationType.getMaterial();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (playerProfile.getEnabledNotificatons().contains(notificationType)) {
                        player.sendMessage(ChatColor.RED + "You have disabled all " + ChatColor.WHITE + notificationType.getDisplayName() + ChatColor.RED + " notifications!");

                        playerProfile.getEnabledNotificatons().remove(notificationType);
                        playerProfile.save();
                        return;
                    }

                    player.sendMessage(ChatColor.GREEN + "You have enabled all " + ChatColor.WHITE + notificationType.getDisplayName() + ChatColor.GREEN + " notifications!");

                    playerProfile.getEnabledNotificatons().add(notificationType);
                    playerProfile.save();
                }
            });

            i++;
        }

        return toReturn;
    }
}
