package net.frozenorb.foxtrot.gameplay.armorclass.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.Category;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class ArmorClassesMenu extends Menu {
    private Category category;

    @Override
    public String getTitle(Player player) {
        return "Armor Classes";
    }

    @Override
    public int size(Player player) {
        return 27;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&9&lArmor Classes");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("");
                toReturn.add(ChatColor.translate("&bWhat are Armor Classes?"));
                toReturn.add(ChatColor.translate("&9❙ &fEach armor classes provides unique traits."));
                toReturn.add(ChatColor.translate("&9❙ &fSuit up to enhance your play-style."));
                toReturn.add("");
                toReturn.add(ChatColor.translate("&7Hover over each armor classes to view its use"));

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BOOK;
            }
        });

        for (ArmorClass armorClass : Foxtrot.getInstance().getArmorClassHandler().getArmorClasses()) {
            if (!armorClass.getCategory().equals(category)) {
                continue;
            }

            toReturn.put(armorClass.getSlot(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.translate(armorClass.getDisplayName());
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add("");
                    toReturn.add(ChatColor.translate(armorClass.getChatColor() + ChatColor.BOLD.toString() + "Perks"));
                    for (String perk : armorClass.getPerks()) {
                        toReturn.add(ChatColor.translate(armorClass.getChatColor() + "❙ &f" + perk));
                    }
                    toReturn.add("");
                    toReturn.add(ChatColor.translate("&aClick to preview this armor class"));

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return armorClass.getDisplayItem();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    new PreviewMenu(armorClass).openMenu(player);
                }
            });
        }

        return toReturn;
    }
}
