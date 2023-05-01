package net.frozenorb.foxtrot.gameplay.ability.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartnerItemMainMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "Partner Items";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (int i = 0; i < 27; i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)14, ""));
        }

        toReturn.put(10, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)7, ""));
        toReturn.put(16, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)7, ""));

        for (Category value : Category.values()) {

            if (value == Category.KIT_MAP || value == Category.PORTABLE_BARD) {
                continue;
            }

            toReturn.put(value.getSlot(), new Button() {
                @Override
                public String getName(Player player) {
                    return value.getDisplayName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to view all ability items in this category");

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return value.getDisplayItem();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    new PartnerItemMenu(value).openMenu(player);
                }
            });
        }


        return toReturn;
    }
}
