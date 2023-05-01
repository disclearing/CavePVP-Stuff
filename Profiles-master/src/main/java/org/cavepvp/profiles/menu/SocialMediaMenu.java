package org.cavepvp.profiles.menu;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.impl.socialmedia.SocialMediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class SocialMediaMenu extends Menu {
    private Profile profile;
    private PlayerProfile playerProfile;

    @Override
    public String getTitle(Player player) {
        return "Social Media";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>(ProfilesSharedButtons.mainButtons(player, playerProfile, profile, MenuType.SOCIAL_MEDIA, true));

        int i = 29;

        for (SocialMediaType value : SocialMediaType.values()) {
            toReturn.put(i, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.DARK_RED + ChatColor.BOLD.toString() + value.getDisplayName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    final String link = playerProfile.getSocialMedia().getOrDefault(value, "N/A");

                    toReturn.add("");

                    if (value == SocialMediaType.DISCORD) {
                        toReturn.add(ChatColor.translate("&4&l┃ &fDiscord Tag: &c" + link));
                    } else {
                        toReturn.add(ChatColor.translate("&4&l┃ &fLink: &c" + link));
                    }
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Left Click to change this link");
                    toReturn.add(ChatColor.GREEN + "Right Click to remove this link");

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
                    if (clickType.name().contains("RIGHT")) {
                        player.sendMessage(ChatColor.RED + "Removed social media type " + value.getDisplayName() + ".");
                        playerProfile.getSocialMedia().remove(value);
                        playerProfile.save();
                        return;
                    }

                    player.closeInventory();

                    player.beginConversation(new ConversationFactory(Profiles.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
                        public String getPromptText(ConversationContext context) {
                            return "§6Type in chat your " + value.getDisplayName() + " link. §7[TYPE CANCEL TO CANCEL]";
                        }

                        @Override
                        public Prompt acceptInput(ConversationContext cc, String s) {
                            final Conversable conversable = cc.getForWhom();

                            if (s.equalsIgnoreCase("cancel")) {
                                conversable.sendRawMessage(ChatColor.RED + "Cancelled setting social media type.");
                                openMenu(player);
                                return END_OF_CONVERSATION;
                            }

                            final String link = s.replace("https://", "").replace("http://", "").replace("www.", "");

                            if (value != SocialMediaType.DISCORD && !link.startsWith(value.getMustStartWith())) {
                                conversable.sendRawMessage(ChatColor.RED + "Invalid " + value.getDisplayName() + " link!");
                                conversable.sendRawMessage(ChatColor.GRAY + "Proper Example: " + ChatColor.WHITE + value.getExample());
                                openMenu(player);
                                return END_OF_CONVERSATION;
                            }

                            if (value == SocialMediaType.DISCORD && !link.contains("#")) {
                                conversable.sendRawMessage(ChatColor.RED + "Invalid Discord Tag!");
                                conversable.sendRawMessage(ChatColor.GRAY + "Proper Example: " + ChatColor.WHITE + "SimplyTrash#2040");
                                openMenu(player);
                                return END_OF_CONVERSATION;
                            }

                            playerProfile.getSocialMedia().put(value, s);
                            playerProfile.save();

                            conversable.sendRawMessage(ChatColor.translate("&6Set your social media link to &f" + s));
                            openMenu(player);
                            return Prompt.END_OF_CONVERSATION;
                        }
                    }).withLocalEcho(false).withEscapeSequence("/null").withTimeout(120).buildConversation(player));
                }
            });

            i++;
        }

        return toReturn;
    }
}
