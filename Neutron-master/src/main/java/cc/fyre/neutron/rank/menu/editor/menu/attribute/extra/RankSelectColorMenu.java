package cc.fyre.neutron.rank.menu.editor.menu.attribute.extra;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public class RankSelectColorMenu extends Menu {

    @Getter private Rank rank;

    @Override
    public String getTitle(Player player) {
        return "Please choose a color.";
    }

    @Override
    public Map<Integer,Button> getButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        for (ChatColor chatColor : ChatColor.values()) {

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
                    toReturn.add(ChatColor.GOLD + "Display: " + chatColor + rank.getName());

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
                public void clicked(Player player,int slot,ClickType clickType) {
                    rank.setColor(chatColor);
                    rank.save();
                    new RankModifyAttributesMenu(rank).openMenu(player);
                }

            });
        }

        return toReturn;
    }

    @Override
    public void onClose(Player player) {
        this.rank.save();
    }

}
