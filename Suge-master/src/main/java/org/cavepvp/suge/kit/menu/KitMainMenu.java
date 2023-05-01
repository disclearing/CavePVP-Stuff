package org.cavepvp.suge.kit.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.data.Category;
import org.cavepvp.suge.kit.data.Kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitMainMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "Choose a category...";
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
                return ChatColor.translate("&4&lKits");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("");
                toReturn.add(ChatColor.translate("&4❙ &fKits Owned: &c" + Suge.getInstance().getKitHandler().findKitsOwned(player).size()));
                toReturn.add("");
                toReturn.add(ChatColor.translate("&aClick to view all kits owned by you"));

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BOOK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new KitOwnedMenu().openMenu(player);
            }
        });

        for (Category category : Category.values()) {

            if (category.equals(Category.NONE)) {
                continue;
            }

            final List<Kit> containsKits = category.findKits();

            toReturn.put(category.getSlot()-1, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.translate(category.getDisplayName());
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    final List<Kit> kitsOwned = Suge.getInstance().getKitHandler().findKitsOwned(player, category);

                    toReturn.add("");
                    toReturn.add(ChatColor.translate(category.getPrimaryColor() + "❙ " + ChatColor.WHITE + "Kits Owned: " + category.getPrimaryColor() + kitsOwned.size()));
                    toReturn.add("");
                    toReturn.add(ChatColor.translate(category.getPrimaryColor() + "&lKits:"));
                    for (Kit containsKit : containsKits) {
                        toReturn.add(category.getPrimaryColor() + "❙ " + ChatColor.WHITE + (kitsOwned.contains(containsKit) ? ChatColor.translate(containsKit.getDisplayName()) : ChatColor.stripColor(ChatColor.translate(containsKit.getDisplayName()))));
                    }
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to view all " + category.getFlatName() + " kits.");

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return category.getDisplayMaterial();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    new KitCategoryMenu(category).openMenu(player);
                }
            });

        }

        return toReturn;
    }
}
