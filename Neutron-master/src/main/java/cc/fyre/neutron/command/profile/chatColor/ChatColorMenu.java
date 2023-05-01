package cc.fyre.neutron.command.profile.chatColor;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.menu.editor.menu.RankModifyAttributesMenu;
import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

@AllArgsConstructor
public class ChatColorMenu extends Menu {

    @Getter private Profile profile;
    @Getter private Rank activeRank;

    @Override
    public String getTitle(Player player) {
        return "Please choose a color.";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        for (ChatColor chatColor : ChatColor.values()) {

            if (ChatColorCommand.disallowedChatColors.contains(chatColor)) {
                continue;
            }

            if (chatColor.ordinal() > 15) {
                continue;
            }

            toReturn.put(toReturn.size(),new Button() {

                @Override
                public String getName(Player player) {
                    return chatColor + chatColor.name();
                }

                @Override
                public List<String> getDescription(Player player) {

                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add("");
                    toReturn.add(ChatColor.GOLD + "Preview:");
                    toReturn.add(ChatColor.translate(NeutronConstants.formatChatDisplay(player, "Hello world!", chatColor)));

                    if (!player.hasPermission("color." + chatColor.name().toLowerCase()) && !player.hasPermission("color.*")) {
                        toReturn.add("");
                        toReturn.add(ChatColor.RED + "This chatcolor is locked! Purchase this chatcolor at " + Neutron.getInstance().getStore());
                    }
                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.INK_SACK;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return ColorUtil.COLOR_MAP.get(chatColor).getDyeData();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (!player.hasPermission("color." + chatColor.name().toLowerCase()) && !player.hasPermission("color.*")) {
                        player.sendMessage(ChatColor.RED + "No permission.");
                        return;
                    }

                    profile.setChatColor(chatColor);
                    profile.save();
                    player.closeInventory();

                    player.sendMessage(ChatColor.GOLD + "Your chat color has been updated to " + chatColor + ColorUtil.getProperName(chatColor));
                }

            });
        }

        return toReturn;
    }

    @Override
    public void onClose(Player player) {
        profile.save();
    }

}