package org.cavepvp.profiles.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.impl.PlayerType;

import java.util.*;

@AllArgsConstructor
public class PreferencesMenu extends Menu {
    private PlayerProfile playerProfile;

    @Override
    public boolean isUpdateAfterClick() {
        return true;
    }

    @Override
    public String getTitle(Player player) {
        return "Preferences";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (int i = 0; i < 27; i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14, ""));
        }

        toReturn.put(11, new Button() {
            final PlayerType nextType = Arrays.stream(PlayerType.values()).filter(it -> it.getNumber() == (playerProfile.getPreferences2().getMessages().getNumber()+1)).findFirst().orElse(PlayerType.EVERYONE);

            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lToggle Messages");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "Block players from messaging you");
                toReturn.add("");
                toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Filters");

                Arrays.stream(PlayerType.values()).forEach(it -> toReturn.add(ChatColor.translate("&4&l┃ &f" +
                                (playerProfile.getPreferences2().getMessages().equals(it) ? it.getDisplayName() : ChatColor.stripColor(it.getDisplayName())))));

                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to switch to " + ChatColor.stripColor(nextType.getDisplayName()));
                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BOOK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                playerProfile.getPreferences2().setMessages(nextType);
                playerProfile.save();

                player.sendMessage(ChatColor.GOLD + "Messages: " + nextType.getDisplayName());
            }
        });

        toReturn.put(12, new Button() {
            final PlayerType nextType = Arrays.stream(PlayerType.values()).filter(it -> it.getNumber() == (playerProfile.getPreferences2().getSounds().getNumber()+1)).findFirst().orElse(PlayerType.EVERYONE);

            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lToggle Sounds");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "Toggle the sound you receive from messages");
                toReturn.add("");
                toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Filters");

                Arrays.stream(PlayerType.values()).forEach(it -> toReturn.add(ChatColor.translate("&4&l┃ &f" +
                        (playerProfile.getPreferences2().getSounds().equals(it) ? it.getDisplayName() : ChatColor.stripColor(it.getDisplayName())))));

                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to switch to " + ChatColor.stripColor(nextType.getDisplayName()));
                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NOTE_BLOCK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                playerProfile.getPreferences2().setSounds(nextType);
                playerProfile.save();

                player.sendMessage(ChatColor.GOLD + "Sounds: " + nextType.getDisplayName());
            }
        });

        toReturn.put(13, new Button() {
            final PlayerType nextType = Arrays.stream(PlayerType.values()).filter(it -> it.getNumber() == (playerProfile.getPreferences2().getProfileViewing().getNumber()+1)).findFirst().orElse(PlayerType.EVERYONE);

            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lToggle Profile Viewing");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "Block players from viewing your profile");
                toReturn.add("");
                toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Filters");

                Arrays.stream(PlayerType.values()).forEach(it -> toReturn.add(ChatColor.translate("&4&l┃ &f" +
                        (playerProfile.getPreferences2().getProfileViewing().equals(it) ? it.getDisplayName() : ChatColor.stripColor(it.getDisplayName())))));

                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to switch to " + ChatColor.stripColor(nextType.getDisplayName()));
                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SKULL_ITEM;
            }

            @Override
            public byte getDamageValue(Player player) {
                return (byte) 3;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemBuilder.of(this.getMaterial(player)).data(this.getDamageValue(player)).setLore(this.getDescription(player)).name(this.getName(player)).skull(player.getName()).build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                playerProfile.getPreferences2().setProfileViewing(nextType);
                playerProfile.save();

                player.sendMessage(ChatColor.GOLD + "Profile Viewing: " + nextType.getDisplayName());
            }
        });

        toReturn.put(14, new Button() {
            final PlayerType nextType = Arrays.stream(PlayerType.values()).filter(it -> it.getNumber() == (playerProfile.getPreferences2().getToggleOfflineMessaging().getNumber()+1)).findFirst().orElse(PlayerType.EVERYONE);

            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lToggle Offline Messages");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "Toggle players messaging you whilst you're offline");
                toReturn.add("");
                toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Filters");

                Arrays.stream(PlayerType.values()).forEach(it -> toReturn.add(ChatColor.translate("&4&l┃ &f" +
                        (playerProfile.getPreferences2().getToggleOfflineMessaging().equals(it) ? it.getDisplayName() : ChatColor.stripColor(it.getDisplayName())))));

                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to switch to " + ChatColor.stripColor(nextType.getDisplayName()));
                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.COMMAND;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                playerProfile.getPreferences2().setToggleOfflineMessaging(nextType);
                playerProfile.save();

                player.sendMessage(ChatColor.GOLD + "Offline Messages: " + nextType.getDisplayName());
            }
        });

        toReturn.put(15, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lToggle Friend Requests");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "Toggle receiving friend requests");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&l┃ &fStatus: " + (playerProfile.getPreferences2().isFriendRequests() ? "&aEnabled" : "&cDisabled")));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to " + (!playerProfile.getPreferences2().isFriendRequests() ? "enable" : "disable") + " friend requests");
                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EMERALD;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                playerProfile.getPreferences2().setFriendRequests(!playerProfile.getPreferences2().isFriendRequests());
                playerProfile.save();

                player.sendMessage(ChatColor.translate("&6Friend Requests: " + (playerProfile.getPreferences2().isFriendRequests() ? "&aEnabled" : "&cDisabled")));
            }
        });

        return toReturn;
    }
}
